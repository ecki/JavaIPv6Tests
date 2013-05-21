/*
 * IPV6Test.java
 *
 * created at 2011-06-28 by Bernd Eckenfels
 */
package net.eckenfels.ipv6test;


import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.ServerSocket;
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
     * <ul>
     * <li>getaddrinfo HOST</li>
     * <li>localaddr</li>
     * <li>connect HOST PORT</li>
     * <li>listen ADDR PORT</li>
     * </ul>
     * 
     * @param args
     *            1-3 arguments, keywords myaddrinfo, getaddrinfo, or connect
     */
    public static void main(String[] args)
    {
        printProperties();

        if (args.length >= 2 && "getaddrinfo".equalsIgnoreCase(args[0]))
            printInfo(args[1]);
        else if (args.length >= 1 && "localaddr".equalsIgnoreCase(args[0]))
            printInfo(null);
        else if (args.length >= 3 && "connect".equalsIgnoreCase(args[0]))
            connectHost(args[1], args[2]);
        else if (args.length >= 3 && "listen".equalsIgnoreCase(args[0]))
            listen(args[1], args[2]);
        else if (args.length >= 2 && "listen".equalsIgnoreCase(args[0]))
            listenDefault(args[1]);
        else {
            System.out
                    .println("IPV6Test getaddrinfo <host>|listen [<host>] <port>|localaddr|connect <host> <port>");
        }
    }

    /**
     * Open TCP Listening socket and wait for one connect.
     * 
     * @param host
     *            the Address to bind to
     * @param port
     *            the port to bind to
     */
    static void listen(String host, String port)
    {
        System.out.printf("Looking up with getAllByName(%s,%s)...%n", host, port);

        long start = System.nanoTime();
        InetAddress[] addresses = null;
        try {
            addresses = InetAddress.getAllByName(host);
        } catch (UnknownHostException ex) {
            System.out.printf(
                    " Exception in InetAddress.getAllByName(%s): %s%n", host,
                    ex.toString());
            ex.printStackTrace(System.out);
            return;
        }

        System.out.printf("  ... returned in %,dns%n",
                (System.nanoTime() - start));

        ServerSocket socket = null;
        int portnum = Integer.parseInt(port);
        start = System.nanoTime();
        System.out.println("Binding to first address... "
                + addresses[0].toString());
        try {
            socket = new ServerSocket(portnum, 1, addresses[0]);
        } catch (IOException ex) {
            System.out.printf("Exception in ServerSocket(): %s%n",
                    ex.toString());
            ex.printStackTrace(System.out);
            return;
        }
        System.out.printf("  ... returned in %,dns%n",
                (System.nanoTime() - start));

        System.out.printf("Listening on %s%n", socket.getLocalSocketAddress()
                .toString());

        Socket client = null;
        try {
            client = socket.accept();
            System.out.printf("Accepted %s -> %s%n", client
                    .getRemoteSocketAddress().toString(), client
                    .getLocalSocketAddress().toString());
        } catch (IOException ex) {
            System.out.printf("Socket.accept Exception: %s%n", ex.toString());
        } finally {
            try {
                client.close();
            } catch (Exception ignored) {
            }
            try {
                socket.close();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Open TCP Listening socket and wait for one connect.
     * 
     * @param port
     *            the port to bind to
     */
    static void listenDefault(String port)
    {
        System.out.printf("Binding to default address (%s)...%n", port);

        int portnum = Integer.parseInt(port);

        long start = System.nanoTime();
        ServerSocket socket = null;
        try {
            socket = new ServerSocket(portnum);
        } catch (IOException ex) {
            System.out.printf("Exception in ServerSocket(): %s%n",
                    ex.toString());
            return;
        }
        System.out.printf("  ... returned in %,dns%n",
                (System.nanoTime() - start));

        System.out.printf("Listening on %s%n", socket.getLocalSocketAddress()
                .toString());

        Socket client = null;
        try {
            client = socket.accept();
            System.out.printf("Accepted %s -> %s%n", client
                    .getRemoteSocketAddress().toString(), client
                    .getLocalSocketAddress().toString());
        } catch (IOException ex) {
            System.out.printf("Socket.accept Exception: %s%n", ex.toString());
        } finally {
            try {
                client.close();
            } catch (Exception ignored) {
            }
            try {
                socket.close();
            } catch (Exception ignored) {
            }
        }
    }


    /**
     * TCP connect to a given endpoint.
     * 
     * @param host
     *            hostname
     * @param port
     *            must be valid integer for desired port
     */
    static void connectHost(String host, String port)
    {
        System.out.printf("Looking up with getAllByName(%s, %s)...%n", host, port);

        InetAddress[] addresses = null;
        long start = System.nanoTime();
        try {
            addresses = InetAddress.getAllByName(host);
        } catch (UnknownHostException ex) {
            System.out.println(" Exception in InetAddress.getAllByName(" + host
                    + "): " + ex);
            ex.printStackTrace(System.out);
            return;
        }
        System.out.printf("  ... returned in %,dns%n",
                (System.nanoTime() - start));

        Socket socket = null;
        int portnum = Integer.parseInt(port);
        IOException firstException = null;

        System.out.println("Trying to connect to one of the addressess...");

        start = System.nanoTime();
        for (InetAddress addr : addresses) {
            try {
                socket = new Socket(Proxy.NO_PROXY);
                SocketAddress endpoint = new InetSocketAddress(addr, portnum);
                socket.connect(endpoint, 10 * 1000);
                break;
            } catch (IOException ex) {
                try {
                    socket.close();
                } catch (Exception ignored) {
                }
                socket = null;
                if (firstException == null)
                    firstException = ex;
            }
        }
        System.out.printf("  ... returned in %,dns%n",
                (System.nanoTime() - start));

        if (socket == null) {
            StringBuilder msg = new StringBuilder("Unable to connect to host=");
            msg.append(host);
            msg.append(" port=");
            msg.append(String.valueOf(portnum));
            msg.append(" [");

            for (int i = 0; i < addresses.length; i++) {
                if (i != 0)
                    msg.append(',');
                msg.append(addresses[i].getHostAddress());
            }
            msg.append("]: ");
            msg.append(firstException.getMessage());
            SocketException se = new SocketException(msg.toString());
            se.initCause(firstException);
            se.fillInStackTrace();
            // throw se; // we do not throw it, but print it in this demo

            System.out.println("Exception: " + se.toString());
            se.printStackTrace(System.out);
            return;
        }

        System.out.println("  .. connected to " + socket);
        try {
            socket.close();
        } catch (Exception ignored) {
        }
    }

    /**
     * Print all addresses returned by
     * <code>InetAddress.getAllByName(host)</code>
     * 
     * @param host
     *            Hostname, might be null
     * @see InetAddress#getAllByName(String)
     */
    static void printInfo(String host)
    {
        System.out.printf("Looking up with getAllByName(%s)...%n", host);

        InetAddress[] addresses = null;
        long start = System.nanoTime();
        try {
            addresses = InetAddress.getAllByName(host);
        } catch (UnknownHostException ex) {
            System.out.println(" Exception in InetAddress.getAllByName(" + host
                    + "): " + ex);
            ex.printStackTrace(System.out);
            return;
        }

        System.out.printf("  ... returned in %,dns%n",
                (System.nanoTime() - start));

        for (InetAddress a : addresses) {
            System.out.println("    " + a);
        }
    }

    /**
     * Write some system properties to stdout.
     */
    static void printProperties()
    {
        // write out some information about the JVM and OS
        System.out.printf("Java %s (%s %s) os=%s version=%s level=%s bit=%s%n",
                System.getProperty("java.runtime.version", "N/A"),
                System.getProperty("java.vm.name", "N/A"),
                System.getProperty("java.vm.version", "N/A"),
                System.getProperty("os.name", "N/A"),
                System.getProperty("os.version", "N/A"),
                System.getProperty("sun.os.patch.level", "N/A"),
                System.getProperty("sun.arch.data.model", "N/A"));

        // details about networking system properties
        System.out
                .printf("  javax.net.preferIPv6Address=%s%n", System
                        .getProperty("java.net.preferIPv6Addresses",
                                "false (default)"));
        System.out.printf("  javax.net.IPv4Stackonly=%s%n", System.getProperty(
                "java.net.preferIPv4Stack", "false (default)"));
        System.out.printf("  networkaddress.cache.ttl=%s%n",
                System.getProperty("networkaddress.cache.ttl", "-1 (default)"));

        // some calculated values
        System.out.printf("  InetAddr.anyLocalAddress=%s%n",
                new InetSocketAddress(0).getAddress());
    }
}
