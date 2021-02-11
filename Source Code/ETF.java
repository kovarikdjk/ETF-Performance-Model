/*ETF class
 * Contains all needed info for an etf. Contains the name, the date created, and the stocks it contains
 * mutator and access methods for the need vars
 */
import java.util.ArrayList;

public class ETF {
	//vars
		private String name;
		protected String date;
		protected ArrayList<String> tickers;
	
	//constructor
		public ETF() {
			name = "";
			date = "";
			tickers = new ArrayList<String>();
		}
		public ETF(String n, String d, ArrayList<String> tic) {
			name = n;
			date = d;
			tickers = tic;
		}
	
	//accessor-mutator
		public String getName () {
			return name;
		}
		public void setName (String n) {
			name = n;
		}

	
}
