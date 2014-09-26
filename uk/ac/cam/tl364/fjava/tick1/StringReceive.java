package uk.ac.cam.tl364.fjava.tick1;

import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.UnknownHostException;

public class StringReceive {

	/**
	 * @param args
	 * @throws IOException 
	 * 
	 */
	public static void main(String[] args) throws IOException {
		if (args.length != 2) {
			System.err.println("This application requires two arguments: <machine> <port>");
			return;
		}
		
		String host = args[0];
		int port = 0;
		
		try {
			port = Integer.parseInt(args[1]);
		} catch (NumberFormatException nfe) {
			System.err.println("This application requires two arguments: <machine> <port>");
			return;
		}
		
		Socket socket;
		
		try {
			socket = new Socket(host, port);
		} catch (UnknownHostException uhe) {
			System.err.println("Cannot connect to "+host+" on port "+port);
			return;
		} catch (NoRouteToHostException nrthe) {
			System.err.println("Cannot connect to "+host+" on port "+port);
			return;
		} catch (ConnectException ce) {
			System.err.println("Cannot connect to "+host+" on port "+port);
			return;
		}
		
		InputStream inputStream = socket.getInputStream();
		
		byte[] buffer = new byte[2048];
		
		while(true) {
			if (inputStream.read(buffer) > 0) System.out.println(new String(buffer));
			buffer = new byte[2048];
		}
	}

}
