package client.transport;

import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import client.UDP.ClientUDP;

public class SelectiveRepeat implements ReliableTransport{

	private final int SELECTIVE_REPEAT = 1;
	List<byte[]> window;
	List<byte[]> acks;
	ClientUDP network;
	private int windowSize;
	List<SRSender> packetsToSend;
	List<Timer> timers;
	AckMonitor monitor;
	//helps me know where i'm at in the file
	int iteration = 0;
	
	public SelectiveRepeat(String iPAddress, int port, int WindowSize) throws UnknownHostException, SocketException 
	{
		network = new ClientUDP(iPAddress, port);
		window = new LinkedList<byte[]>();
		windowSize = WindowSize;
		packetsToSend = new LinkedList<SRSender>();
	}
	
	@Override
	public void Send(List<byte[]> file) {
		if(file.size() == 0)
		{
			System.out.println("No File found");
			return;
		}
		//tells the server what method I'm sending
		SendInitialPacket();
		
		while(iteration < file.size())
		{
			SetUpWindow(file);		
			SendWindow();
			ReceiveAcks();
		}
		System.out.println("Sent all packets");
		
		//network.Close();
	}

	
	private void SetUpMonitor() {
		monitor = new AckMonitor(acks, packetsToSend);
	}

	//tells the server I'm sending by Go back N
	private void SendInitialPacket() 
	{
		try
		{
			network.SendFile(new byte[]{SELECTIVE_REPEAT});
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
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
	
	private void ReceiveAcks() 
	{
		acks = new LinkedList<byte[]>();
		SetUpMonitor();
		network.RecieveAck(windowSize, acks, this);		
	}
	
	private void SendWindow() {
		packetsToSend = new LinkedList<SRSender>();
		timers = new LinkedList<Timer>();
		int number = 0;
		
		for(byte[] packet:window)
		{
			packetsToSend.add(new SRSender(packet,network, number));
			++number;
		}
		
		for(SRSender packet: packetsToSend)
		{
			timers.add(new Timer(packet,1));
		}
		
		for(Timer timer: timers)
		{
			timer.Start();
			try {
				Thread.sleep(5);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}

	@Override
	public void AcknowledgementReceived(int ackNumber) {
		if(ackNumber > 5)
		{
			System.out.println("Why" + iteration);
		}
		monitor.Acknowledge(ackNumber);
		timers.get(ackNumber).End();
		
	}
}
