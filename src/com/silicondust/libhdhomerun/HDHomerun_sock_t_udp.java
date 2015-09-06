package com.silicondust.libhdhomerun;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;

import com.silicondust.libhdhomerun.HDHomerun_Sock.hdhomerun_sock_t;


public class HDHomerun_sock_t_udp implements hdhomerun_sock_t {
//	DatagramChannel channel = null;;
	MulticastSocket multiSocket = null;
	private boolean mValid = false;

	public HDHomerun_sock_t_udp(SocketAddress socketAddr, int sendBufferSize, int recBufferSize, boolean allowReuse) throws Exception {
		
		multiSocket = new MulticastSocket(socketAddr);
		multiSocket.setBroadcast(true);
		multiSocket.setReuseAddress(allowReuse);
		if(sendBufferSize > 0)
			multiSocket.setSendBufferSize(sendBufferSize);
		if(recBufferSize > 0)
			multiSocket.setReceiveBufferSize(recBufferSize);
		mValid = multiSocket.isBound();
	}
	
	@Override
	public boolean addGroup(InetAddress groupAddr) {
		
		try {
			multiSocket.joinGroup(groupAddr);
			return true;
		}
		catch (IOException e) {
			return false;
		}
	}
	
	@Override
	public boolean dropGroup(InetAddress multicastIP) {
		try {
			multiSocket.leaveGroup(multicastIP);
			return true;
		}
		catch (IOException e) {
			return false;
		}
	}
	
	@Override
	public boolean isValid() {
		
		return mValid;
	}
	
	@Override
	public void closeSocket() throws Exception
	{
		multiSocket.close();
	}
	public SocketAddress getSocketAddr() {
		return multiSocket.getLocalSocketAddress();
	}
	
	@Override
	public int getSocketPort() {
		return multiSocket.getPort();
	}
	
	@Override
	public int send(byte[] data, int startIndex, int length, int timeout) throws IOException{
	
		throw new IOException();
	}
	
	@Override
	public int sendto(byte[] data, int startIndex, int length, InetSocketAddress remoteAddr, int timeout) throws IOException {
		
		int oldtimeout = multiSocket.getSoTimeout();
		if(timeout != oldtimeout) {
			if(timeout == 0)
				timeout = HDHomerun_Sock.DEFAULT_NONBLOCKING_TIMEOUT;
			multiSocket.setSoTimeout(timeout);
		}
		
		int bytesWritten = 0;
		int bytesLeft = length;
		int packetSize = multiSocket.getSendBufferSize();			
		while(true) {				
			
			if(bytesLeft < packetSize)
				packetSize = bytesLeft;
			
			DatagramPacket p = new DatagramPacket(data, startIndex + bytesWritten, packetSize, remoteAddr);
			multiSocket.send(p);
			bytesWritten += packetSize;
			bytesLeft -= packetSize;
			
			if(bytesLeft == 0)
				break;
		}

		if(timeout != oldtimeout)
			multiSocket.setSoTimeout(oldtimeout);
		
		return bytesWritten;
	}
	
	@Override
	public boolean recv(byte[] data, int start, int length[], int timeout) throws IOException{
		
		long endTime = HDHomerun_OS.getcurrenttime() + timeout;
		int packetSize = multiSocket.getReceiveBufferSize();
		if(length[0] < packetSize)
			packetSize = length[0];
		while (true) {

			DatagramPacket p = new DatagramPacket(data, start, length[0]);
			multiSocket.receive(p);
			int ret = p.getOffset();
			if (ret > 0) {
				length[0] = ret;
				return true;
			}

			if(endTime <= HDHomerun_OS.getcurrenttime())
				return false;
			}
		}

	@Override
	public InetSocketAddress recvfrom(byte[] data, int start, int length[], int timeout) throws IOException 
	{
		int packetSize = multiSocket.getReceiveBufferSize();
		multiSocket.setSoTimeout(10);
		if(length[0] < packetSize)
			packetSize = length[0];
		while (true) {

			DatagramPacket p = new DatagramPacket(data, start, length[0]);
			try {
				multiSocket.receive(p);
			}

			catch (SocketException e) {
				e.printStackTrace();
			}
			catch (IOException e) {
				throw e;
			}
			int ret = p.getOffset();
			if (ret > 0) {
				InetSocketAddress remoteAddr = (InetSocketAddress)p.getSocketAddress();
				length[0] = ret;
				return remoteAddr;
			}
		}
	}

}
