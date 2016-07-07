package client.main;



import java.io.IOException;

import client.transport.ClientTransport;

public class Driver {
	
	public static void main(String[] args) throws IOException
	{
		
		ClientTransport clientTransport = new ClientTransport("localhost", 15200);
		
		clientTransport.SendFile("D:\\input.txt");
		
	}
	

}
