package uk.ac.cam.tl364.fjava.tick2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.text.FieldPosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import uk.ac.cam.cl.fjava.messages.ChangeNickMessage;
import uk.ac.cam.cl.fjava.messages.ChatMessage;
import uk.ac.cam.cl.fjava.messages.DynamicObjectInputStream;
import uk.ac.cam.cl.fjava.messages.Execute;
import uk.ac.cam.cl.fjava.messages.Message;
import uk.ac.cam.cl.fjava.messages.NewMessageType;
import uk.ac.cam.cl.fjava.messages.RelayMessage;
import uk.ac.cam.cl.fjava.messages.StatusMessage;

@FurtherJavaPreamble(
		author = "Tom Lefley",
		date = "18th November 2013",
		crsid = "tl364",
		summary = "A chat client",
		ticker = FurtherJavaPreamble.Ticker.A)
public class ChatClient {
	
	static Socket socket;
	static SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	static Date messageTime;
	
	static void begin() throws IOException {
		
		final InputStream inputStream = socket.getInputStream();
		final OutputStream outputStream = socket.getOutputStream();
		final DynamicObjectInputStream in = new DynamicObjectInputStream(inputStream);
		final ObjectOutputStream out = new ObjectOutputStream(outputStream);
		
		Thread output = new Thread() {
			@Override
			public void run() {
				 while(true) {
					 readMessage(in);
				}
			}
		};
		output.setDaemon(true);
		output.start();

		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
			
		while(true) {
			messageTime = new Date();
			String messageTimeString = new String(sdf.format(messageTime,new StringBuffer(),new FieldPosition(0)));
			try {
				String message = new String(r.readLine());
				if (message.equals("\\quit")) {
					System.out.println(messageTimeString+" [Client] Connection terminated.");
					return;
				}
				if (message.startsWith("\\nick")) {
					writeMessage(new ChangeNickMessage(message.substring(6)), out);
				} else if (message.startsWith("\\")) {
					System.out.println(messageTimeString+" [Client] Unknown command \""+ message.substring(1)+"\".");
				} else {
					writeMessage(new ChatMessage(message), out);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
		
	
	static boolean writeMessage(Message message, ObjectOutputStream out) {
		try {
			out.writeObject(message);
			out.flush();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	static boolean readMessage(DynamicObjectInputStream ips) {
		Message receive = null;
		String date = null;
		String name = "Client";
		String message = null;
		Field[] fields;
		Method[] methods;
		Class<?> someClass;
		try {
			Object fromServ = ips.readObject();
			if (fromServ instanceof Message) receive = (Message) fromServ;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		date = new String(sdf.format(receive.getCreationTime(),new StringBuffer(),new FieldPosition(0)));
		if (receive instanceof NewMessageType) {
			ips.addClass(((NewMessageType) receive).getName(), ((NewMessageType) receive).getClassData());
			message = new String("New class "+((NewMessageType) receive).getName()+" loaded.");
			System.out.println(date+" ["+name+"] "+message);
		} else {
			if (receive instanceof RelayMessage) {
				message = ((RelayMessage)receive).getMessage();
				name = ((RelayMessage)receive).getFrom();
				System.out.println(date+" ["+name+"] "+message);
			} else {
				if (receive instanceof StatusMessage) {
					message = ((StatusMessage)receive).getMessage();
					name = "Server";
					System.out.println(date+" ["+name+"] "+message);
				} else {
					someClass = receive.getClass();
					fields = someClass.getDeclaredFields();
					methods = someClass.getDeclaredMethods();
					message = new String(someClass.getSimpleName()+":");
					try {
						for (int i = 0; i<fields.length; i++) {
							Field f = fields[i];
							f.setAccessible(true);
							message = message.concat(new String(" "+f.getName()+"("+f.get(receive).toString()+")"));
							if (i != fields.length-1) {
								message = message.concat(",");
							}
						} 
						System.out.println(date+" ["+name+"] "+message);
						
						for (Method m: methods) {
							if (m.getParameterTypes().length == 0 && m.isAnnotationPresent(Execute.class)) {
								m.invoke(receive);
							}
						}
					
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						e.printStackTrace();
					}
					
				}
			}
		}
		date = message = null;
		name = "Client";
		
		return true;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
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
		
		try {
			socket = new Socket(host, port);
			String date = new String(sdf.format(new Date(),new StringBuffer(),new FieldPosition(0)));
			System.out.println(date+" [Client] Connected to "+host+" on port "+port+".");
			begin();
		} catch (IOException e) {
			System.err.println("Cannot connect to "+host+" on port "+port);
			return;
		}

	}

}
