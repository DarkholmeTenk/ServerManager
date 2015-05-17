public class FreezeListener implements IMessageListener, Runnable
{
	private volatile boolean isWaiting = false;
	private ScreenManager monitoredScreen;

	public FreezeListener(ScreenManager man)
	{
		monitoredScreen = man;
	}	

	public void die(){};
	public boolean isDead(){return false;}
	
	public void run()
	{
		System.out.println("Freeze monitor starting!");
		while(true)
		{
			try
			{
				if(isWaiting)
				{
					System.err.println("No response from list command detected. Restarting");
					//monitoredScreen.restart();
				}
				Thread.sleep(600 * 1000);
				isWaiting = true;
				monitoredScreen.sendLine("list");
				Thread.sleep(6 * 1000);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}

	public  boolean awaitingMessage()
	{
		return isWaiting;
	}

	public void message(String message)
	{
		MinecraftMessage m = new MinecraftMessage(message);
		if(m.getSource().equals("Server thread") && m.getSeverity().equals("INFO"))
		{
			String mess = m.getMessage();
			System.out.println("mess : #" + mess + "#");
			if(mess.startsWith("There are") && mess.endsWith("players online:"))
			{
				System.err.println("Response from list detected!");
				isWaiting = false;
			}
		}
		else
		{
			System.out.println("s: #"+m.getSource()+"# #" + m.getSeverity()+"#");
		}
	}
}
