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
		private static int getMonthIndex(String m) {
			switch(m) {
				case "January": return 1;
				case "February": return 2;
				case "March": return 3;
				case "April": return 4;
				case "May": return 5;
				case "June": return 6;
				case "July": return 7;
				case "August": return 8;
				case "September": return 9;
				case "October": return 10;
				case "November": return 11;
				case "December": return 12;
				default: return 0;
			}
		}
		
		private static String getMonthByIndex(int i) {
			switch(i) {
				case 1: return "January";
				case 2: return "February";
				case 3: return "March";
				case 4: return "April";
				case 5: return "May";
				case 6: return "June";
				case 7: return "July";
				case 8: return "August";
				case 9: return "September";
				case 10: return "October";
				case 11: return "November";
				case 12: return "December";
				default: return null;
			}
		}
		
		private static boolean isNumber(String num) {
			try {
				int id = Integer.parseInt(num);
				return true;
			} catch (Exception ex) {
				return false;
			}
		}
		
		private static String formatDate(String date) {
        	date = date.replace(".", ",");
        	while(date.contains(" ") || date.contains(" "))
        		date = date.replace(" ", ",").replace(" ", ",");
        	
        	String newDate = "";
        	boolean firstNumber = true;
        	for(String ld : date.split("")) {
        		boolean isNumber = isNumber(ld);
        		if(isNumber && firstNumber) {
        			firstNumber = false;
        			newDate += ","+ld;
        		} else
        			newDate += ld;
        	} newDate = newDate.replace(",,", ",");
        	
        	return newDate;
		}
		
		public static HashMap<String, String> parseBans(String data) throws IOException {
	    	HashMap<String, String> bans = new HashMap<String, String>();
	        CSVParser parser = CSVFormat.EXCEL.withHeader().parse(new StringReader(data));
	        for(CSVRecord csvRecord : parser) {
	        	String expiry = formatDate(csvRecord.get("Expiry Date"));
	        	
	        	try {
		        	int eMonth = getMonthIndex(expiry.split(",")[0]);
		        	int eDay = Integer.parseInt(expiry.split(",")[1]);
		        	int eYear = 0;
		        	try {
		        		eYear = Integer.parseInt(expiry.split(",")[3]);
		        	} catch (ArrayIndexOutOfBoundsException ex1) {
		        		try {
		        			eYear = Integer.parseInt(expiry.split(",")[2]);
		        		} catch (ArrayIndexOutOfBoundsException ex2) {
		        			eYear = eDay;
		        			eDay = 1;
		        		}
		        	}
		        	
		        	Calendar cal = Calendar.getInstance();
		        	cal.setTime(new Date());
		        	int month = cal.get(Calendar.MONTH);
		        	int day = cal.get(Calendar.DAY_OF_MONTH);
		        	int year = cal.get(Calendar.YEAR);
		        	
		        	if(year > eYear)
		        		continue;
		        	if(month == eMonth && day >= eDay && year >= eYear)
		        		continue;
		        	if(month > eMonth && year >= eYear)
		        		continue;
		        	
		            UUID uuid;
		            try {
		                uuid = UUID.fromString(csvRecord.get("UUID"));
		            } catch (Exception e) {
		                continue;
		            } if(uuid != null)
		            	bans.put(uuid.toString(), csvRecord.get("Reason"));
	        	} catch (Exception ex) {
	        		System.out.println("[BungeeUBL]: Error occurred while reading the Ban List.");
	        		System.out.println("[Debug]: " + expiry);
	        		ex.printStackTrace();
	        	}
	        } return bans;
	    }
	}
}
