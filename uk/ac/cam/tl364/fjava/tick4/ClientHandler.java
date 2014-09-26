package uk.ac.cam.tl364.fjava.tick4;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

import uk.ac.cam.cl.fjava.messages.ChangeNickMessage;
import uk.ac.cam.cl.fjava.messages.ChatMessage;
import uk.ac.cam.cl.fjava.messages.Message;
import uk.ac.cam.cl.fjava.messages.RelayMessage;
import uk.ac.cam.cl.fjava.messages.StatusMessage;

public class ClientHandler {
	 private Socket socket;
	 private MultiQueue<Message> multiQueue;
	 private String nickname;
	 private MessageQueue<Message> clientMessages;
	 
	 private Random random = new Random();
	 private boolean kill;
	 
	 public ClientHandler(Socket s, MultiQueue<Message> q) {
		  socket = s;
		  multiQueue = q;
		  clientMessages = new SafeMessageQueue<Message>();
		  multiQueue.register(clientMessages);
		  nickname = new String("Anonymous"+random.nextInt(99999));
		  multiQueue.put(new StatusMessage(nickname+" connected from "+s.getInetAddress().getHostName()+"."));
		  begin();
	 }
	 
	 public void begin() {
		final ObjectInputStream in;
		final ObjectOutputStream out;
		try {
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
			Thread input = new Thread() {
				@Override
				public void run() {
					 while(true) {
						 readMessage(in);
						 if(kill) return;
					}
				}
			};
			input.setDaemon(true);
			input.start();
			Thread output = new Thread() {
				@Override
				public void run() {
					 while(true) {
						 writeMessages(out);
						 if(kill) return;
					}
				}
			};
			output.setDaemon(true);
			output.start();
		} catch (IOException e) {
			kill();
		}
		
	 }
	 
	 boolean readMessage(ObjectInputStream ips) {
			Message receive = null;
			String name = nickname;
			try {
				Object fromServ = ips.readObject();
				if (fromServ instanceof Message) receive = (Message) fromServ;
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
			if (receive instanceof ChangeNickMessage) {
				name = ((ChangeNickMessage)receive).name;
				multiQueue.put(new StatusMessage(nickname+" is now known as "+name+"."));
				nickname = name;
			}
			if (receive instanceof ChatMessage) {
				multiQueue.put(new RelayMessage(nickname, (ChatMessage) receive));			
			}
			return true;
	 }
	 
	 boolean writeMessages(ObjectOutputStream ops) {
		 try {
				ops.writeObject(clientMessages.take());
				ops.flush();
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		 return true;	 
	 }
	 
	 void kill() {
		 multiQueue.deregister(clientMessages);
		 multiQueue.put(new StatusMessage(nickname+" has disconnected."));
	 }
}