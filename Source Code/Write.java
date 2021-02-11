/*Write class
 * class will allow for writing to the data file, and writes new etfs to it
 * method allows for user input to be received and used to write a new etf to data file
 * writes in the format of...	Name:MM/DD/YYYY:ticker,ticker,ticker,;
 */

import java.io.*;

import java.time.format.DateTimeFormatter;  
import java.time.LocalDateTime;  //for getting local date to write to the file

public class Write extends Interface
{	
	//vars
		private FileWriter fw;
		private PrintWriter pw;
		private Quotes q;
		
		private ETF etf;
		
	//constructor
		public Write () throws Exception
		{		
			super();
			
			fw = new FileWriter (file2, true);
			pw = new PrintWriter (fw);
			q = new Quotes();
			
			etf = new ETF();
		}
		
	
	/*gets data from the user about the etf name and tickers it will contain and adds the ETF to data.txt
	  @param String array of names already used					*/
		public void getETFInfo (String[] takenNames) throws Exception
		{
			//writes the name
				System.out.println ("\nWhat is the name of your ETF?");
				etf.setName (sc.nextLine().trim());
				
				boolean badName = false;
				do
				{
					badName = false;
					//checks if name is already used
					for (int i=0; i<takenNames.length; i++) {
						if (takenNames[i].equalsIgnoreCase(etf.getName())) {
							System.out.println ("\nThis is not a valid name. You must use a name not already being used.");
							badName = true;
							break;
						}
					}
					//checks it is only letters (other symbols cause issue with html code)
					if (etf.getName().isEmpty() || !isLettersOnly(etf.getName())  ||  badName) {
						if (!badName)
							System.out.println ("\nThis is not a valid name. The name must only contain letters.");
						badName = true;
						etf.setName (sc.nextLine());
					}
				}while (badName);
			
			//gets the tickers
				int count = 0;
				String ticker = "";
				double price = 0;
				while (true)
				{
					/*
					 * try-catch loop checks the ticker name is valid. it asks the api to get a quote, and if it does not exist, the etf returns a messgage that it is not valid. 
					 * During the parse in the quotes class, if it is not in the right format (ie the error message has been returned by the api), 
					 * the parse does not work, and throws the indeoutofbounds exception
					 */
					try
					{
						System.out.println ("\nEnter an equity from the NYSE, NASDAQ, or AMEX. Type \"Exit\" to stop adding tickers. Make sure to include and \"-\" or \".\"");
						ticker = sc.nextLine();
						if (ticker.equalsIgnoreCase("exit"))
							break;
						else 
							price = (q.getPrice(ticker));
					} 
					catch (IndexOutOfBoundsException e)
					{					
						System.out.println ("\nThis is not a valid ticker. Make sure it is listed on one of the eligable stock exchanges, and you type in any symbols neccesary.");
						if (ticker.equalsIgnoreCase("exit"))
							break;
						else 
							continue;
					}
					
					
					ticker = ticker.toUpperCase();
					System.out.println ("\n" + ticker + " current price is " + price + " but it will be \"purchased\" for the end of day price.");
					etf.tickers.add(ticker);
				}
			
				//sets date to current date
				//https://www.javatpoint.com/java-get-current-date
				   DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");  
				   LocalDateTime now = LocalDateTime.now();  
				   etf.date = now.format(dtf);
				   
			//writes it to data file
				if (!(etf.tickers.size() == 0))
				   writeToFile();
		}
	
	//writes ETF to data.txt
		private void writeToFile() throws IOException
		{			
			pw.print (etf.getName() + ":" + etf.date + ":");
			for (int i=0; i<etf.tickers.size(); i++) {
				pw.print (etf.tickers.get(i) + ",");
			}
			pw.println (";");
			
			fw.close();
		}
	
	//checks that the title is only letters @returns true if only letters @param string that is checked
		private boolean isLettersOnly (String str) {
			for (int i=0; i<str.length(); i++) {
				Character c = (str.charAt(i));
				if ( !Character.isLetter(c) && !Character.isWhitespace(c)  )
					return false;
			}
			return true;
		}
}
