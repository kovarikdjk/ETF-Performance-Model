/*HistoricStockData class
 * will get data back x dataPoints back in time from api and put the dates and prices on those dates into arraylists
 * precondition: ticker is already verified as valid
 */

import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

import java.time.LocalDateTime; //for local time
import java.time.format.DateTimeFormatter; //for local time format
import java.text.SimpleDateFormat;//format time input

import org.apache.commons.io.*;//easy read html
import java.net.URL;//http request alt2
import java.nio.charset.*; //format for package that is an easy way to read files into a string (that is faster)


public class HistoricStockData extends Quotes {
	//vars
		protected ArrayList<String> dates;
		protected ArrayList<Double> prices;
		private String ticker;
	
	//cosntructor @param string ticker for this class
		public HistoricStockData (String ticker) throws Exception
		{
			super();
			this.ticker = ticker;
			dates = new ArrayList<String>();
			prices = new ArrayList<Double>();
		}

	/*
	 * method sets the dates and prices for the class ticker
	 * @param dataPoints that the data will go back to
	 * gets data from api and fills in the array list
	 * if the stock does not have enough data (ie it is a newer stock than the requested data ex tsla, snap) it will 
	 *    fill in the dates from the dates.txt file, and -1 for the price. the -1 will indicate to the graph class not to use the 
	 *    data for that day
	 */
		public void setData (int dataPoints) throws Exception{
			String html = "";
			//saves data requests by getting the initial test data for the dates file from a demo source
			if (ticker.equalsIgnoreCase("TEST"))
				html = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol=IBM&outputsize=full&apikey=demo";
			else
				html = "https://www.alphavantage.co/query?function=TIME_SERIES_DAILY_ADJUSTED&symbol=" + 
			ticker.toUpperCase() + "&outputsize=full&apikey=" + key; 
			
			//the html file, whether it exists yet or not
				File stock = new File ("Stock Json Data\\" + ticker + ".html");
				String parent = stock.getAbsoluteFile().getParent() + "\\" + ticker + ".html";
				File file2 = new File (parent);
				
			/*wait function since this api is limited to 5 calls per minute. 
			  also checks if this quote has already been processed, to save time
			  if the quote exists already, then it is checked that it is upto date.
			  if it is upto date, then it reads data from the stock json data folder, where that api data is saved as an html file*/
				String body = "";
				
				try {
					//this is so the dates file is updated everytime the program is ran
						if (ticker.contentEquals("TEST")) {
							throw new FileNotFoundException();
						}
		
					//for speed instead of a loop with buffered reader:
					//help from: https://stackoverflow.com/questions/326390/how-do-i-create-a-java-string-from-the-contents-of-a-file					
						body = FileUtils.readFileToString(file2, StandardCharsets.UTF_8);

					//checks the date of the stock file and makes sure it is up to date	
					//https://mkyong.com/java/how-to-get-the-file-last-modified-date-in-java/
						DateTimeFormatter dtfdate = DateTimeFormatter.ofPattern("MM/dd/yyyy");
						DateTimeFormatter dtftime = DateTimeFormatter.ofPattern("HH:mm");
						LocalDateTime now = LocalDateTime.now();  
						String currentDate = now.format(dtfdate);
						String currentTime = now.format(dtftime);
						
						String fileDate = body.substring (body.indexOf("Last Refreshed")+18  ,  (body.indexOf("Last Refreshed")+18)+10);
						fileDate = changeDateFormat(fileDate);
						
						SimpleDateFormat dateformat = new SimpleDateFormat("MM/dd/yyyy");
						SimpleDateFormat timeformat = new SimpleDateFormat("HH:mm");					
						String fileDateSaved =  dateformat.format(file2.lastModified());
						String fileTime =  timeformat.format(file2.lastModified());
	
						
						/*finds the time difference between the today time and file save time. 
						  prevents reloading data due to api website not updating to current quotes for some stocks	
						  so if difference is less than 2 hours, it still uses it, even though the data is one day old */
							int currentMin = Integer.parseInt(currentTime.substring(0,2)) * 60;
							currentMin +=    Integer.parseInt(currentTime.substring(3,5));

							int fileMin = Integer.parseInt(fileTime.substring(0,2)) * 60;
							fileMin +=    Integer.parseInt(fileTime.substring(3,5));
			
							int timeDifference = currentMin - fileMin;

								
						
						//push it back to trading day, or stays the same if already a trading day
							DateFunctions dayf = new DateFunctions();
							currentDate = dayf.pushBackToTradingDay(currentDate);
							//System.out.println ("currentDate\t"  + ticker + "\t" + currentDate);
							
							DateFunctions dayf2 = new DateFunctions();
							fileDate = dayf2.pushBackToTradingDay(fileDate);
							//System.out.println ("fileDate\t"  + ticker + "\t" + fileDate + "\n");
						
							
						if(currentDate.equals(fileDate));	
						else {
							//only updates file if it has been 2 hours on the same day due to api not getting current data up fast
							if (currentDate.contentEquals(fileDateSaved) && timeDifference<120);
							else {
								body = "";
								//https://www.webucator.com/how-to/how-throw-an-exception-java.cfm
									throw new FileNotFoundException();
							}
						}
					
					//tells user data has been retrieved from files
						System.out.println ("Data Retrieved for " + ticker + " from Local Files");
						
				}
				//if that file does not exist because the stock is newly added to a new etf
				catch (FileNotFoundException e) {
					URL url = new URL(html);			        
					int count = 0;
					
					while (body.length()<300) {
						count++;
						
						//source: https://www.geeksforgeeks.org/download-web-page-using-java/
						//gets html from api and creates a new file. writes the data to the file so that spacing is consistent
							BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream())); 				
							BufferedWriter writer = new BufferedWriter(new FileWriter(file2));
						
						//reads in each line and writes to file2
					        String line; 
					        while ((line = reader.readLine()) != null) { 
					            writer.write(line); 
					        } 
					
					        reader.close(); 
					        writer.close(); 
						
					   //puts the newly created file into the string body
							body = FileUtils.readFileToString(file2, StandardCharsets.UTF_8);
						
						//if the api is being dumb due to the 5 per minute limit
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
					}
					if (count>=2)
						System.out.println();
					
					//tells user data has been retrieved from server
						if (!ticker.equals("TEST"))
							System.out.println ("Data Retrieved for " + ticker + " from Alpha Vantage");
				}
					
						
			
			//puts data into dates and prices array. parse is based on html that is downloaded. there are different spacing for the one directly from the website
				body = body.substring (body.indexOf("(Daily)")+12);
				dates.add(body.substring(8,18));
				prices.add (Double.parseDouble (body.substring (   (body.indexOf("adjusted close")+18)  ,  (body.indexOf(",",body.indexOf("adjusted close")+18 )-1))));
				//System.out.println (body.substring(8,18));
				//System.out.println (body.substring (   (body.indexOf("adjusted close")+18)  ,  (body.indexOf(",",body.indexOf("adjusted close")+18 )-1)));
				body = body.substring(body.indexOf("split")+10);
				//System.out.println ("1\t" + dates.get(0) + "\t" + prices.get(0));
		
				for (int i=1; i<=dataPoints-1; i++) {	
					try {
						dates.add(		(body.substring(body.indexOf("},")+11  ,  body.indexOf(":"  ,  body.indexOf("},")+12 )-1))   		);
						prices.add(		Double.parseDouble (body.substring(body.indexOf("adjusted close")+18  ,  body.indexOf("volume"  ,  body.indexOf("adjusted close")+18 )-18     ))   		);
						//System.out.println (body.substring(body.indexOf("},")+11  ,  body.indexOf(":"  ,  body.indexOf("},")+12 )-1));
						//System.out.println ( (body.substring(body.indexOf("adjusted close")+18  ,  body.indexOf("volume"  ,  body.indexOf("adjusted close")+18 )-18     )) );
						body = body.substring(body.indexOf("split")+10);
						//System.out.println ((i+1) + "\t" + dates.get(i) + "\t" + prices.get(i));
					}
					//if the stock is too new it will get dates from the list of dates and add them to the array
					catch (IndexOutOfBoundsException e) {					
						//reads dates from lists and fills in empty data
							File file = new File ("dates.txt");
							String parent2 = file.getAbsoluteFile().getParent() + "\\dates.txt";
							File datesFile = new File (parent2);
							
							FileReader fr = new FileReader (datesFile);
							Scanner sc = new Scanner (fr);
							for (int x=0; x<i; x++)
								sc.nextLine();
							
							for (int x=i; x<5033; x++)
							{
								dates.add (sc.nextLine());
								prices.add(-1.0);
								//System.out.println ((x+1) + "\t" + dates.get(x) + "\t" + prices.get(x));
							}
							break;
					}
				}	
		}

	//converts format of date  from yyyy-mm-dd to mm/dd/yyyy
		private String changeDateFormat(String date) {
			String year = date.substring(0,4);
			String month = date.substring(5,7);
			String day = date.substring(8,10);
			
			return (month + "/" + day + "/" + year);
		}
	
}
