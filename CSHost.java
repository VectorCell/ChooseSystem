import java.net.*;
import java.io.*;
import java.util.*;

public class CSHost implements Comparable<CSHost>
{
	private String name;
	private String fullname;
	private boolean up;
	private int users;
	private double load;

	private String username, password;

	private static String defaultUsername = "";
	private static String defaultPassword = "";

	private static final HashMap<String, LinkedList<String>> documents = new HashMap<String, LinkedList<String>>();

	private static LinkedList<String> blacklist = null;
	
	private CSHost(String name, boolean up, int users, double load, String username, String password)
	{
		this(name, up, users, load, username, password, true);
	}

	private CSHost(String name, boolean up, int users, double load, String username, String password, boolean utSystem)
	{
		this(name, utSystem ? (name.endsWith(".cs.utexas.edu") ? name : name + ".cs.utexas.edu") : name, up, users, load, username, password);
	}

	private CSHost(String name, String fullname, boolean up, int users, double load, String username, String password)
	{
		this.name = name;
		this.fullname = fullname;
		this.up = up;
		this.users = users;
		this.load = load;
		this.username = username;
		this.password = password;
	}

	public String getName()
	{
		return name;
	}

	public String getFullName()
	{
		return fullname;
	}

	public boolean isUp()
	{
		return up;
	}

	public int getUsers()
	{
		return users;
	}

	public double getLoad()
	{
		return load;
	}

	public String getUsername()
	{
		return username;
	}

	public String getPassword()
	{
		return password;
	}

	public int compareTo(CSHost other)
	{
		if (up && other.up) {
			if (load < other.load)
				return -1;
			else if (load > other.load)
				return 1;
			else if (users < other.users)
				return -1;
			else if (users > other.users)
				return 1;
			else
				return 0;
		} else if (!up && !other.up)
			return 0;
		else
			return up ? 1 : -1;
	}

	public String toString()
	{
		return "" + name + ", " + fullname + ", " + (up ? "up" : "down") + ", " + users + ", " + load + "";
	}

	public static void setDefaultUsername(String username)
	{
		defaultUsername = username;
	}

	public static void setDefaultPassword(String password)
	{
		defaultPassword = password;
	}

	public static boolean blacklisted(String hostname)
	{
		if (blacklist == null) {
			blacklist = getDocument("http://cs.utexas.edu/~bismith/apps/ChooseSystem/public/blacklist.txt");
		}
		return blacklist.contains(hostname);
	}

	public boolean respondsToPing()
	{
		return ping(fullname);
	}
	
	private static boolean ping(String host)
	{
		try {
			boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");

			ProcessBuilder processBuilder = new ProcessBuilder("ping", isWindows? "-n" : "-c", "1 -w 10 ", host);
			Process proc = processBuilder.start();

			System.out.print("Waiting for " + host + "...");
			int returnVal = proc.waitFor();
			System.out.println("done!");
			return returnVal == 0;
		} catch (Exception ex) {
			System.out.println(ex);
			return false;
		}
	}

	public static CSHost[] getCSHosts()
	{
		return getCSHostsFromURL("http://cs.utexas.edu/~bismith/apps/ChooseSystem/public/cshosts.txt");
	}

	public static CSHost[] getCSHosts_sins64()
	{
		return getCSHostsFromURL("http://cs.utexas.edu/~bismith/apps/ChooseSystem/public/cshosts_sins64.txt");
	}

	public static CSHost[] getCSHosts_virtues64()
	{
		return getCSHostsFromURL("http://cs.utexas.edu/~bismith/apps/ChooseSystem/public/cshosts_virtues64.txt");
	}

	public static CSHost[] getCSHosts_kol64()
	{
		return getCSHostsFromURL("http://cs.utexas.edu/~bismith/apps/ChooseSystem/public/cshosts_kol64.txt");
	}

	public static CSHost[] getCSHosts_candy64()
	{
		return getCSHostsFromURL("http://cs.utexas.edu/~bismith/apps/ChooseSystem/public/cshosts_candy64.txt");
	}

	public static CSHost[] getCSHosts_nethack64()
	{
		return getCSHostsFromURL("http://cs.utexas.edu/~bismith/apps/ChooseSystem/public/cshosts_nethack64.txt");
	}

	public static CSHost[] getCSHosts_pub64()
	{
		return getCSHostsFromURL("http://cs.utexas.edu/~bismith/apps/ChooseSystem/public/cshosts_pub64.txt");
	}

	public static CSHost[] getCSHosts_personal()
	{
		LinkedList<Object> list = new LinkedList<Object>();
		list.add(getCSHosts_personal(System.getProperty("user.home") + "\\Google Drive\\Programs\\ChooseSystem\\personal_systems.txt"));
		if (list.getLast() == null) {
			new Thread(new Runnable() {
				public void run() {
					javax.swing.JOptionPane.showMessageDialog(null, "System.getProperty(\"user.home\") doesn't work...");
				}
			}).start();
		}
		list.add(getCSHosts_personal("C:\\Users\\Brandon\\Google Drive\\Programs\\ChooseSystem\\personal_systems.txt"));
		list.add(getCSHosts_personal("D:\\Brandon\\Google Drive\\Programs\\ChooseSystem\\personal_systems.txt"));
		for (Object obj : list) {
			if (obj instanceof CSHost[])
				return (CSHost[])obj;
		}
		return new CSHost[] {
			new CSHost("DigitalOcean VPS", "vps.bismith.net", true, 0, 0.0, "brandon", defaultPassword),
			new CSHost("Home", "bismith.net", true, 0, 0.0, "brandon", defaultPassword),
			new CSHost("Minecraft", "10.0.0.15", true, 0, 0.0, "brandon", defaultPassword),
			new CSHost("Raspberry Pi", "10.0.0.21", true, 0, 0.0, "bismith", defaultPassword)
		};
	}

	public static CSHost[] getCSHosts_personal(String filename)
	{
		try {
			LinkedList<String> personal = new LinkedList<String>();
			Scanner reader = new Scanner(new File(filename));
			while (reader.hasNextLine()) {
				String line = reader.nextLine();
				if (line != null && !line.equals(""))
					personal.add(line);
			}
			CSHost[] systems = new CSHost[personal.size()];
			int index = 0;
			for (String str : personal) {
				String name = null;
				String address = null;
				String username = defaultUsername;
				String password = defaultPassword;
				String[] array = str.split(",");
				for (int k = 0; k < array.length; k++)
					array[k] = array[k].trim();
				if (array.length == 1) {
					name = address = array[0];
				} else if (array.length == 2) {
					name = array[0];
					address = array[1];
				} else if (array.length == 3) {
					name = array[0];
					address = array[1];
					username = array[2];
				} else if (array.length >= 4) {
					name = array[0];
					address = array[1];
					username = array[2];
					password = array[3];
				}
				systems[index++] = new CSHost(name, address, true, 0, 0.0, username, password);
			}
			return systems;
		} catch (Exception ex) {
			return null;
		}
	}

	public static CSHost[] getCSHostsLab()
	{
		// not yet implemented
		return getCSHosts();
	}

	// returns the array, sorted
	private static CSHost[] getCSHostsFromURL(String url)
	{
		LinkedList<CSHost> cshosts = new LinkedList<CSHost>();
		// System.out.println("Getting CS hostnames...");
		String[] hosts = getCSHostnames(url);
		// System.out.println("Getting CS Lab Status...");
		String[] lab = getLabStatus();

		// System.out.println("Determining status of lab machines...");
		int upCount = 0;
		for (String host : hosts) {
			int k = 0;
			while (k < lab.length && !lab[k].contains(host)) k++;
			if (k < lab.length && lab[k].contains(">" + host + "<")) {
				boolean up = lab[k+1].contains(">up<");
				if (!blacklisted(host)) {
					if (up) {
						int users = Integer.valueOf(lab[k+3].substring(lab[k+3].indexOf(">")+1, lab[k+3].indexOf("</td>")));
						double load = Double.valueOf(lab[k+4].substring(lab[k+4].indexOf(">")+1, lab[k+4].indexOf("</td>")));
						cshosts.add(new CSHost(host, up, users, load, defaultUsername, defaultPassword));
						upCount++;
					} else {
						System.out.println("Not including down system: " + host);
					}
				} else {
					System.out.println("Not including blacklisted system " + host);
				}
			}
		}
		// System.out.println("There are " + upCount + " systems that are listed as up.");

		CSHost[] array = new CSHost[cshosts.size()];
		int n = 0;
		for (CSHost host : cshosts) {
			array[n++] = host;
		}
		Arrays.sort(array);
		return array;
	}

	private static CSHost[] copy(CSHost[] a)
	{
		CSHost[] b = new CSHost[a.length];
		for (int k = 0; k < a.length; k++)
			b[k] = a[k];
		return b;
	}

	private static String[] getCSHostnames(String url)
	{
		LinkedList<String> list = getDocument(url);
		String[] array = new String[list.size()];
		int n = 0;
		for (String s : list)
			array[n++] = s;
		return array;
	}

	private static String[] getLabStatus()
	{
		LinkedList<String> list = getDocument("http://apps.cs.utexas.edu/unixlabstatus/");
		String[] array = new String[list.size()];
		int n = 0;
		for (String s : list)
			array[n++] = s;
		return array;
	}

	private static LinkedList<String> getDocument(String url)
	{
		// System.out.println("Getting " + url);
		if (!documents.containsKey(url)) {
			try {
				LinkedList<String> list = new LinkedList<String>();
				URL oracle = new URL(url);
				BufferedReader in = new BufferedReader(
				new InputStreamReader(oracle.openStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null)
				if (!inputLine.equals(""))
				list.addLast(inputLine);
				in.close();
				documents.put(url, list);
			} catch (Exception ex) {
				System.err.println("Document not found: " + url);
				documents.put(url, new LinkedList<String>());
			}
		}
		return documents.get(url);
	}
}