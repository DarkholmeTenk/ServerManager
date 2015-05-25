import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

public class ScreenManager
{
	private File myDir;
	private String[] opts;

	private Process server;
	private InputStream stream;
	private BufferedReader reader;
	private PrintStream writer;

	ArrayList<IMessageListener> listeners = new ArrayList<IMessageListener>();
	LinkedList<String> lastMessages = new LinkedList<String>();

	public ScreenManager(File directory,String... options) throws Exception
	{
		myDir = directory;
		opts = options;
		start();
	}

	private void detectCrash(String line)
	{
		String crashString = "This crash report has been saved to";
		MinecraftMessage m = new MinecraftMessage(line);
		if(m.getSource().equals("[Minecraft-Server]") && (m.getMessage().indexOf("This crash report has been saved to") == 0))
		{
			try
			{
				restart();
			}
			catch(Exception e)
			{}
		}
	}

	public void addMessageListener(IMessageListener listener)
	{
		listeners.add(listener);
	}

	private synchronized void addLine(String line)
	{
		Iterator<IMessageListener> iter = listeners.iterator();
		while(iter.hasNext())
		{
			IMessageListener listen = iter.next();
			if(listen.isDead())
				iter.remove();
		}
		for(IMessageListener listen: listeners)
		{
			if(listen.awaitingMessage())
				listen.message(line);
		}
		//detectCrash(line);
		lastMessages.addFirst(line);
		if(lastMessages.size() > 30)
			lastMessages.removeLast();
	}

	public void sendLine(String line)
	{
		addLine(line);
		writer.print(line+"\n\n");
		writer.flush();
	}

	public String getLine() throws IOException
	{
		if(!reader.ready())
			return null;
		String line = reader.readLine();
		if(line == null)
			return null;
		addLine(line);
		return line;
	}

	public void restart() throws Exception
	{
		shutdown();
		start();
	}

	private String[] getArguments()
	{
		int s = opts.length;
		String[] arg = new String[5 + s];
		arg[0] = "/usr/bin/java";
		arg[1] = "-server";
		for(int i = 2;i< (opts.length + 2);i++)
		{
			arg[i] = opts[i-2];
		}
		arg[s + 2] = "-jar";
		arg[s + 3] = myDir.toString() + "/server.jar";
		arg[s + 4] = "nogui";
		return arg;
	}

	public void start() throws Exception
	{
		while(!isDown())
			shutdown();
		String[] args = getArguments();
		System.out.println(Arrays.toString(args));
		ProcessBuilder b = new ProcessBuilder(args);
		b.directory(myDir);
		b.redirectErrorStream(true);
		server = b.start();

		stream = server.getInputStream();
		reader = new BufferedReader(new InputStreamReader(stream));
		writer = new PrintStream(server.getOutputStream());
	}

	public boolean isDown()
	{
		if(server == null)
			return true;
		try
		{
			server.exitValue();
			return true;
		}
		catch(IllegalThreadStateException e)
		{
			return false;
		}
	}

	public void shutdown() throws Exception
	{
		sendLine("stop");
		System.out.println("Sent stop command");
		int deadTimer = 0;
		while (deadTimer < 40000)
		{
			try
			{
				server.exitValue();
				return;
			}
			catch(IllegalThreadStateException e)
			{
				if(deadTimer >= 30000)
				{
					server.destroy();
					isDown();
					server = null;
					return;
				}
			}
			Thread.sleep(500);
			deadTimer += 500;
		}
	}
}
