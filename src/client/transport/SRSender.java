package client.transport;

import java.io.IOException;

import client.UDP.ClientUDP;

public class SRSender {

	private ClientUDP network;
	private Thread t;
	private byte[] packet;
	private boolean acknowledged;
	private int windowNumber;
	
	public SRSender(byte[] packet, ClientUDP network, int windowNumber)
	{
		acknowledged = false;
		this.packet = packet;
		this.network = network;
		this.windowNumber = windowNumber;
	}
	


	public void run() {
		
		try {
			network.SendFile(packet);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		
	}
	
	public void setAcknowledged(boolean ack)
	{
		acknowledged = ack;
	}
	
	public int getWindowNumber()
	{
		return windowNumber;
	}

	public boolean getAck() {
		return acknowledged;
	}
	

}
