import java.net.*;
import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ChooseSystem
{
	private static final String SSH_DEFAULT = "\"C:\\Program Files (x86)\\PuTTY\\putty.exe\"";
	private static final String VNCVIEWER_DEFAULT = "\"C:\\Program Files\\TightVNC\\tvnviewer.exe\"";
	private static String ssh = SSH_DEFAULT;
	private static String vncviewer = VNCVIEWER_DEFAULT;
	private static String osname;
	static
	{
		osname = System.getProperty("os.name", "generic").toLowerCase();
		if (osname.startsWith("windows")) {
			ssh = "cmd /c start \"\" " + SSH_DEFAULT;
			vncviewer = "cmd /c start \"\" " + VNCVIEWER_DEFAULT;
		}
		else if (osname.startsWith("linux")) {
			ssh = "";
			vncviewer = "";
		}
		else if (osname.startsWith("sunos")) {
			ssh = "";
			vncviewer = "";
		}
		else if (osname.startsWith("mac") || osname.startsWith("darwin")) {
			ssh = "";
			vncviewer = "";
		} else {
			ssh = "";
			vncviewer = "";
		}
	}
	
	private static int rows = 20;
	
	public static void main(String[] args)
	{
		String username = "";
		String password = "";
		try {
			for (int k = 0; k < args.length; k++) {
				if (args[k].equals("-putty") || args[k].equals("-ssh")) {
					ssh = "\"" + args[k+1] + "\"";
				} else if (args[k].equals("-tightvnc") || args[k].equals("-vncviewer")) {
					vncviewer = "\"" + args[k+1] + "\"";
				} else if (args[k].equals("-rows")) {
					rows = Integer.valueOf(args[k+1]);
				} else if (args[k].equals("-username")) {
					username = args[k+1];
				} else if (args[k].equals("-password")) {
					password = args[k+1];
				}
			}
		} catch (Exception ex) {
			System.err.println(ex);
		}

		// can optionally set default username and password here
		CSHost.setDefaultUsername("");
		CSHost.setDefaultPassword("");
		
		JFrame loading = new JFrame("Loading...");
		loading.setContentPane(new JLabel("        Loading...        "));
		loading.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		loading.pack();
		loading.setVisible(true);

		final JFrame frame = new JFrame("CS Systems");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		frame.setContentPane(getContentPane());
		setJMenuBar(frame);

		frame.pack();
		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Dimension screenDimension = env.getMaximumWindowBounds().getSize();

		frame.setSize(frame.getSize().width + 20, Math.min(frame.getSize().height, screenDimension.height));

		loading.dispose();
		frame.setVisible(true);
	}
	
	private static JMenuBar setJMenuBar(final JFrame frame)
	{
		JMenuBar menuBar = new JMenuBar();
		
		JMenu file = new JMenu("File");
		JMenuItem exit = new JMenuItem("Quit");
		exit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent ae) {
				frame.dispose();
			}
		});
		file.add(exit);
		
		menuBar.add(file);
		return menuBar;
	}

	private static JTabbedPane getContentPane()
	{
		JTabbedPane content = new JTabbedPane();

		content.addTab("All", getCSHostsPane(CSHost.getCSHosts()));
		content.addTab("sins64", getCSHostsPane(CSHost.getCSHosts_sins64()));
		content.addTab("virtues64", getCSHostsPane(CSHost.getCSHosts_virtues64()));
		content.addTab("kol64", getCSHostsPane(CSHost.getCSHosts_kol64()));
		content.addTab("candy64", getCSHostsPane(CSHost.getCSHosts_candy64()));
		content.addTab("nethack64", getCSHostsPane(CSHost.getCSHosts_nethack64()));
		content.addTab("personal", getCSHostsPane(CSHost.getCSHosts_personal()));

		return content;
	}

	private static JScrollPane getCSHostsPane(CSHost[] cshosts)
	{
		int rows = Integer.MAX_VALUE;
		return getCSHostsPane(cshosts, Math.min(rows, cshosts.length));
	}

	private static JScrollPane getCSHostsPane(CSHost[] cshosts, int rows_systems)
	{
		final CSHost[] hosts = cshosts;

		// int rows_systems = Math.min(rows, hosts.length);

		JPanel content = new JPanel(new BorderLayout());
		JPanel west = new JPanel(new GridLayout(rows_systems + 1, 1));
		JPanel center = new JPanel(new GridLayout(rows_systems + 1, 2));
		JPanel east = new JPanel(new GridLayout(rows_systems + 1, 2));
		west.add(new JLabel("  name"));
		center.add(new JLabel("  users"));
		center.add(new JLabel("  load"));
		east.add(new JLabel("  ssh"));
		east.add(new JLabel("  vnc"));
		int unreachable = 0;
		for (int k = 0; k < Math.min(hosts.length, rows_systems + unreachable); k++) {
			final int index = k;
			String hostname_tmp = hosts[index].getFullName();
			// if (!hostname_tmp.contains(".")) hostname_tmp += ".cs.utexas.edu";
			final String hostname = hostname_tmp;
			final String username = hosts[k].getUsername();
			final String password = hosts[k].getPassword();
			
			if (false && !hosts[index].respondsToPing()) {
				unreachable++;
				continue;
			}

			final JLabel label = new JLabel("  " + hosts[k].getName() + "  ");
			label.addMouseListener(new MouseListener() {
				public void mouseClicked(MouseEvent me) {}
				public void mouseEntered(MouseEvent me) {}
				public void mouseExited(MouseEvent me) {}
				public void mousePressed(MouseEvent me) {
					Color foreground = Color.RED;
					if (!label.getForeground().equals(foreground))
						label.setForeground(foreground);
					else
						label.setForeground(new JLabel().getForeground());
					label.update(label.getGraphics());
				}
				public void mouseReleased(MouseEvent me) {}
			});
			west.add(label);

			center.add(new JLabel("  " + hosts[k].getUsers() + "  "));

			center.add(new JLabel("  " + hosts[k].getLoad() + "  "));

			final JButton sshbutton = new JButton("SSH");
			sshbutton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					exec("cmd /c start \"\" " 
						+ ssh + " -ssh " 
						+ (username != null && !username.equals("") ? username + "@" : "") + hostname
						+ (password != null && !password.equals("") ? " -pw " + password : ""));
				}
			});
			east.add(sshbutton);

			JButton vncbutton = new JButton("VNC");
			vncbutton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					exec("cmd /c start \"\" " 
						+ vncviewer + " -host=" + hostname + " -port=5901" 
						+ (password != null && !password.equals("") ? " -password=" + password : ""));
				}
			});
			east.add(vncbutton);

		}
		content.add(west, BorderLayout.WEST);
		content.add(center, BorderLayout.CENTER);
		content.add(east, BorderLayout.EAST);

		// return content;
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(content, BorderLayout.NORTH);
		return new JScrollPane(panel);
	}

	// works for up to 9 arguments
	private static String makeCmd(String template, String... args)
	{
		for (int k = 0; k < Math.min(args.length, 10); k++) {
			template = template.replace("%" + k, args[k]);
		}
		return template;
	}

	private static LinkedList<String> exec(String cmd)
	{
		LinkedList<String> output = new LinkedList<String>();
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			String s;

			BufferedReader stdInput = new BufferedReader(new 
				InputStreamReader(proc.getInputStream()));
			// read the output from the command
			while (false && (s = stdInput.readLine()) != null) {
				output.add(s);
			}
		} catch (Exception ex) {
			System.err.println(ex);
		}
		return output;
	}
}
