import java.util.Scanner;
import java.util.ArrayList;
import java.io.*;
import java.lang.System;

public class console {
	/* constants for changing terminal output color */
	private static final String BLUE = "\033[94m";
	private static final String PURPLE = "\033[95m";
	private static final String NORMAL = "\033[0m";

	/* display variables */
	private static String address = "not connected";
	private static String tagline = BLUE + "roku(not connected)~$ " + NORMAL;

	/* list of commands that can be passed straight to the roku */
	private static String rokucommands = "|home|rev|fwd|play|select|left|right|down|up|back|instantreplay|info|backspace|search|enter|";

	/* file to store the last connected address in */
	private static String lastaddrfile = System.getProperty("java.io.tmpdir") + "/lastaddress.conf";

	/* roku control object */
	private static Roku r = new Roku("");

	/**
	 * Connect to the last used address -- if present -- and then
	 * process user commands as they're entered.
	 */
	public static void main(String[] args) {
		String input;
		String output;
		Scanner s = new Scanner( System.in );
		if (args.length != 0) {
			/* if the arg is "last" connect to last known */
			if (args[0].equals("last")) {
				process("last");
			}
			else if (args[0].equals("test")) {
			try {
				String rokuAddress = RokuScan.scanForRoku();
				process("connect " + rokuAddress);
			} catch (Exception e) {
				System.out.println(BLUE + "[*] " + PURPLE + "Couldn't find a Roku on the network -- please connect manually." + NORMAL);
			}
			}
			/* if the arg is "find" attempt to find the roku */
			else if (args[0].equals("find")) {
				process("find");
			}
			/* otherwise use the IP Address provided */
			else {
				process("connect " + args[0]);
			}
		} 
		do {
			System.out.print(tagline);
			input = s.nextLine();
			output = process(input);
			System.out.println(PURPLE + output + NORMAL);
		} while (true);
	}

	/**
	 * Put an IP Address in the last used address temp file
	 */
	private static boolean putAddress( String fPath, String data ) {
		BufferedWriter bufferedWriter = null;
		try {
			File myFile = new File(fPath);
			if (!myFile.exists()) {
				myFile.createNewFile();
			}
			Writer writer = new FileWriter(myFile);
			bufferedWriter = new BufferedWriter(writer);
			bufferedWriter.write(data);
			return true;
		} catch (IOException e) {
			return false;
		} finally{
			try{
				if(bufferedWriter != null) bufferedWriter.close();
			} catch(Exception ex){}
		}
	}

	/**
	 * Get an address from the last used temp file
	 * @return null if no address is found, otherwise returns the address
	 */
	private static String getAddress(String fPath) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(fPath));
			StringBuilder data = new StringBuilder();

			String line = br.readLine();
			while (line!=null) {
				data.append(line);
				data.append('\n');
				line = br.readLine();
			}

			String fileData = data.toString();
			br.close();
			return fileData;
		} catch(Exception e) {
			return "null";
		}
	}

	/**
	 * Process a user command
	 * @param rawinput the user's input as a string
	 */
	private static String process( String rawinput ) {
		String input = rawinput.trim().toLowerCase();
		String status;
		if (input.equals("exit"))
			System.exit(1);
		else if (input.equals("help")) {
			return NORMAL + "General Commands:"+"\n"+"\thelp\t\tdisplay this message"+"\n"+"\tlast\t\tconnect to the last IP Address used"+"\n"+"\tfind\t\tscan the network for a Roku device and connect to it"+"\n"+ "\tfind all\tscan the network for multiple roku devices and display their addresses\n" + "\texit\t\texit the terminal"+"\n"+"\tuninstall\tdisplay uninstall instructions"+"\n"+"\t"+"\n"+"Roku Commands:"+"\n"+"\tconnect <ip address>"+"\n"+"\ttype <text>"+"\n"+"\thome"+"\n"+"\tplay"+"\n"+"\tselect"+"\n"+"\tleft/right/up/down (l/r/u/d for short)"+"\n"+"\tinstantreplay"+"\n"+"\tnetflix"+"\n"+"\tamazon"+"\n"+"\tsearch"+"\n"+"\tx (access a GUI to control with arrow keys)"+"\n"+""+"\n"+"";
		}
		else if (input.equals("netflix")) {
			r.netflix();
			return "Launched netflix...";
		}
		else if (input.equals("last")) {
			address = getAddress(lastaddrfile);			
			if (!address.equals("null"))
				return process("connect " + address);
			return "No last address found.";
		}
		else if (input.equals("find")) {
			/* build a list of threads to run based on the thread count */
			System.out.println(BLUE + "[*] " + PURPLE + "Creating " + Scan.threadCount + " MSEARCH requests to 239.255.255.250 on port 1900..." + NORMAL);
			ArrayList<Scan> threads = new ArrayList<Scan>();
			for (int i = 0; i < Scan.threadCount; i++) 
				threads.add(new Scan());

			/* start each thread */
			System.out.println(BLUE + "[*] " + PURPLE + "Sending multicast SSDP MSEARCH requests..." + NORMAL);
			for (Scan scan : threads)
				scan.start();

			/* wait for a response */
			System.out.println(BLUE + "[*] " + PURPLE + "Waiting for network response..." + NORMAL);
			while (Scan.results.size() == 0) {
				try { Thread.sleep(10); } catch (Exception e) {}
			}

			/* stop each thread */
			for (Scan scan : threads)
				scan.interrupt();

			/* display results */
			String result = Scan.results.get(0);
			System.out.println(BLUE + "[*] " + PURPLE + "Found Roku at " + result + "..." + NORMAL);
			process("connect " + result);
			return "";
		}
		else if (input.equals("find all")) {
			/* build a list of threads to run based on the thread count */
			System.out.println(BLUE + "[*] " + PURPLE + "Scanning, press [ENTER] when done..." + NORMAL);
			ArrayList<Scan> threads = new ArrayList<Scan>();
			for (int i = 0; i < Scan.threadCount; i++) 
				threads.add(new Scan());

			/* start each thread */
			for (Scan scan : threads)
				scan.start();

			/* wait for a response */
			Scan.spontaneousOutput = true;
			System.out.println(BLUE + "[*] " + PURPLE + "Results: " + NORMAL);
			Scanner scannerEnter = new Scanner(System.in);
			String possiblyEnter;
			while (scannerEnter.hasNextLine()) {
				possiblyEnter = scannerEnter.nextLine();
				if (possiblyEnter.trim().equals("")) {
					System.out.println(BLUE + "[*] " + PURPLE + "Scan complete" + NORMAL);
					Scan.spontaneousOutput = false;
					break;
				}
			}

			/* stop each thread */
			for (Scan scan : threads)
				scan.interrupt();
			
			return "\b\b";
		}
		else if (input.equals("uninstall")) {
			return "To uninstall roku, navigate to /etc/roku and run sudo ./uninstall";
		}
		else if (input.equals("amazon")) {
			r.amazon();
			return "Launched amazon...";
		}
		else if (input.equals("l")) {
			r.keypress("left");
			return "Executed left...";
		}
		else if (input.equals("r")) {
			r.keypress("right");
			return "Executed right...";
		}
		else if (input.equals("u")) {
			r.keypress("up");
			return "Executed up...";
		}
		else if (input.equals("d")) {
			r.keypress("down");
			return "Executed down...";
		}
		else if (input.equals("x")) {
			x Window = new x(address);
			return "\b";
		}
		else if (input.startsWith("type ")) {
			status = r.enterString(input.substring(5));
			return "Executed " + input + "...";
		}
		else if (input.startsWith("connect ")) {
			r = new Roku(input.substring(8));
			address = input.substring(8);
			tagline = BLUE + "roku@" + input.substring(8) + "~$ " + NORMAL;
			putAddress(lastaddrfile, address);
			return "";
		}
		else if (rokucommands.contains("|" + input + "|")) {
			status = r.keypress(input);
			return "Executed " + input + "...";
		}
		return "Unrecognized command \"" + rawinput + "\" - use \"help\" for a list of commands.";
	}
}
