package server.main;


import java.io.IOException;

import server.transport.ServerTransport;

public class ServerDriver {
	public static void main(String[] args) throws IOException
	{    
		ServerTransport server = new ServerTransport(15200);
		
		while(true)
		{
			System.out.println("Waiting...");
			server.receiveFile();
		}
	}
}