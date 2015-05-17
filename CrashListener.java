public class CrashListener implements IMessageListener
{
	private boolean dead = false;
	private ScreenManager monitoredScreen;

	public CrashListener(ScreenManager man)
	{
		monitoredScreen = man;
	}	

	public boolean isDead()
	{
		return dead;
	}

	public boolean awaitingMessage()
	{
		return !dead;
	}

	public void die()
	{
		dead = true;
	}

	public void message(String message)
	{
		
		String crashString = "This crash report has been saved to";
		MinecraftMessage m = new MinecraftMessage(message);
		if(m.getSource().equals("[Minecraft-Server]") && m.getMessage().indexOf("This crash report has been saved to") == 0)
		{
			try
			{
				monitoredScreen.restart();
			}
			catch(Exception e)
			{}
		}
	}
}
