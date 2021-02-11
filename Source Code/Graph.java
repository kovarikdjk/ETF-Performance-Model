/*Graph class
 * will print a graph to an html file of stock historic data and auto opens the graph
 * uses google graphs format
 */
import java.io.*;
import java.awt.Desktop; //to auto open graph

public class Graph {
	//vars
		private FileWriter fw;
		private PrintWriter pw;
		private File file2;
		
	//constructor
		public Graph() throws IOException
		{
			File file = new File ("htmlWeb.html");
			String parent = file.getAbsoluteFile().getParent() + "\\htmlWeb.html";
			file2 = new File (parent);
			
			fw = new FileWriter (file2);
			pw = new PrintWriter (fw);
		}
	
	/*This method will get the historic data for each stock in each etf, and calculate the percent change from the 
	 *first date in the date up to that date in the data to show the percent change performance of that stock It does 
	 *this for each stock for each etf. This data will be average for every day in the data and then graphed. The graph 
	 *will be created by an algorithm that will write to a file the html for a google graph.
	 * 
	 * @param array of ETF classes that will be used to calcualte data
	 * @param the comparison ticker that is used to reference etf performance to the general market
	 * @param comparison stock name
	 * @param the datapoints in the graph (is the same as how many dates will be in the data)
	 */
		public void makeHistoricGraph (ETF[] etfs, String compareTicker, String compareName, int dataPoints) throws Exception
		{
			System.out.println ("\nStatus:");
			
			/*	-gets data for each stock in the etf, makes a HistoricStockData class for each stock
			  	-finds the daily percent change for each stock...  finds each daily percent change from the original data point
			  	-then puts into one 2d array which contains (Date, Percent change to date from first data point) for each etf
			  	-there are two of these arrays, one 2d for the etfs total average, and one 1D for the comparison index fund*/
		
			//creates HistoricStockData class for each stock: first level of array is each etf, second level is each stock in that etf
				
				//2d array of each ETF, with each Stock
					HistoricStockData[][] historicalData = new HistoricStockData[etfs.length][];
				
				//array of percent changes are initalized. The 2d array first level is everyday, and then 
					Double[][] etfsPercentChange = new Double[dataPoints][etfs.length];
				
				//finds the oldest data point for each stock. this is often the last one, but for new stocks it may not be
					double[][] oldest = new double[historicalData.length][];	
					
					for (int qw=0; qw<etfsPercentChange.length; qw++) {
						for (int wq=0; wq<etfsPercentChange[qw].length; wq++) {
							etfsPercentChange[qw][wq] = 0.0;
						}
					}
				
				//loop for each etf
					for (int x=0; x<historicalData.length; x++)
					{
						//declares amount of stocks in this indivdual etf
							historicalData[x] = new HistoricStockData [etfs[x].tickers.size()];
							oldest[x] = new double [etfs[x].tickers.size()];
						
						//loop for each stock in this etf
							for (int i=0; i<historicalData[x].length; i++)
							{
								//runs functions for this stock to get data and store it in class
									historicalData[x][i] = new HistoricStockData(etfs[x].tickers.get(i));
									historicalData[x][i].setData(dataPoints);
								
								//runs loop of all times to find the oldest one with a value
									for (int t=dataPoints-1; t>=0; t--) {
										if (historicalData[x][i].prices.get(t) == -1)
											continue;
										else {
											oldest[x][i] = historicalData[x][i].prices.get(t);
											//System.out.println (oldest[x][i]);
											break;
										}
										
									}
							}
					}
			//finds each change for each date, for each etf
				/*finds "to-date" %change from the latest date in the data set for each stock, 
				  averages for each etf, puts into array where each point coresponds to a date
				  
				  if there is a date where no data is avalible for the stock, then it is not counted into the 
				  average until data is avalible.
	
				  if the etf contained only recent ipo's, old data would be a flat line since no stock is
				  "purchased" in the etf										*/
				
				//loop for each date
					for (int t=0; t<dataPoints; t++) {
						//loop for each etf
						for (int x=0; x<historicalData.length; x++) {
							//var to count stocks in etf that have values
								int validStocks = 0;
								
							//loop for each stock
							for (int i=0; i<historicalData[x].length; i++) {
								//-1 is the value if there is no data for that date
								if (historicalData[x][i].prices.get(t) == -1)
									continue;
								else {
									double neww = historicalData[x][i].prices.get(t);
									etfsPercentChange[t][x] += (neww/oldest[x][i]) -1;
									validStocks++;
								}	
							}
							//all stocks in etf have changes added to them, now it is divided by the applicable stocks
							if (validStocks==0)
								etfsPercentChange[t][x] = 0.0;
							else
								etfsPercentChange[t][x] = (etfsPercentChange[t][x] / validStocks) * 100;
							
							//System.out.println (historicalData[x][0].dates.get(t) + "\t" + etfsPercentChange[t][x]);
						}
					}
			
			//put the comparison ETF into HistoricStockData and find percent changes to-date from first point
				HistoricStockData comparisonData = new HistoricStockData(compareTicker);
				comparisonData.setData(dataPoints);
				Double[] compareETFPercentChange = new Double[dataPoints];
				for (int t=0; t<dataPoints; t++)
				{
					compareETFPercentChange[t] = ((comparisonData.prices.get(t) / comparisonData.prices.get(comparisonData.prices.size()-1))-1) *100;
					//System.out.println (compareETFPercentChange[t] );
				}

				
			//puts all dates into one array in the proper format (from yyyy-mm-dd to mm/dd/yyyy)	
				DateFunctions df = new DateFunctions();
				String[] finalDate = new String[historicalData[0][0].dates.size()];
				for(int i=0; i<historicalData[0][0].dates.size(); i++) {
					finalDate[i] = df.changeDateFormat(historicalData[0][0].dates.get(i));
				}
				
			//prints it to the html document
			//help from:		https://developers.google.com/chart/interactive/docs/gallery/linechart
				pw.println ("  <html>");
				pw.println ("  <head>");
				pw.println ("   <script type=\"text/javascript\" src=\"https://www.gstatic.com/charts/loader.js\"></script>");
				pw.println ("  <script type=\"text/javascript\">");
				pw.println ("     google.charts.load('current', {'packages':['corechart']});");
				pw.println ("    google.charts.setOnLoadCallback(drawChart);");
	
				pw.println ("     function drawChart() {");
				pw.println ("      var data = google.visualization.arrayToDataTable([");
				
				
				//prints the titles of the names for the legend
				pw.print ("        ['Date', '");
				pw.print (compareName + "', '");
				for (int i=0; i< etfs.length; i++) {
					if (i==etfs.length-1)
						pw.println( etfs[i].getName() + "']," );
					else
						pw.print( etfs[i].getName() + "',  '" );
				}
				
				
				//prints the the percent changes and dates for each point
				for (int t=dataPoints-1; t>=0; t--) {
					pw.print ("        ['" + finalDate[t] + "',   ");
					
					for (int x=-1; x<etfs.length; x++) {
						if (x==-1)
							pw.print (compareETFPercentChange[t]  + ",   ");
						else if (x==etfs.length-1)
							pw.print (etfsPercentChange[t][x] );
						else
							pw.print (etfsPercentChange[t][x] + ",   ");
					}
					if (t==0)
						pw.println ("       ]");
					else
						pw.println ("       ],");
				}
				pw.println ("       ]);");
	
				
				pw.println ("      var options = {");
				pw.println ("       	title: 'Custom ETF(s) vs. " + compareName + "',");
				pw.println ("			vAxis: { title: \"Percent Change (%)\" },		");
				
				//can be removed to get rid of the curve in the graph so that the line follows each point exactly
				pw.println ("     	    curveType: 'function',");
				pw.println ("     	    legend: { position: 'bottom' }");
				pw.println ("       };");
	
				pw.println ("       var chart = new google.visualization.LineChart(document.getElementById('curve_chart'));");
	
				pw.println ("       chart.draw(data, options);");
				pw.println ("      }");
				pw.println ("    </script>");
				pw.println ("  </head>");
				pw.println ("  <body>");
				pw.println ("    <div id=\"curve_chart\" style=\"width: 1200px; height: 600px\"></div>");
				pw.println ("  </body>");
				pw.println ("</html>");
			
			
			
			pw.close();
			
			
			//opens the graph on the desktop main browser 
			//help from:		https://stackoverflow.com/questions/3657157/how-do-i-get-a-files-directory-using-the-file-object
			Desktop.getDesktop().browse(file2.toURI());
		}
}
