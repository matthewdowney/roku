/* Send a multicast SSDP M-SEARCH request on multiple threads to 
 * find the addresses of all Rokus on the network.
 * device. For more info go to: 
 * http://sdkdocs.roku.com/display/sdkdoc/External+Control+Guide 
 */

import java.net.*;
import java.util.ArrayList;

/* we extend the Thread class to that we can call multiple instances */
class Scan extends Thread {
	/* these class variables are shared by all threads */

	/* our results list that each Thread will report back to */
	public static ArrayList<String> results = new ArrayList<String>();

	/* we use this to determine how many threads to create */
	public static final int threadCount = 5;

	/* whether or not we print to the command line for each result */
	public static boolean spontaneousOutput = false;

	/* constants for changing terminal output color */
	private static final String BLUE = "\033[94m";
	private static final String PURPLE = "\033[95m";
	private static final String NORMAL = "\033[0m";

	public static boolean verbose = true;

	/**
	 * Scan the local area network for a single Roku device
	 * @return the IP Address of the first found Roku device
	 */
	public String scanForRoku() {
		try {
			/* create byte arrays to hold our send and response data */
			byte[] sendData = new byte[1024];
			byte[] receiveData = new byte[1024];

			/* our M-SEARCH data as a byte array */
			String MSEARCH = "M-SEARCH * HTTP/1.1\nHost: 239.255.255.250:1900\nMan: \"ssdp:discover\"\nST: roku:ecp\n"; 
			sendData = MSEARCH.getBytes();

			/* create a packet from our data destined for 239.255.255.250:1900 */
			DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("239.255.255.250"), 1900);

			/* send packet to the socket we're creating */
			DatagramSocket clientSocket = new DatagramSocket();
			clientSocket.send(sendPacket);

			/* recieve response and store in our receivePacket */
			DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
			clientSocket.receive(receivePacket);

			/* get the response as a string */
			String response = new String(receivePacket.getData());

			/* close the socket */
			clientSocket.close();

			/* parse the IP from the response */
			/* the response should contain a line like:
Location:  http://192.168.1.9:8060/
and we're only interested in the address -- not the port.
So we find the line, then split it at the http:// and the : to get the address.
			 */
			response = response.toLowerCase();
			String address = response.split("location:")[1].split("\n")[0].split("http://")[1].split(":")[0].trim();

			/* return the address */
			return address;
		} catch (Exception e) {
			return "none";
		}
	}

	/**
	 * Scan the local network many times to find multiple Rokus
	 * @return a String array of Roku IP Addresses
	 */
	public void run() {
		String addr = "";

		/* loop will run until interrupted */
		for (int i = 1; i > 0; i++) {
			try {
				Thread.sleep(1);
				addr = scanForRoku();
				if (!addr.equals("none")) {
					if (!Scan.results.contains(addr)) {
						if (spontaneousOutput)
							System.out.println("\t" + addr);
						Scan.results.add(addr);
					}
				}
			} catch (InterruptedException e) {
				break;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Print a status message with a specific color & format
	 */
	private static void status(String msg) {
		if (verbose)
			System.out.println(BLUE + "[*] " + PURPLE + msg + NORMAL);
	}

}
