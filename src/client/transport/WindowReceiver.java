package client.transport;

import java.util.LinkedList;
import java.util.List;

import client.UDP.ClientUDP;

public class WindowReceiver implements Runnable{

	List<byte[]> acks;
	ClientUDP network;
	int windowSize;
	boolean finished;
	Thread t;
	public WindowReceiver(List<byte[]> acks, ClientUDP network, int windowSize)
	{
		this.acks = acks;
		this.network = network;
		this.windowSize = windowSize;
		t =new Thread(this);
	}
	
	public void Start()
	{
		t.start();
	}
	
	public void Interrupt()
	{
		t.interrupt();
		finished = true;
	}
	@Override
	public void run() 
	{
		acks = new LinkedList<byte[]>();
		network.RecieveAck(windowSize, acks);
		finished = true;
	}
	
	public boolean Finished()
	{
		return finished;
	}
	
	public List<byte[]> getAcks()
	{
		return acks;
	}

}
