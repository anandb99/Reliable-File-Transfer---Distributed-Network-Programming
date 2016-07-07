package client.transport;

import java.util.List;

public class AckMonitor {

	List<SRSender> senders;

	public AckMonitor(List<byte[]> ack, List<SRSender> senders)
	{
		this.senders = senders;
	}
	
	public void Acknowledge(int ackNumber) {
		senders.get(ackNumber).setAcknowledged(true);
	}

}
