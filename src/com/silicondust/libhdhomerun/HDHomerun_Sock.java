package com.silicondust.libhdhomerun;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

public class HDHomerun_Sock {

	public static final int DEFAULT_NONBLOCKING_TIMEOUT = 1;
	public static class hdhomerun_local_ip_info_t {
		public int ip_addr; // uint32_t 
		public int subnet_mask; // uint32_t 
	};	
	
	public static String IPAddr_IntToString(int ip) {
		
		byte[] ipBytes = IPAddr_IntToBytes(ip);
		int[] ipInts = new int[4];
		ipInts[0] = ipBytes[0];
		if(ipInts[0] < 0)
			ipInts[0] += 256;
		ipInts[1] = ipBytes[1];
		if(ipInts[1] < 0)
			ipInts[1] += 256;
		ipInts[2] = ipBytes[2];
		if(ipInts[2] < 0)
			ipInts[2] += 256;
		ipInts[3] = ipBytes[3];
		if(ipInts[3] < 0)
			ipInts[3] += 256;
		
		return String.format("%d.%d.%d.%d", ipInts[0], ipInts[1], ipInts[2], ipInts[3]);
	}

	public static byte[] IPAddr_IntToBytes(int ip) {
	
		byte[] ret = new byte[4];
		ret[0] = (byte) (ip >> 24);
		ret[1] = (byte) (ip >> 16);
		ret[2] = (byte) (ip >> 8);
		ret[3] = (byte) (ip >> 0);
		
		return ret;
	}
	
	
	public static int IPAddr_BytesToInt(byte[] ip) {
	
		int ret = 0;
		int byte0 = (HDHomerun_OS.getRealUByteVal(ip[0]) << 24);
		int byte1 = (HDHomerun_OS.getRealUByteVal(ip[1]) << 16);
		int byte2 = (HDHomerun_OS.getRealUByteVal(ip[2]) << 8);
		int byte3 = (HDHomerun_OS.getRealUByteVal(ip[3]) << 0);
		
		ret = byte0 | byte1 | byte2 | byte3;
    	
    	return ret;
	}
	
	public static final int HDHOMERUN_SOCK_INVALID = -1;

	private hdhomerun_sock_t mSocket;
	private Exception  mLastError = new Exception();
	
	public interface hdhomerun_sock_t {
		
		public boolean isValid();
		void closeSocket()  throws Exception;
		
		public SocketAddress getSocketAddr();
		public int getSocketPort();
		
		public boolean addGroup(InetAddress multicastIP);
		public boolean dropGroup(InetAddress multicastIP);
		
		
		public int send(byte[] data, int startIndex, int length, int timeout) throws IOException;
		public int sendto(byte[] data, int startIndex, int length, InetSocketAddress remoteAddr, int timeout) throws IOException;
		
		boolean recv(byte[] data, int start, int length[], int timeout) throws IOException;
		InetSocketAddress recvfrom(byte[] data, int start, int length[], int timeout) throws IOException;
	}
	
	// UPD
	public HDHomerun_Sock(int local_addr, int local_port, int sendBufferSize, int recBufferSize, boolean allowReuse) throws Exception {
	
		InetAddress inetAddr = InetAddress.getByAddress(IPAddr_IntToBytes(local_addr));
		InetSocketAddress localAddr = new InetSocketAddress(inetAddr, local_port);
		mSocket = (hdhomerun_sock_t) new HDHomerun_sock_t_udp(localAddr, sendBufferSize, recBufferSize, allowReuse);
	}
	
	// TCP
	public HDHomerun_Sock(int remote_addr, int remote_port, int sendBufferSize, int recBufferSize, int timeout) throws IOException {
			
		InetAddress inetAddr = InetAddress.getByAddress(IPAddr_IntToBytes(remote_addr));
		InetSocketAddress remoteAddr = new InetSocketAddress(inetAddr, remote_port);
		mSocket = (hdhomerun_sock_t) new HDHomerun_sock_t_tcp(remoteAddr, sendBufferSize, recBufferSize, timeout);
		
	}
	
	private static int ShiftSubnetBitsInt(int bitShift) {
		
		int ret = 0;
		for(int i = 0; i < bitShift; ++i) {
			ret |= (1 << (31 - i));
		}
		
		return ret;
	}
	// hdhomerun_local_ip_info - how many adapters have ip addresses
	public static int hdhomerun_local_ip_info(hdhomerun_local_ip_info_t ip_info_list[], int max_count)
	{
		int count = 0;
		
    	try {
    		for(Enumeration<NetworkInterface> list = NetworkInterface.getNetworkInterfaces(); list.hasMoreElements();)
			{
			 	NetworkInterface i = list.nextElement();
			 	for(Enumeration<InetAddress> adds = i.getInetAddresses(); adds.hasMoreElements(); ) {
			    	InetAddress a = adds.nextElement(); 
			    	
			    	int subnet = ShiftSubnetBitsInt(24);
			    	if(a.isLoopbackAddress() || a.getHostAddress().equals("255.255.255.255") || a.getHostAddress().equals("0.0.0.0"))
			    		continue;
			    	
			    	// only ipv4
			    	if(a.getAddress().length > 4)
			    		continue;
			    	
			    	ip_info_list[count] = new hdhomerun_local_ip_info_t();
			    	ip_info_list[count].ip_addr = IPAddr_BytesToInt(a.getAddress());
			    	ip_info_list[count++].subnet_mask = subnet;
			    }
			}
		} catch (SocketException e) {
			e.printStackTrace();
		}
	    return count;
	}
	
	public  void destroy()
	{
		try {
			mSocket.closeSocket();
		} catch (Exception e) {
			mLastError = e;
		}
	}

	public final String getlasterror()
	{
		return mLastError.getMessage();
	}
	
	public InetSocketAddress getsockname_addr() // uint32_t 
	{
		 return (InetSocketAddress) mSocket.getSocketAddr();
	}
	
	public int getsockname_port()
	{
		return mSocket.getSocketPort();
	}

	public boolean addGroup(int multicastIP) {
		
		try {
			InetAddress multicastAddr = InetAddress.getByAddress(IPAddr_IntToBytes(multicastIP));
			return mSocket.addGroup(multicastAddr);
		} catch (UnknownHostException e) {
			return false;
		}		
	}
	
	public boolean dropGroup(int multicastIP) {
		
		try {
			InetAddress multicastAddr = InetAddress.getByAddress(IPAddr_IntToBytes(multicastIP));
			return mSocket.dropGroup(multicastAddr);
		} catch (UnknownHostException e) {
			return false;
		}		
	}
	
	public static int getaddrinfo_addr(final String name) // uint32_t
	{
		int ret = 0;
	    try {
	    	InetAddress inet = InetAddress.getByName(name);
	        ret = IPAddr_BytesToInt(inet.getAddress());
	    } catch (UnknownHostException e) {
	        // couldn't find it, oh well
	    }
	    
	    return ret;
	}

	boolean send(final byte[] data, int startIndex, int length, int timeout)
	{
		boolean ret = false;
		try {
			if(0 < mSocket.send(data, startIndex, length, timeout))
				ret = true;
		}
		catch(IOException e) {
			mLastError = e;
		}
		
		return ret;
	}
	
	boolean sendto(int remote_addr, int remote_port, final byte[] data, int startIndex, int length, int timeout) {
		
		InetAddress inetAddr;
		try {
			inetAddr = InetAddress.getByAddress(IPAddr_IntToBytes(remote_addr));
		} catch (UnknownHostException e1) {

			mLastError = e1;
			return false;
		}
		InetSocketAddress remoteAddr = new InetSocketAddress(inetAddr, remote_port);
		boolean ret =false;
		try {
			if(0 < mSocket.sendto(data, startIndex, length, remoteAddr, timeout))
				ret = true;
		}
		catch(IOException e) {
			mLastError = e;
		}
		
		return ret;
	}
	
	boolean recv(byte[] data, int start, int[] length, int timeout)
	{
		boolean ret = false;
		try {
			ret = mSocket.recv(data, start, length, timeout);
		}
		catch(IOException e) {
			mLastError = e;
		}
		
		return ret;
	}

	boolean recvfrom(int[] remote_addr, int[] remote_port, byte[] data, int start, int[] length, int timeout)
	{
		boolean ret = false;
		try {
			InetSocketAddress remoteAddr = (InetSocketAddress) mSocket.recvfrom(data, start, length, timeout);
			if(remoteAddr != null)
			{
				remote_addr[0] = IPAddr_BytesToInt(remoteAddr.getAddress().getAddress());
				remote_port[0] = remoteAddr.getPort();
				ret = true;
			}
		}
		catch(IOException e) {
			mLastError = e;
		}
		
		return ret;
	}

	public boolean isValid() {
		return mSocket.isValid();
	}
}
