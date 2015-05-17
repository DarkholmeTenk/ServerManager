import java.util.ArrayList;

public class RestartThread implements Runnable
{
	ArrayList<ScreenManager> managers = new ArrayList<ScreenManager>();
	private boolean keepTrying = true;
	public void addManager(ScreenManager... manager)
	{
		for(ScreenManager m:manager)
			managers.add(m);
	}

	public void stop()
	{
		System.out.println("Stopping restart thread!");
		keepTrying = false;
	}

	public void run()
	{
		while(keepTrying)
		{
			try
			{
				if(managers.size() == 0)
					continue;

				for(ScreenManager m: managers)
				{
					try
					{
						if(m.isDown())
						{
							System.out.println("Restart thread kicking server!");
							m.restart();
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				Thread.sleep(100);
			}
			catch(InterruptedException e)
			{
			}
		}
	}
}
