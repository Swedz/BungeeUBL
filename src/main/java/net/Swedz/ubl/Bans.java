package net.Swedz.ubl;

import java.io.IOException;
import java.io.StringReader;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import net.md_5.bungee.api.ProxyServer;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Bans {
	private static final String URL = "https://docs.google.com/spreadsheet/ccc?key=0AjACyg1Jc3_GdEhqWU5PTEVHZDVLYWphd2JfaEZXd2c&output=csv";
	public static HashMap<String, String> list = new HashMap<String, String>();
	public static boolean updating = false;
	
	public Bans() {
		updating = true;
		list = new HashMap<String, String>();
		
		ProxyServer.getInstance().getScheduler().runAsync(Main.instance(), new Runnable() {
			public void run() {
				OkHttpClient client = new OkHttpClient.Builder()
	                .followRedirects(true)
	                .retryOnConnectionFailure(true)
	                .readTimeout(5*1000, TimeUnit.SECONDS)
		        .build();

		        Request.Builder request = new Request.Builder()
	                .url(URL)
	                .header("Accept-Language", "en-US,en;q=0.8")
	                .header("User-Agent", "Mozilla")
	                .header("Referer", "google.com");
	        	System.out.println("[BungeeUBL]: Gathering all of the UBL...");
		        
		        Response response = null;
		        try {
		        	response = client.newCall(request.build()).execute();
		        } catch (Exception ex) {
		        	System.out.println("[BungeeUBL]: Error occurred while requesting the UBL.");
		        	ex.printStackTrace();
		        }
		        
		        try {
		        	String data = response.body().string();
		        	list = Parser.parseBans(data);
		        	updating = false;
		        	System.out.println("[BungeeUBL]: Loaded all current UBL punishments (" + list.size() + " total).");
		        } catch (Exception ex) {
		        	System.out.println("[BungeeUBL]: Error occurred while reading the UBL.");
		        	ex.printStackTrace();
		        }
			}
		});
	}
	
	public static class Parser {
		public static HashMap<String, String> parseBans(String data) throws IOException {
	    	HashMap<String, String> bans = new HashMap<String, String>();
	        CSVParser parser = CSVFormat.EXCEL.withHeader().parse(new StringReader(data));
	        for(CSVRecord csvRecord : parser) {
	        	try {
		        	UUID uuid = null;
		            try {
		                uuid = UUID.fromString(csvRecord.get("UUID"));
		            } catch (Exception e) {
		                continue;
		            } if(uuid != null)
		            	bans.put(uuid.toString(), csvRecord.get("Reason"));
	        	} catch (Exception ex) {
	        		System.out.println("[BungeeUBL]: Error occurred while reading the Ban List.");
	        		ex.printStackTrace();
	        	}
	        } return bans;
	    }
	}
}
