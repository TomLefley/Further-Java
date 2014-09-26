package uk.ac.cam.tl364.fjava.tick2;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.net.URLConnection;

public class TestMessageReadWrite {
	
	static boolean writeMessage(String message, String filename) {
		TestMessage testMsg = new TestMessage();
		testMsg.setMessage(message);
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			out.writeObject(testMsg);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	static String readMessage(String location) {
		TestMessage outMsg = null;
			try {
				ObjectInputStream in;
				if (location.startsWith("http://")) {
					URL url = new URL(location);
					URLConnection urlCon = url.openConnection();
					in = new ObjectInputStream(urlCon.getInputStream());
				} else {
					FileInputStream fis = new FileInputStream(location);
					in = new ObjectInputStream(fis);
				}
				Object fromLoc = in.readObject();
				in.close();
				if (fromLoc instanceof TestMessage) outMsg = (TestMessage) fromLoc;
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		if (outMsg != null) return outMsg.getMessage();
		return null;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println(readMessage("http://www.cl.cam.ac.uk/teaching/current/FJava/testmessage-tl364.jobj"));

	}

}
