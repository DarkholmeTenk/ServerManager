import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Test
{
	private static BufferedReader in;
	public static ShutdownThread st;
	public static RestartThread rt;
	
	static
	{
		rt = new RestartThread();
		st = new ShutdownThread(rt);
		Runtime.getRuntime().addShutdownHook(new Thread(st));
	}

	public static void main(String[] args) throws Exception
	{
		if (args.length == 0)
			throw new IllegalArgumentException("No minecraft folder");
		String f =args[0];
		in = new BufferedReader(new InputStreamReader(System.in));
		ScreenManager m = new ScreenManager(new File(f),"-Xmx3G","-XX:+UseG1GC","-XX:+UseStringDeduplication");
		st.addManager(m);
		rt.addManager(m);
		m.addMessageListener(new CrashListener(m));
		FreezeListener frez = new FreezeListener(m);
		m.addMessageListener(frez);
		new Thread(frez).start();
		Thread resThread = new Thread(rt);
		resThread.start();
		System.out.println("Server created");
		while(true)
		{
			String l = m.getLine();
			while(l != null)
			{	
				System.out.println(l);
				l = m.getLine();
			}
			
			if(in.ready())
			{
				l = in.readLine();
				if(l != null)
					m.sendLine(l);
			}
			Thread.sleep(25);
		}
	}
}
