package uk.ac.cam.tl364.fjava.tick1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.net.UnknownHostException;

import com.google.code.chatterbotapi.*;


public class StringChat {

	public static void main(String[] args) throws Exception {

		String host = null;
		int port = 0;
		
		if (args.length != 2) {
			System.err.println("This application requires two arguments: <machine> <port>");
			return;
		}
		
		host = args[0];
		
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
		
		final InputStream inputStream = socket.getInputStream();
		final OutputStream outputStream = socket.getOutputStream();
		
		ChatterBotFactory factory = new ChatterBotFactory();

        ChatterBot bot1 = factory.create(ChatterBotType.CLEVERBOT);
        final ChatterBotSession bot1session = bot1.createSession();

		
		Thread output = new Thread() {
		 @Override
		 public void run() {
			byte[] inputBuffer = new byte[2048];
			
			try {
				outputStream.write("CHATTERBOT ONLINE, TYPE 'Hey, Listen!' TO CHAT".getBytes());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
	        String talkingIP = "/128.232.108.45";
				
			while(true) {
				try {
					if (inputStream.read(inputBuffer) > 0) {
						String message = new String(inputBuffer);
						System.out.println(message);
						String[] splitMessage = message.split(":");
						if (splitMessage.length <2) continue;
						String IP = splitMessage[0];
						System.out.println(splitMessage[1]);
						if (splitMessage[1].equals("Hey, Listen!")) talkingIP = splitMessage[0];
						if (IP.equals(talkingIP)) {
							try {
								outputStream.write(new String("CHATTERBOT: "+bot1session.think(splitMessage[1])).getBytes());
							} catch (IOException e1) {
								e1.printStackTrace();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				} catch (IOException e) {
					System.err.println(e.getMessage());
					e.printStackTrace();
				}
				inputBuffer = new byte[2048];
			}
		 }
		};
		output.setDaemon(true);
		output.start();
		
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		while(true) {
			outputStream.write(r.readLine().getBytes());
		}
	}
}
