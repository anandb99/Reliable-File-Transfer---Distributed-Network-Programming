package server.UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import client.transport.Stopwatch;

public class ServerUDP 
{
	private final int GOBACKN = 1;
	private final int SELECTIVE_REPEAT = 2;
	
	private DatagramSocket serverSocket;
	private int port;

	public ServerUDP(int port) 
	{
		this.port = port;
	}

	public List<byte[]> RecieveFile() throws SocketException 
	{
		serverSocket = new DatagramSocket(port);	
		int sendType = 0;
		
		try
		{
			byte[] buffer = new byte[500];
			DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
			serverSocket.receive(receivePacket);
			System.out.println("Received initial packet");
			sendType = (int)buffer[0];
		}
		catch ( Exception e)
		{
			System.out.println(e);    
		}	
		if(sendType == GOBACKN)
		{
			return ReceiveGoBackN();
		}
		else if(sendType == SELECTIVE_REPEAT)
		{
			return ReceiveSelectiveRepeat();
		}
		return null;
	}

	private List<byte[]> ReceiveSelectiveRepeat() {
		List<byte[]> receivedFile = new LinkedList<byte[]>();
		HashMap<Integer, byte[]> receivedPackets = new HashMap<Integer, byte[]>();
		
		Stopwatch timer = new Stopwatch();
		int stopValue = 0;
		try
		{
			do
			{
				byte[] buffer = new byte[500];
				DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
				serverSocket.receive(receivePacket);
				System.out.println("Received packet" + (buffer[0] + 128 * (int)buffer[3]));
				
				if(!receivedPackets.containsKey((int)buffer[0] + 128 * (int)buffer[3]))
				{					
					receivedPackets.put((int)buffer[0], buffer);				
					serverSocket.send(new DatagramPacket(GetAckPacket(buffer), 4, receivePacket.getAddress(), receivePacket.getPort()));
				}
				stopValue = (int)buffer[5];		
			}while(stopValue!=(byte)-1);

			serverSocket.close();
			
			for(int i = 0; i < receivedPackets.size(); ++i)
			{
				receivedFile.add(receivedPackets.get(i));
			}
			System.out.println("Received all the packets");
		}
		catch ( Exception e)
		{
			System.out.println(e);    
		}	
		
		System.out.println(timer.elapsedTime() + "seconds");
		return receivedFile;
	}

	private List<byte[]> ReceiveGoBackN() 
	{
		List<byte[]> recievedFile = new LinkedList<byte[]>();
		int stopValue = 0;
		int expectedPacket = 0;
		
		Stopwatch timer = new Stopwatch();
		try
		{
			do
			{
				byte[] buffer = new byte[500];
				DatagramPacket receivePacket = new DatagramPacket(buffer, buffer.length);
				serverSocket.receive(receivePacket);
//				System.out.println("Received packet" + (buffer[0] + 128 * (int)buffer[3]));
				//check the packet to make sure its the right one.
				if(expectedPacket == (int)buffer[0] + 128 * (int)buffer[3])
				{
					recievedFile.add(buffer);
					serverSocket.send(new DatagramPacket(GetAckPacket(buffer), 4, receivePacket.getAddress(), receivePacket.getPort()));
					++expectedPacket;
				}
				stopValue = (int)buffer[5];
			}while(stopValue!=(byte)-1);

			serverSocket.close();
			System.out.println("Received all the packets");
		}
		catch ( Exception e)
		{
			System.out.println(e);    
		}	
		System.out.println(timer.elapsedTime() + "seconds");
		return recievedFile;
	}

	//acknowledgement packet
	//first slot: packet location in file
	//second slot: location in window
	//third slot: ack number
	//forth slot: how many past 128 we are 
	private byte[] GetAckPacket(byte[] buffer) {
		byte[] ack = new byte[4];
		ack[0] = buffer[0];
		ack[1] = buffer[1];
		ack[2] = (byte)1;
		ack[3] = buffer[3];
		return ack;
	}
	
	

}
