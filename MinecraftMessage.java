import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MinecraftMessage
{
	String message;
	String severity = "Unknown";
	String source = "Unknown";
	long time = 0;
	private static SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss");

	private String[] groupClusters(String[] toCluster)
	{
		ArrayList<String> list = new ArrayList<String>();
		String current = "";
		for(int i = 0;i < toCluster.length;i++)
		{
			if(current.equals(":"))
				current = "";
			if(current.equals(""))
				current = toCluster[i];
			else
				current += " " +  toCluster[i];
			if(!current.startsWith("[") || (current.indexOf(']') != -1))
			{
				int index = current.indexOf(']');
				String newCurrent = "";
				if(index != -1)
				{
					if(index < current.length())
						newCurrent = current.substring(index+1);
					current = current.substring(0,index);
				}
				current = current.replace("[","");
				current = current.replace("]","");
				list.add(current);
				current = newCurrent;
			}
		}
		if(!current.equals(""))
			list.add(current);
		return list.toArray(new String[0]);
	}

	private String trim(String a)
	{
		if(a.startsWith(":"))
			a = a.substring(1);
		return a.replace("[","").replace("]","").trim();
	}

	public MinecraftMessage(String in)
	{
		//System.out.println("S:"+in);
		if(in.contains("]"))
		{
			String[] data = in.split("\\]",4);
			//data = groupClusters(data);
			try
			{
				//System.out.println("ARRAY:" + Arrays.toString(data));
				time = sdf.parse(trim(data[0])).getTime();
				String s = trim(data[1]);
				String[] secondary = s.split("/");
				source = secondary[0];
				severity = secondary[1];
				if(data.length == 4)
					message = data[3];
				else
					message = data[2];
				//System.out.println("MESSAGE: " + source + "	" + severity + "	" + message);
			}
			catch(Exception e)
			{
				System.err.println("Error handling message " + in);
				System.err.println(e);
				//e.printStackTrace();
				message = in;
			}
		}
		else
		{
			message = in;
		}
	}

	public String getMessage()
	{
		return message;
	}

	public String getSource()
	{
		return source;
	}

	public String getSeverity()
	{
		return severity;
	}

	public long getTime()
	{
		return time;
	}

}
