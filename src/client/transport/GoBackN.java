package client.transport;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import client.UDP.ClientUDP;

public class GoBackN implements ReliableTransport{

	private final int GOBACKN = 1;
	List<byte[]> window;
	List<byte[]> acks;
	ClientUDP network;
	private int windowSize;
	//helps me know where i'm at in the file
	int iteration = 0;
	
	public GoBackN(String iPAddress, int port, int WindowSize) throws UnknownHostException, SocketException 
	{
		network = new ClientUDP(iPAddress, port);
		window = new LinkedList<byte[]>();
		windowSize = WindowSize;
	}

	@Override
	public void Send(List<byte[]> file) 
	{		
		if(file.size() == 0)
		{
			System.out.println("No File found");
			return;
		}
		//tells the server what method I'm sending
		SendInitialPacket();
		//while i have packets to send send the packets
		while(iteration < file.size())
		{
			SetUpWindow(file);		
			SendWindow();
			ReceiveAcks();
			CheckAcks(acks);
		}
		System.out.println("Sent all packets");
		
		network.Close();
	}
	

	//tells the server I'm sending by Go back N
	private void SendInitialPacket() 
	{
		try
		{
			network.SendFile(new byte[]{GOBACKN});
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	private void ReceiveAcks() {
		WindowReceiver receiver = new WindowReceiver(acks, network, window.size());
		Stopwatch timer = new Stopwatch();
		receiver.Start();
	
		double time = timer.elapsedTime();
		while(!receiver.Finished() && time < 1)
		{
			time = timer.elapsedTime();
		}
		
		if(!receiver.Finished())
		{
			receiver.Interrupt();
		}
		acks = receiver.getAcks();
		
	}

	private void SendWindow() {
		try 
		{
			for(byte[] packet:window)
			{
				network.SendFile(packet);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

	private void SetUpWindow(List<byte[]> file) {
		window = new LinkedList<byte[]>();
		for(int i = 0; i < windowSize; ++i)
		{
			if(iteration < file.size())
			{
				//add a header to the packet
				AddHeader(file.get(iteration), iteration, i, file.size());
				//add the packet to the window
				window.add(file.get(iteration));
				++iteration;
			}
		}		
		
	}

	//if there is something wrong it will be in this function
	private void CheckAcks(List<byte[]> acks) 
	{
		if(acks == null)
		{
			iteration -= window.size();
			return;
		}
		
		if(acks.size() == window.size())
			return;
		List<Integer> expected = new LinkedList<Integer>();
		
		for(int i = 0; i < window.size(); ++i)
		{
			expected.add(i);
		}
		
		
		for(byte[] packet:acks)
		{
			int windowSpot = (int)packet[1];
			if(expected.contains(windowSpot))
				expected.remove(windowSpot);
		}
		
		if(!expected.isEmpty())
		{
			iteration -= (window.size() - expected.get(0));
		}
		//check the acknowledgment packets here
		acks = new LinkedList<byte[]>();
	}

	//the header is 
	//first slot: location in file
	//second slot:location in window
	//third slot:fileLength
	//forth slot: how many past 128 we are 
	private void AddHeader(byte[] segment, int location, int windowLocation, int fileLength) 
	{
		int byteNumber =0;
		if(location > 127)
		{
			byteNumber = location/128;
			location = location %128;  
		}
		segment[0] = (byte) location;
		segment[1] = (byte) windowLocation;
		segment[2] = (byte) fileLength;
		segment[3] = (byte) byteNumber;
		
	}

	@Override
	public void AcknowledgementReceived(int ackNumber) {
		//do nothing
		
	}

	


}
