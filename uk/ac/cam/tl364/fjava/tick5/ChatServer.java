package uk.ac.cam.tl364.fjava.tick5;

import java.io.IOException;
import java.net.ServerSocket;
import java.sql.SQLException;

import uk.ac.cam.cl.fjava.messages.Message;

public class ChatServer {
	
	static ServerSocket ss;
	static MultiQueue<Message> mq;
	
	public static void begin(Database d) {
		ClientHandler ch = null;
		while(true) {
			try {
				new ClientHandler(ss.accept(), mq, d);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int port;
		
		if (args.length != 2) {
			System.err.println("Usage: java ChatServer <port> <database name>");
			return;
		}
		try {
			port = Integer.parseInt(args[0]);
		} catch (NumberFormatException nfe) {
			System.err.println("Usage: java ChatServer <port>");
			return;
		}
		try {
			ss = new ServerSocket(port);
		} catch (IOException ioe) {
			System.err.println("Cannot use port number "+port);
			return;
		}
		
		Database d = null;
		try {
			d = new Database(args[1]);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		mq = new MultiQueue<Message>();
		
		if (d != null) begin(d);
	}

}
