package client.transport;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ClientTransport {

	private ReliableTransport RDT;
	private int windowSize =4;
	
	
	public ClientTransport(String IPAddress, int port) throws UnknownHostException, SocketException 
	{
		//If i want to switch the RDT i can do it right here
		RDT = new GoBackN(IPAddress, port, windowSize);
//		RDT = new SelectiveRepeat(IPAddress, port, windowSize);
	}

	public void SendFile(String filePath) throws IOException 
	{
    	List<byte[]> file = ParseFile(filePath);
    	RDT.Send(file);
    }

	

	private List<byte[]> ParseFile(String filePath) 
	{
		FileInputStream file = null;
        List<byte[]> parsedFile = new LinkedList<byte[]>();
        parsedFile.add(GetFileName(filePath));
        
         

        try
        {
        	
        	file = new FileInputStream(filePath);
            while ( ( file.available() != 0 ))
            {	
            	byte[] buffer = new byte[500]; 
            	//I let the first 5 bytes be the header
            	file.read(buffer, 5, buffer.length - 5);
//                System.out.println("Data : "+ new String(buffer));
                parsedFile.add(buffer);
            }

        }
        catch(Exception e)
        {
        	System.out.println(e);
        }
        finally
        {
        	parsedFile.add(StopBuffer());
            CloseFile(file);                                                                                                                         
         }
		return parsedFile;
	}

	private byte[] GetFileName(String filePath) 
	{
		//I let the first 5 bytes be the header
		byte[] buffer = new byte[500];
		File file = new File(filePath);	
		byte[] fileBytes = file.getName().getBytes();
		for(int i = 5; i < fileBytes.length + 5; ++i)
		{
			buffer[i]= fileBytes[i-5];
		}
		return buffer;
	}

	private byte[] StopBuffer() 
	{
		byte[] buffer = new byte[500];
		Arrays.fill(buffer, (byte)-1);
		return buffer;
	}

	private void CloseFile(FileInputStream file) 
	{
		  try
          {
              if ( file != null )
                  file.close();
          }
          catch ( Exception e)
          {
              System.out.println(e);    
          }
		
	}

	

}
