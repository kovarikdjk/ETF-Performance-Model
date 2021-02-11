/*
 * Quotes class
 * key is a custom key from the api given to registered user. it tracks the user usage (and if premium member)
 * class has key as a var, used by hstoric quotes class. this class has one method for getting today quote, but not historic
 */

import com.mashape.unirest.http.Unirest;//httprequest using the unirest package

public class Quotes {
	//vars				
		protected String key;
		
	//constructor
		public Quotes() {
			key = "0VY520XNMHVU293C";
		}
	
	//gets the price of one stock @param String of the ticker @returns double the most recent price of the stock
		public double getPrice (String ticker) throws Exception {
			String html = "https://www.alphavantage.co/query?function=GLOBAL_QUOTE&symbol=" + ticker.toUpperCase() +
							"&apikey=" + key;
			String body = Unirest.get(html)
					 .asString()
					 .getBody();
			
			int count = 0;
			//this length is if the message says that the quotes request has exceeded the max per minute/per day
			while (body.length() == 236) {
				count++;
				if (count>=2) {
					if (count==2)
						System.out.print ("Waiting for API servers");
					else {
						try
						{
							Thread.sleep(1000);
							System.out.print(".");
						}
						catch(InterruptedException ex)
						{
						    Thread.currentThread().interrupt();
						}
					}
				}
				body = Unirest.get(html)
						 .asString()
						 .getBody();
			}
				
			//parse is based on the api json format
			return Double.parseDouble (body.substring ((body.indexOf("price")+9), (body.indexOf(",",body.indexOf("price")+9 )-1) ));
		}

}
