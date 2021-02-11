import java.io.File;
import java.util.Scanner;

/*
 * Interface class 
 * serves as a template for the variables needed in both the read and write class
 * file2 is the file with data and scanner is for user input
 */
public class Interface {
	//vars
		protected File file2;
		protected Scanner sc;
	
	//constructor
		public Interface() {
			File file = new File ("data.txt");
			String parent = file.getAbsoluteFile().getParent() + "\\data.txt";
			file2 = new File (parent);
			
			sc = new Scanner (System.in);
		}
}
