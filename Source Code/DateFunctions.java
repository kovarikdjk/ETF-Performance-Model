/*DateFunctions class
 * provides needed methods to various classes that are based around doing something with the list of trading dates (dates.txt)
 * also works to provide functions to get how many dataPoints there are until a certain date
 * 
 * Precondition: dates.txt is up to date
 */
import java.io.*;
import java.util.Scanner;

//for getting current date
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateFunctions {
	//vars
		private File file2;
		
	//constructor
		public DateFunctions() throws FileNotFoundException {
			File file = new File ("dates.txt");
			String parent = file.getAbsoluteFile().getParent() + "\\dates.txt";
			file2 = new File (parent);
		}
		

	/*pushes date back to the last trading day. if already a trading day, it does nothing and returns the same input
	  @param string date that is the starting point
	  @returns string the new date after it is pushed back to a trading day*/
		public String pushBackToTradingDay (String date) throws Exception {		
			while (!isTrading(date)) {
				int year = Integer.parseInt(date.substring(6,10));
				int day = Integer.parseInt(date.substring(3,5));
				int month = Integer.parseInt(date.substring(0,2));
				
				day--;
				if (day<1) {
					day = 31;
					month--;
				}
				if (month<1) {
					month = 12;
					year--;
				}
				
				if (month<10)
					date = "0" + month;
				else
					date = "" + month;
				
				if (day<10)
					date += "/0" + day;
				else
					date += "/" + day;
				
				date += "/" + year;	
				
			}
			return date;
		}
	
	//checks if it is a trading day @returns true if valid date @param date with format mm/dd/yyyy
		public boolean isTrading (String date) throws Exception {
			boolean isTrading = false;
			
			FileReader fr = new FileReader (file2);
			Scanner sc = new Scanner (fr);
			
			for (int i=0; i<5033; i++) {
				String line = changeDateFormat(sc.nextLine());
				
				if (date.contentEquals(line)) {
					isTrading = true;
					break;
				}
			}
			return isTrading;
		}

	//converts format of date  from yyyy-mm-dd to mm/dd/yyyy
		public String changeDateFormat(String date) {	
			String year = date.substring(0,4);
			String month = date.substring(5,7);
			String day = date.substring(8,10);
			
			return (month + "/" + day + "/" + year);
		}
		
	/*takes date and sets it back @param int days, then sets it to the last previous trading day from that day
	  @returns dataPoints (based on the dates) to go back to*/
		public int setBackDays (int daysBack) throws Exception{
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
			LocalDateTime now = LocalDateTime.now();
			String date = now.format(dtf);

			int year = Integer.parseInt(date.substring(6,10));
			int day = Integer.parseInt(date.substring(3,5));
			int month = Integer.parseInt(date.substring(0,2));

			for (int i=0; i<daysBack; i++){
				day--;

				if (day<1) {
					month--;
					
					if (month<1) {
						month = 12;
						year--;
					}
					
					if (month== 1 || month== 3 || month== 5 || month== 7 || month== 8 || month== 10 || month== 12)
						day = 31;
					
					else if (month== 4 ||month== 6 ||month== 9 ||month== 11)
						day = 30;
					
					else if (month== 2) {
						if (year%4 == 0)
							day = 29;
						else
							day = 28;
					}
				}
			}
			
			if (month<10)
				date = "0" + month;
			else
				date = "" + month;

			if (day<10)
				date += "/0" + day;
			else
				date += "/" + day;

			date += "/" + year;
			date = (pushBackToTradingDay(date));
			return searchDataPointsBack(date);
		}
		
	/*takes date and sets it back @param months, then sets it to the last previous trading day
	  @returns dataPoints (based on the dates) to go back to*/
		public int setBackMonths (int monthsBack) throws Exception {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
			LocalDateTime now = LocalDateTime.now();
			String date = now.format(dtf);

			int year = Integer.parseInt(date.substring(6,10));
			int day = Integer.parseInt(date.substring(3,5));
			int month = Integer.parseInt(date.substring(0,2));

			for (int i=0; i<monthsBack; i++){
				month--;

				if (month<1) {
					month = 12;
					year--;
				}
			}
			if (month<10)
				date = "0" + month;
			else
				date = "" + month;

			if (day<10)
				date += "/0" + day;
			else
				date += "/" + day;

			date += "/" + year;
			date = (pushBackToTradingDay(date));
			return searchDataPointsBack(date);			
		}
	
	/*takes date and sets it back @param years, then sets it to the last previous trading day
	  @returns dataPoints (based on the dates) to go back to*/
		public int setBackYears (int yearsBack) throws Exception {
			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy");
			LocalDateTime now = LocalDateTime.now();
			String date = now.format(dtf);

			int year = Integer.parseInt(date.substring(6,10));
			int day = Integer.parseInt(date.substring(3,5));
			int month = Integer.parseInt(date.substring(0,2));

			for (int i=0; i<yearsBack; i++){
				year--;
			}
			if (month<10)
				date = "0" + month;
			else
				date = "" + month;

			if (day<10)
				date += "/0" + day;
			else
				date += "/" + day;

			date += "/" + year;
			date = (pushBackToTradingDay(date));
			return searchDataPointsBack(date);			
		}
		
	/*searches how many data points back a date is and @returns int dataPoints from current date
	@param string of the date to go back to. starts at most recent date	*/
		public int searchDataPointsBack (String date) throws FileNotFoundException {
			File filedates = new File ("dates.txt");
			String parent2 = filedates.getAbsoluteFile().getParent() + "\\dates.txt";
			File datesFile = new File (parent2);

			FileReader fr = new FileReader (datesFile);
			Scanner scanDates = new Scanner (fr);

			int dataP = 0;
			while (!(date.equals(changeDateFormat(scanDates.nextLine()))))
				dataP++;
			return (dataP + 1);
		}
}
