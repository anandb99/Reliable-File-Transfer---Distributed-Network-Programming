package client.transport;

import java.util.List;

public interface ReliableTransport {
	
	public abstract void Send(List<byte[]> file);

	public abstract void AcknowledgementReceived(int ackNumber);

}
