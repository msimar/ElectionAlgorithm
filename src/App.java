import com.mps.pearl.ConfigFileReader;
import com.mps.pearl.core.NodeManager;
import com.mps.pearl.util.Logger;

/**
 * 
 * This class act as an Entry point for Application. It performs parsing of file and 
 * activate Nodes to perform Bully Election Algorithms.
 * 
 * @author msingh
 * @version 1.0 20 Jan, 2014
 *
 */
public class App {
	
	/**
	 * This method begin execution of Bully Election Algorithm.
	 * 
	 * @param args A String Array of arguments as an input. The first 
	 * argument should be filename with set of Nodes
	 */
	public static void main(String[] args) {
		if(Logger.DEBUG)Logger.d("APP", "main()");
		 
		if( args.length == 0 ){
			System.out.println("ERR! Argument missing : Configuration_file ");
			System.exit(1);
			return;
		}
					
		// Step 1:: parse the configuration file
		// parse configuration file to data structure
		new ConfigFileReader().parseConfigFile(args[0]);
		
		// Step 2:: Run all the Nodes of the System
		NodeManager.getInstance().init();
	}
}
