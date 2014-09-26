package uk.ac.cam.tl364.fjava.tick4;

import java.io.IOException;
import java.net.ServerSocket;

import uk.ac.cam.cl.fjava.messages.Message;

public class ChatServer {
	
	static ServerSocket ss;
	static MultiQueue<Message> mq;
	
	public static void begin() {
		ClientHandler ch = null;
		while(true) {
			try {
				new ClientHandler(ss.accept(), mq);
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
		
		if (args.length != 1) {
			System.err.println("Usage: java ChatServer <port>");
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
		mq = new MultiQueue<Message>();
		begin();
	}

}
