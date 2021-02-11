/*
 * Read class
 * This class will read the data file and make a list of ETF's
 * has methods that utilize the Graph class to create a custom graph
 * user input on graph variables is got here
 */

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Read extends Interface
{
	//vars
		private FileReader fr;
		protected ArrayList<ETF> allETF;
		private Scanner scanData;
		private Graph g;
	
	//constructor
		public Read() throws Exception
		{
			super();
			
			fr =new FileReader (file2);
			allETF  = new ArrayList<ETF>();
			scanData = new Scanner (fr);
			g = new Graph();
			
			//reads in data and adds it to an array list
			while (scanData.hasNextLine())
			{
				String line = scanData.nextLine();
				String name = "";
				String date = "";
				ArrayList<String> tickers = new ArrayList<String>();
				
				name = line.substring(0, line.indexOf(":"));
				line = line.substring( line.indexOf(":")+1);
				date = line.substring(0, line.indexOf(":"));
				line = line.substring( line.indexOf(":")+1);
				
				boolean semicolon = false;
				while (!semicolon)
				{
					if (line.equals(";"))
						semicolon = true;
					else
					{
						tickers.add(line.substring(0, line.indexOf(",")));
						line = line.substring( line.indexOf(",")+1);
					}
				}
				allETF.add (new ETF(name, date, tickers));
			}
		}
		
		
		
		
	//method will ask user which ETF they want to see performance of to-date and call the graph class
		public void getPerformanceGraph() throws Exception {
			//print all etfs left
				System.out.println("\nAvalible Custom ETF's:");
				
				for (int i=0; i<allETF.size(); i++) {
					String toPrint = "";
					toPrint += ((i+1) + ".\t" + allETF.get(i).getName() + ": ");
							
					for (int x=0; x<allETF.get(i).tickers.size(); x++) {
						if (x==6) {
							toPrint += (allETF.get(i).tickers.get(x) + " ...");
							break;
						}
						else if (x == allETF.get(i).tickers.size()-1)
							toPrint += (allETF.get(i).tickers.get(x));
						else
							toPrint += (allETF.get(i).tickers.get(x) + ", ");
					}
					System.out.printf (  "%-55s%s%n", toPrint, ("(Date Created: " + allETF.get(i).date + ")") );
				}
			
			//asks user which they want to add
				System.out.println("\nChoose an ETF to see the performance of it to-date.");
				int etfChoice = sc.nextInt();
				while(etfChoice<1 || etfChoice>(allETF.size())) {
					System.out.println ("\nThis is not a valid choice. Enter a choice from the menu.");
					etfChoice = sc.nextInt();
				}
				ETF[] etfs = new ETF[1];
				etfs[0] = allETF.get(etfChoice-1);
			
			//calculates the data points from the date the etf was created to today
				String startDate = etfs[0].date;
				DateFunctions df = new DateFunctions();
				startDate = (df.pushBackToTradingDay(startDate));
				int dataPoints = df.searchDataPointsBack(startDate);
			
			//gets the comparision ticker and name
				String rawDataCompare = getCompareStock();
				String compareTicker = rawDataCompare.substring(0,rawDataCompare.indexOf("+"));
				String compareName  = rawDataCompare.substring(rawDataCompare.indexOf("+")+1);
				
			//prints the time it took (for performance reference)
				long startTime = System.nanoTime();
				g.makeHistoricGraph (etfs, compareTicker, compareName, dataPoints);
				long endTime = System.nanoTime();
				System.out.println ("\nIt took " + ((endTime - startTime)/1000000000.0) + " seconds to create this graph");
		}
		
		
		
		
	//method will ask user which ETF they want to see historic performance of and get the graph
		public void getHistoricGraph() throws Exception
		{
			
			//asks what etfs they want to use
				ArrayList<ETF> tempETF = new ArrayList<ETF>();
				boolean exit = false;
				while(!exit) {
					
					//print all etfs left (etf's already selected are removed from the list)
						System.out.println("\nAvalible Custom ETF's:");
						
						for (int i=0; i<allETF.size(); i++) {
							System.out.print ((i+1) + ".\t" + allETF.get(i).getName() + ": ");
							
							for (int x=0; x<allETF.get(i).tickers.size(); x++) {
								if (x==6) {
									System.out.println (allETF.get(i).tickers.get(x) + " ...");
									break;
								}
								else if (x == allETF.get(i).tickers.size()-1)
									System.out.println (allETF.get(i).tickers.get(x));
								else
									System.out.print (allETF.get(i).tickers.get(x) + ", ");
							}
						}
						System.out.println((allETF.size()+1) + ".\tEXIT");
					
					//asks user which they want to add
						System.out.println("\nChoose an ETF to include in the data.");
						int etfChoice = sc.nextInt();
						
						while(etfChoice<1 || etfChoice>(allETF.size()+1)) {
							System.out.println ("\nThis is not a valid choice. Enter a choice from the menu.");
							etfChoice = sc.nextInt();
						}
						if (etfChoice == (allETF.size()+1) ) {
							if (tempETF.size() == 0)
								return;
							else 
								exit = true;
						}	
						else {
							//adds the selected to a list that will be graphed, and removes that same one from the list of all etfs
								tempETF.add(allETF.get(etfChoice-1));
								allETF.remove(etfChoice-1);
						}
		
				}//end while loop
				
				//puts the temp arraylist into an array of ETF
					ETF[] etfs = new ETF[tempETF.size()];
					for (int i=0; i<etfs.length; i++) {
						etfs[i] = tempETF.get(i);
					}

					
					
			//dataPoints var
				int dataPoints = 0;
			
			//gets the start date for data from the user, based on what type of parameter they want to use
				System.out.println ("\nChoose how you want to set the parameters of the data\n1.\tEnter a starting date\n2.\tEnter a time frame");
				int input = sc.nextInt();
				while(input<1 || input>2) {
					System.out.println ("\nThis is not a valid choice.");
					input = sc.nextInt();
				}
				
				DateFunctions df = new DateFunctions();
				//if the user wants to enter a start date for the data
				if (input == 1) {
					System.out.println ("\nEnter a starting date with the format mm/dd/yyyy. EX: if the date is Jan. 2nd, 2020 then type: 01/02/2020");
					sc.nextLine();
					String date = sc.nextLine();
					
					while (!df.isTrading(date)) {
						System.out.println ("\nThis is not a valid date. Make sure the date you entered is a day the market was open and you used the correct format.");
						date = sc.nextLine();
					}
				
					dataPoints = df.searchDataPointsBack(date);
				}
				//if they want to enter a time unit (EX: x years or x days)
				else if (input == 2) {
					System.out.println ("\nWhat unit of time do you want to use?\n1.\tDays\n2.\tMonths\n3.\tYears\n4.\tTrading Days");
					input = sc.nextInt();
					while(input<1 || input>4) {
						System.out.println ("\nThis is not a valid choice. Enter a choice from the menu.");
						input = sc.nextInt();
					}
					String timeUnit = "";
					if (input == 1)
						timeUnit = "Days";
					else if (input == 2)
						timeUnit = "Months";
					else if (input == 3)
						timeUnit = "Years";
					else if (input == 4)
						timeUnit = "Trading Days";					
					
					System.out.println ("\nHow many " + timeUnit + " will the data go back?");
					int userUnitAmount = sc.nextInt();
	
					
					if (input == 1) {
						while (userUnitAmount>7300 || userUnitAmount<1) {
							System.out.println ("\nThis is an invalid input. Enter a number from 1-7300 Days.");
							userUnitAmount = sc.nextInt();
						}
						dataPoints = df.setBackDays(userUnitAmount);
					}						
					else if (input == 2) {
						while (userUnitAmount>240 || userUnitAmount<1) {
							System.out.println ("\nThis is an invalid input. Enter a number from 1-240 Months.");
							userUnitAmount = sc.nextInt();
						}
						dataPoints = df.setBackMonths(userUnitAmount);
						
					}					
					else if (input == 3) {
						while (userUnitAmount>20 || userUnitAmount<1) {
							System.out.println ("\nThis is an invalid input. Enter a number from 1-20 Years.");
							userUnitAmount = sc.nextInt();
						}
						dataPoints = df.setBackYears(userUnitAmount);
					}
					else if (input == 4) {
						while (userUnitAmount>5033 || userUnitAmount<1) {
							System.out.println ("\nThis is an invalid input. Enter a number from 1-5033 Trading Days.");
							userUnitAmount = sc.nextInt();
						}
						dataPoints = userUnitAmount;
					}
				}			
			
				
				
			//gets the comparisions reference ticker and name
				String rawDataCompare = getCompareStock();
				String compareTicker = rawDataCompare.substring(0,rawDataCompare.indexOf("+"));
				String compareName  = rawDataCompare.substring(rawDataCompare.indexOf("+")+1);

			//prints the time it took (for performance reference)	
				long startTime = System.nanoTime();
				g.makeHistoricGraph (etfs, compareTicker, compareName, dataPoints);
				long endTime = System.nanoTime();
				System.out.println ("\nIt took " + ((endTime - startTime)/1000000000.0) + " seconds to create this graph");
		}
	
		
		
	//@returns the ticker+the stock name that will be used to compare the etf to. offers option of 3 different index funds that follow the general market
		private String getCompareStock() {
			//ask what the user wants to compare it to
			String compareName = "";
			String compareTicker = "";
			System.out.println ("\nDo you want to compare this ETF to:\n1.\tSPDR S&P 500 ETF Trust" +
								"\n2.\tNASDAQ PowerShares QQQ Trust" +
								"\n3.\tDow Jones Industrial Average ETF");
			
			int input = sc.nextInt();
			while(input<0 || input>3) {
				System.out.println ("\nThis is not a valid choice.");
				input = sc.nextInt();
			}
			
			if (input == 1) {
				compareName = "SPDR S&P 500 ETF Trust";
				compareTicker = "SPY";
			}
			else if (input == 2) {
				compareName = "NASDAQ PowerShares QQQ Trust";
				compareTicker = "QQQ";	
			}
			else if (input == 3) {
				compareName = "Dow Jones Industrial Average ETF";
				compareTicker = "DIA";
			}
			
			return compareTicker + "+" + compareName;
		}
	
	
	
	
	
	
}
