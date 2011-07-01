/*
 * IPV6Test.java
 *
 * created at 2011-06-28 by Bernd Eckenfels
 * 
 * Copyright (c) 2011 SEEBURGER AG, Germany. All Rights Reserved.
 */
package net.eckenfels.ipv6test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.UnknownHostException;


/**
 * Test class to demonstrate multiple aspects of IPv6 sockets in Java.
 * 
 * @author Bernd Eckenfels 
 */
public class IPV6Test
{
	/**
	 * Main function for test program.
	 * <P>
	 * 
	 * Use one of the following parameters:
	 * <ul><li>getaddrinfo HOST</li><li>myaddrinfo</li><li>connect HOST PORT</li></ul>
	 * 
	 * @param args 1-3 arguments, keywords myaddrinfo, getaddrinfo, or connect
	 */
	public static void main(String[] args)
	{
		printProperties();
		
		if (args.length >= 2 && "getaddrinfo".equalsIgnoreCase(args[0]))
			printInfo(args[1]);
		else if (args.length >= 1 && "myaddrinfo".equalsIgnoreCase(args[0]))
			printInfo(null);
		else if (args.length >=3 && "connect".equalsIgnoreCase(args[0]))
			connectHost(args[1], args[2]);
		else
		{
			System.out.println("IPV6Test gettaddrinfo <host>|myaddinfo|connect <host> <port>");
		}
	}


	/**
	 * TCP connect to a given endpoint.
	 * 
	 * @param host hostname
	 * @param port must be valid integer for desired port
	 */
	static void connectHost(String host, String port)
	{
		InetAddress[] addresses = null;
		long start;
		try
		{
			System.out.println("Looking up with getAllByName(" + host + ")...");
			start = System.nanoTime();
			addresses = InetAddress.getAllByName(host);
			System.out.printf("  ... returned in %,dns%n", (System.nanoTime() - start));
		}
		catch (UnknownHostException ex)
		{
			System.out.println(" Exception in InetAddress.getAllByName(" + host + "): " + ex);
			ex.printStackTrace(System.out);
			return;
		}

		Socket socket = null;
		int portnum = Integer.parseInt(port);
		IOException firstException = null;
		start = System.nanoTime();
		System.out.println("Trying to connect to one of the addressess...");
		for(InetAddress addr : addresses)
		{
			try
			{
				socket = new Socket(Proxy.NO_PROXY);
				SocketAddress endpoint = new InetSocketAddress(addr, portnum);
				socket.connect(endpoint, 10*1000);
				break;
			} catch(IOException ex) 
			{
				socket = null;
				if (firstException == null)
					firstException = ex;
			}
		}
		System.out.printf("  ... returned in %,dns%n", (System.nanoTime() - start));

		if (socket == null)
		{
			StringBuilder msg = new StringBuilder("Unable to connect to host=");
			msg.append(host); msg.append(" port="); 
		    msg.append(String.valueOf(portnum)); msg.append(" [");
			
			for(int i=0;i < addresses.length;i++)
			{
				if (i != 0)
					msg.append(',');
				msg.append(addresses[i].getHostAddress());
			}
			msg.append("]: "); msg.append(firstException.getMessage());
			SocketException se = new SocketException(msg.toString());
			se.initCause(firstException);
			se.fillInStackTrace();
			//throw se; // we do not throw it, but print it in this demo

			System.out.println("Exception: " + se.toString());
			se.printStackTrace(System.out);
		} else {
			System.out.println("  .. connected to " + socket);
			try { socket.close(); } catch (Exception ignored) { }
		}
	}


	/** 
	 * Print all addresses returned by <code>InetAddress.getAllByName(host)</code>
	 * 
	 * @param host Hostname, might be null
	 * @see InetAddress#getAllByName(String)
	 */
	static void printInfo(String host)
	{
		InetAddress[] addresses = null;
		try
		{
			System.out.println("Looking up with getAllByName(" + host + ")...");
			long start = System.nanoTime();
			addresses = InetAddress.getAllByName(host);
			System.out.printf("  ... returned in %,dns%n", (System.nanoTime() - start));
		} catch (UnknownHostException ex) {
			System.out.println(" Exception in InetAddress.getAllByName(" + host + "): " + ex);
			ex.printStackTrace(System.out);
			return;
		}
		
		for(InetAddress a : addresses) {
			System.out.println("    " + a);
		}
	}


	/** 
	 * Write some system properties to stdout.
	 */
	static void printProperties() 
	{
		// write out some information about the JVM and OS
		System.out.printf("Java %s (%s) os=%s version=%s level=%s bit=%s%n", 
				System.getProperty("java.runtime.version", "N/A"), 
				System.getProperty("java.vendor", "N/A"), 
				System.getProperty("os.name", "N/A"), 
				System.getProperty("os.version", "N/A"), 
				System.getProperty("sun.os.patch.level", "N/A"),
				System.getProperty("sun.arch.data.model", "N/A"));

		// details about networking system properties
		System.out.printf("  javax.net.preferIPv6Address=%s%n", System.getProperty("jjava.net.preferIPv6Addresses", "false (default)"));
		System.out.printf("  javax.net.IPv4Stackonly=%s%n", System.getProperty("java.net.preferIPv4Stack", "false (default)"));
		System.out.printf("  networkaddress.cache.ttl=%s%n", System.getProperty("networkaddress.cache.ttl", "-1 (default)"));
	}
}
