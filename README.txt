To run this program:

1.	Go to Environment Variables (in Control Panel) and make sure Java JDK is added to the Path variable
	*if not, click on Path, then Add, then enter the path for java. it should be simular to this:	
	
					C:\Program Files\Java\jdk-13.0.1\bin

2.	Double-click the Comand Script called "Run Me" in the "target" folder


NOTES:
***If you are running this and keep getting "waiting for API servers..." on the first run, this is due to a request 
limit from the server so, consider getting your own key at https://www.alphavantage.co/support/#api-key and changing 
the key in the Quotes.java file and recompiling using Apache Maven.***


