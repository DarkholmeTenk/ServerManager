import java.util.ArrayList;

public class ShutdownThread implements Runnable
{
	ArrayList<ScreenManager> managers = new ArrayList<ScreenManager>();
	RestartThread restartThread = null;
	public ShutdownThread(RestartThread rt)
	{
		restartThread = rt;
	}

	public void addManager(ScreenManager manager)
	{
		managers.add(manager);
	}
	
	@Override
	public void run()
	{
		restartThread.stop();
		for(ScreenManager manager : managers)
		{
			try
			{
				manager.shutdown();
			}
			catch(Exception e)
			{
			}
		}
	}
}
