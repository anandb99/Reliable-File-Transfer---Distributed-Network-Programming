package client.transport;

public class Timer implements Runnable{

	SRSender packet;
	int timeOut;
	Thread t;
	public Timer(SRSender packet, int timeOut)
	{
		this.packet = packet;
		this.timeOut = timeOut;
		t = new Thread(this);
	}
	
	
	public void Start()
	{
		t.start();
	}
	
	public void End()
	{
		try {
			t.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	@Override
	public void run() {
		while(!packet.getAck())
		{
			packet.run();
			Stopwatch timer = new Stopwatch();
			double time = timer.elapsedTime();
			
			while(time < timeOut && !packet.getAck())
			{
				time = timer.elapsedTime();
			}
		}

	}
	
	

}
