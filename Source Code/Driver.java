/*/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
 *ETF Creator v1.0
 *This program allows the user to create and track custom ETF's
 *It provides the user the option to track an ETF from it's inception date, or over a custom time-line
 *The program will create an html graph of the ETF(s) based on the parameters the user chooses
 *
 *This Program uses Alpha Vantage API to get historic data, and Google graphs to make the html graph
 *This Program requires an internet connection to produce the graph and get API quotes from the server
 *
 *
 *Created by DJ Kovarik
 *5/19/2020
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////*/


/*
 * Driver Class
 * this class is the main class that is the starting point for the program
 * it gets user input for what menu option they want
 * allows user to choose to create an etf, get a graph, get help, or exit the program
 */

import java.io.*;
import java.util.Scanner;

public class Driver 
{
	public static void main(String[] args) throws Exception
	{
		//scanner
			Scanner sc = new Scanner (System.in);
		
		//welcome
			System.out.println ("Welcome to ETF Creator!\n\nYou can use this program to create a list of equities"
					+ " from the NYSE, NASDAQ, and AMEX exchanges. \nEach stock will be equally weighted in your custom ETF."
					+ "\n\nPlease wait while the program loads...");
		
		//menu
			String[] menu = {"Create New ETF", "Check ETF Performance To-Date", "Calculate ETF Historic Performance", "Help", "Exit"};
		
		//sets up dates file so that the file can be referenced by other classes
			File file = new File ("dates.txt");
			String parent = file.getAbsoluteFile().getParent() + "\\dates.txt";
			File file2 = new File (parent);

			//updates the list of dates to include most recent dates and exclude expired data	
				HistoricStockData datesList = new HistoricStockData ("TEST");
				
			//5033 is the max data from the API, which is for 20 years
				datesList.setData(5033);
		
			FileWriter fw = new FileWriter (file2);
			PrintWriter pw = new PrintWriter (fw);
			for (int i=0; i<datesList.dates.size(); i++) {
				if (i==datesList.dates.size()-1)
					pw.print (datesList.dates.get(i));
				else
					pw.println (datesList.dates.get(i));
			}
			fw.close();
			
		
		//loop for menu options
		boolean loop = true;
		do
		{
			//get menu selection
				System.out.println ("\nPlease make a selection");
				for (int i=0; i<menu.length; i++)
					System.out.println ((i+1) + ".\t" + menu[i]);
				int selection = sc.nextInt();
				while (selection<1 || selection>menu.length)
				{
					System.out.println("\nThis is not a valid menu selection. Enter a different selection");
					selection = sc.nextInt();
				}
			
			//if user chooses options one
			if (selection==1)
			{
				//list of all previously used etf names so there are no repeats
					Read r = new Read();
					String[] names = new String[r.allETF.size()];
					for (int i=0; i<r.allETF.size();i++) {
						names[i] = r.allETF.get(i).getName();
					}
				Write w = new Write();
				w.getETFInfo(names);
			}
			else if (selection==2)
			{
				Read r = new Read();
				r.getPerformanceGraph();
			}
			else if (selection==3)
			{
				Read r = new Read();
				r.getHistoricGraph();
			}
			else if (selection ==4) {
				System.out.println ("\n\nThis program allows you to create custom ETF's. ETF stands for Exchange Traded Fund, and is a "+
										"collection of stocks, commodities, or bonds \nwhich trades at a price that is close to the value of it's holdings. " +
										"Different ETF's serve different purposes. Some follow a sector, the \nS&P 500, gold prices, or many other things. This " +
										"app will allow you to create an ETF with the purpose of all the equities falling into a \ncatagory, or you can just " +
										"choose stocks that have no corolation, it's up to you.");
				
				System.out.println ("\nHelpful Definitions:\nTicker:\tThe ticker symbol is the abbreviation that an equity is traded under. EX: Apple Inc. is traded as AAPL");
				System.out.println ("Equity:\tAnother word for stock.");
				System.out.println("Bond:\tA type of loan that a company or government sells where interest is paid on a set date when the bond \"matures\" instead of periodically.");
				
				try
				{
					Thread.sleep(11000);
				}
				catch(InterruptedException ex)
				{
				    Thread.currentThread().interrupt();
				}
			}
			else if (selection==5)
			{
				System.out.println ("\n\n\nGoodbye, Thank You for using ETF Creator!");
				loop = false;
			}
		}while (loop);
		
					
			
	}

}
