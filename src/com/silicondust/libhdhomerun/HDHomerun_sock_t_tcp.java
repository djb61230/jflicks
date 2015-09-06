package com.silicondust.libhdhomerun;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import com.silicondust.libhdhomerun.HDHomerun_Sock.hdhomerun_sock_t;

public class HDHomerun_sock_t_tcp implements hdhomerun_sock_t {
		SocketChannel channel;
		boolean mValid = false;
		
		public HDHomerun_sock_t_tcp(SocketAddress remoteAddr, int sendBufferSize, int recBufferSize, int timeout) throws IOException {
		
			channel = SocketChannel.open();
			channel.configureBlocking(false);
			if(timeout == 0)
				timeout = HDHomerun_Sock.DEFAULT_NONBLOCKING_TIMEOUT;
			channel.socket().setSoTimeout(timeout);
			if(sendBufferSize > 0)
				channel.socket().setSendBufferSize(sendBufferSize);
			if(recBufferSize > 0)
				channel.socket().setReceiveBufferSize(recBufferSize);
			
			channel.connect(remoteAddr);
			while(!channel.finishConnect())
			{
				if(!channel.isConnectionPending())
					break;
			}
			mValid = channel.isConnected();			
		}
		
		@Override
		public boolean addGroup(InetAddress multicastIP) {
			return false;
		}
		
		@Override
		public boolean dropGroup(InetAddress multicastIP) {
			return false;
		}
		
		@Override
		public boolean isValid() {
			
			return mValid;
		}
		
		@Override
		public void closeSocket() throws Exception{

			channel.close();			
		}
		
		@Override
		public SocketAddress getSocketAddr() {
			return channel.socket().getLocalSocketAddress();
		}
		
		@Override
		public int getSocketPort() {
			return channel.socket().getPort();
		}

		@Override
		public int send(byte[] data, int startIndex, int length, int timeout) throws IOException{
		
			int oldtimeout = channel.socket().getSoTimeout();
			if(timeout != oldtimeout) {
				if(timeout == 0)
					timeout = HDHomerun_Sock.DEFAULT_NONBLOCKING_TIMEOUT;
				channel.socket().setSoTimeout(timeout);
			}
			int packetSize = channel.socket().getSendBufferSize();
			int bytesWritten = 0;
			int bytesLeft = length;
			while(true) {
			
				if(bytesLeft < packetSize)
					packetSize = bytesLeft;
				ByteBuffer buff = ByteBuffer.wrap(data, startIndex + bytesWritten, packetSize);
				channel.write(buff);

				bytesLeft -= packetSize;
				bytesWritten += packetSize;
				
				if(bytesLeft == 0)
					break;
			}
			
		
			if(timeout != oldtimeout)
				channel.socket().setSoTimeout(oldtimeout);
			
			return bytesWritten;
		}
		
		@Override
		public int sendto(byte[] data, int startIndex, int length, InetSocketAddress remoteAddr, int timeout) throws IOException {

			throw new IOException();
		}
		
		@Override
		public InetSocketAddress recvfrom(byte[] data, int start, int length[], int timeout)  throws IOException{
			
			throw new IOException();
		}
			
		@Override
		public boolean recv(byte[] data, int start, int length[], int timeout) throws IOException{
			
			long endTime = HDHomerun_OS.getcurrenttime() + timeout;
			int packetSize = channel.socket().getReceiveBufferSize();
			if(length[0] < packetSize)
				packetSize = length[0];
			while (true) {

				ByteBuffer buff = ByteBuffer.wrap(data, start, length[0]);
				int ret = channel.read(buff);
				if (ret > 0) {
					length[0] = ret;
					return true;
				}

				if(endTime <= HDHomerun_OS.getcurrenttime())
					return false;
				
			}
		}
	

}
