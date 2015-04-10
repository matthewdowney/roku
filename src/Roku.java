import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.lang.Thread;

public class Roku {
	private final String USER_AGENT = "Mozilla/5.0";
	private String ROKU_URL = "";
	private int ROKU_PORT = 8060;
	private int TYPE_DELAY = 500;

        /* constants for changing terminal output color */
        private static final String BLUE = "\033[94m";
        private static final String PURPLE = "\033[95m";
        private static final String NORMAL = "\033[0m";

	public static void main(String[] args) {
		
	}
	
	public Roku(String URL) {
		ROKU_URL = "http://" + URL.replace("http://", "").replace("https://", "").replace("/", "") + ":" + ROKU_PORT;
		if (URL.trim() != "") {
			System.out.println(BLUE + "[*] " + PURPLE + "Verifying connection..." + NORMAL);
			if (verify(ROKU_URL))
				System.out.println(BLUE + "[*] " + PURPLE + "Roku connection verified." + NORMAL);
			else
				System.out.println(BLUE + "[*] " + PURPLE + "Roku connection could not be verified -- either there is not a Roku at this address or it is unresponsive. Commands may still be issued." + NORMAL);
		}
	}
	
	public Roku(String URL, int port) {
		ROKU_PORT = port;
		ROKU_URL = "http://" + URL.replace("http://", "").replace("https://", "").replace("/", "") + ":" + ROKU_PORT;
		if (URL.trim() != "") {
			System.out.println(BLUE + "[*] " + PURPLE + "Verifying connection..." + NORMAL);
			if (verify(ROKU_URL))
				System.out.println(BLUE + "[*] " + PURPLE + "Roku connection verified." + NORMAL);
			else
				System.out.println(BLUE + "[*] " + PURPLE + "Roku connection could not be verified -- either there is not a Roku at this address or it is unresponsive. Commands may still be issued." + NORMAL);
		}
	}
	
	public boolean verify(String rokuURL) {
		try {
			URL url = new URL(rokuURL);
			InputStream is = url.openStream();
			int ptr = 0;
			StringBuffer buffer = new StringBuffer();
			while ((ptr = is.read()) != -1) {
			    buffer.append((char)ptr);
			}
			String data = buffer.toString();
			if (data.toLowerCase().contains("roku"))
				return true;
			return false;
		} catch (Exception e) {
			return false;
		}
	}
	
	public String keypress(String key) {
		return sendPost(ROKU_URL + "/keypress/" + key);
	}
	
	public void wait(int i) throws InterruptedException {
		Thread.sleep(i);
	}
	
	public String netflix() {
		return sendPost(ROKU_URL + "/launch/12");
	}
	
	public String amazon() {
		return sendPost(ROKU_URL + "/launch/13");
	}
	
	public String keyup(String key) {
		return sendPost(ROKU_URL + "/keyup/" + key);
	}
	
	public String keydown(String key) {
		return sendPost(ROKU_URL + "/keydown/" + key);
	}
	
	public String enterString(String str) {
		String[] s = str.toLowerCase().replace(" ", "+").split("");
		try {
			for (String t : s) {
				keypress("Lit_" + t);
				Thread.sleep(TYPE_DELAY);
			}
			return "true";
		} catch(Exception e) {
			return "false";
		}
	}

	public void setDelay(int d) {
		TYPE_DELAY = d;
	}
	private String sendPost(String url) {
		try {
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	 
			//add reuqest header
			con.setRequestMethod("POST");
			con.setRequestProperty("User-Agent", USER_AGENT);
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
	 
			String urlParameters = "";
	 
			// Send post request
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			wr.writeBytes(urlParameters);
			wr.flush();
			wr.close();
	 
			// int responseCode = con.getResponseCode();  
			BufferedReader in = new BufferedReader(
					new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			return "true";
		} catch (Exception E) {
			return "false";
		}
	}
}
