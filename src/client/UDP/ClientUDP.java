package client.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import client.transport.ReliableTransport;

public class ClientUDP {

	private DatagramSocket clientSocket;    
    private InetAddress IPAddress;   
    private int portNumber;
    
	public ClientUDP(String IPAddress, int port) throws UnknownHostException, SocketException 
	{
		this.IPAddress = InetAddress.getByName(IPAddress);
		portNumber = port;
		clientSocket = new DatagramSocket();
	}

	//send all packets just like before
	public void SendFile(byte[] packet) throws IOException 
	{
		DatagramPacket sendPacket = new DatagramPacket(packet, packet.length, IPAddress, portNumber);		
	    clientSocket.send(sendPacket);
	}
	
	//This the receiving function.  I assume how many packets to receive back 
	//This will block if it doesn't get all the acknowledgments back.
	//we need to time this out.
	public List<byte[]> RecieveAck(int expectedWindowSize, List<byte[]> recievedAcks, ReliableTransport RDT)
	{
		int test = 0;

		try
		{
			do
			{
				byte[] buffer = new byte[500];
				DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
				clientSocket.receive(receivePacket);
				recievedAcks.add(buffer);
				RDT.AcknowledgementReceived((int)buffer[1]);
				++test;
			}while(test<expectedWindowSize);

		}
		catch ( Exception e)
		{
			System.out.println(e);    
		}	
		return recievedAcks;
	}

	//This the receiving function.  I assume how many packets to receive back 
	//This will block if it doesn't get all the acknowledgments back.
	//we need to time this out.
	public List<byte[]> RecieveAck(int expectedWindowSize, List<byte[]> recievedAcks)
	{
		int test = 0;

		try
		{
			do
			{
				byte[] buffer = new byte[500];
				DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
				clientSocket.receive(receivePacket);
				recievedAcks.add(buffer);
				++test;
			}while(test<expectedWindowSize);

		}
		catch ( Exception e)
		{
			System.out.println(e);    
		}	
		return recievedAcks;
	}
	
	public void Close()
	{
		clientSocket.close();
	}

}
