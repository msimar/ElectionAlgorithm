package com.mps.pearl.util;

/**
 * 
 * This class implements Logger. It provide advance debugging 
 * property to the Application.
 * 
 * @author msingh
 * @version 1.0 20 Jan, 2014
 *
 */

public class Logger {
	
	/**
	 * A debug state
	 */
	public static final boolean DEBUG = true;
	
	/**
	 * Allocates a new Logger object.
	 */
	public Logger() {
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * This method is designed to debug verbose messages
	 * 
	 * @param tag the String name of the debugging class
	 * @param message the String debugging message 
	 */
	public static void d(String tag, String message){
		System.out.println("[" + tag + "] : " + message);
	}
	
	/**
	 * This method is designed to debug Error messages
	 * 
	 * @param tag the String name of the debugging class
	 * @param message the String debugging message 
	 */
	public static void e(String tag, String message){
		System.err.println("[" + tag + "] : " + message);
	}
}
