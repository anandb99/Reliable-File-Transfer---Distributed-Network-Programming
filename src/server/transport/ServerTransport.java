package server.transport;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.net.SocketException;
import java.util.List;

import server.UDP.ServerUDP;

public class ServerTransport {

	ServerUDP UDPServer;
	public ServerTransport(int port) 
	{
		UDPServer = new ServerUDP(port);
		// TODO Auto-generated constructor stub
	}

	public void receiveFile() throws FileNotFoundException, SocketException 
	{
		List<byte[]> recievedFile = UDPServer.RecieveFile();
		if(recievedFile == null)
			return;
		WriteFile(recievedFile);		
	}

	private void WriteFile(List<byte[]> recievedFile) throws FileNotFoundException 
	{
		String fileName = new String(recievedFile.get(0)).trim().substring(3);
		FileOutputStream fos = new FileOutputStream(new File("E:\\" + fileName));
		int packetNumber = 0;
		try
		{
			for(byte[] packet : recievedFile)
			{
				//Writes the packet starting at the 5 spot
				if(packet[5] != -1 && packetNumber != 0)
					fos.write(packet,5, packet.length-5);
				++packetNumber;
			}

			if ( fos != null )
				fos.close();
			System.out.println("Done !");
		}
		catch ( Exception e)
		{
			System.out.println(e);    
		}
		
	}

}
