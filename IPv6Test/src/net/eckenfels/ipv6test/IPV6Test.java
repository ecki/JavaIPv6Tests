/*
 * IPV6Test.java
 *
 * created at 2011-06-28by Eckenfel <YOURMAILADDRESS>
 * 
 * $Id: $
 *
 * Copyright (c) 2011 SEEBURGER AG, Germany. All Rights Reserved.
 */
package net.eckenfels.ipv6test;


public class IPV6Test
{
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		printProperties();

	}

	/** 
	 * Write some system properties to stdout.
	 */
	static void printProperties() 
	{
		// write out some information about the JVM and OS
		System.out.println("Java " + System.getProperty("java.runtime.version", "N/A") + " (" + System.getProperty("java.vendor") + ") " + System.getProperty("sun.arch.data.model", "") + " " + System.getProperty("os.name") + " " + System.getProperty("os.version") + " " + System.getProperty("sun.os.patch.level", "") );

		// details about networking system properties
		System.out.println("  javax.net.preferIPv6Address=" + System.getProperty("jjava.net.preferIPv6Addresses", "false (default)"));
		System.out.println("  javax.net.IPv4Stackonly=" + System.getProperty("java.net.preferIPv4Stack", "false (default)"));

		System.out.println("  networkaddress.cache.ttl=" + System.getProperty("networkaddress.cache.ttl", "-1 (default)"));

		// System.out.println("" + System.getProperties().toString());
	}
}



