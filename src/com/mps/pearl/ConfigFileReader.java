package com.mps.pearl;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import com.mps.pearl.core.Node;
import com.mps.pearl.core.Resource;
import com.mps.pearl.util.Logger;

/**
 * This class implements a parser to read text file which should 
 * contain set of nodes in each line. The parser parse each line
 * and transform each token in a line into Node. Token represented as 
 * an Id, IP Address and Port Number. These compiled Nodes are stored 
 * into Resources as a collection.  
 * 
 * @author msingh
 * @version 1.0 20 Jan, 2014
 *
 */

public class ConfigFileReader {
	
	/**
	 * A Logger tag
	 */
	private final String TAG = ConfigFileReader.class.getSimpleName();
	
	/**
	 * An instance of Resource class
	 */
	private Resource mResourceInstance;
	
	/**
	 * Allocates a new ConfigFileReader object.
	 */
	public ConfigFileReader() {
		mResourceInstance = Resource.getInstance();
	}
	
	/**
	 * This method parse the configuration file and transform content of 
	 * configuration file into set of Nodes. These compiled Nodes are stored 
	 * into the the Resource class.	
	 */
	public void parseConfigFile(String fileName){
		if(Logger.DEBUG)Logger.d(TAG, "parseConfigFile()");
		
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		InputStream input = classLoader.getResourceAsStream("./" + fileName);
		//InputStream input = classLoader.getResourceAsStream("./com/mps/pearl/configuration_file.txt");
		
		List<Node> nodeList = mResourceInstance.getNodeList();
		
		try(BufferedReader br = new BufferedReader(new InputStreamReader(input))) {
			
		    for(String line; (line = br.readLine()) != null; ) {
		    	buildNodes(line,nodeList);
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(Logger.DEBUG)Logger.d(TAG, "Total Nodes in System : " + nodeList.size() );
	}
	
	/**
	 * This method build Nodes for converting each lines String into tokens. Each token 
	 * represent Node property which is stored into Resource class
	 * 
	 * @param line The line read from configuration file
	 * @param nodeList The List<Node> data structure as a holder to save Nodes. 
	 */
	private void buildNodes(String line,List<Node> nodeList){
		// split the lines to tokens
		String[] nodeToken = line.split("\\s+");
		// create new nodes, add to list
		mResourceInstance.add(new Node(Integer.parseInt(nodeToken[0]),nodeToken[1],nodeToken[2]));
	}
}
