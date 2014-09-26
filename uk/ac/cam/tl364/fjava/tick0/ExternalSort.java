package uk.ac.cam.tl364.fjava.tick0;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.TreeMap;

public class ExternalSort {

	public static void sort(String f1, String f2) throws FileNotFoundException, IOException {
		
		RandomAccessFile a = new RandomAccessFile(f1,"rw");
		RandomAccessFile b = new RandomAccessFile(f2,"rw");
		
		if (a.length() == 0) return;
		
		long mem = (long) ((Runtime.getRuntime().freeMemory())*0.65);
		long len = a.length();
		int length = (int)len/4;
		//int memdiv = (int)(mem/16);
		//int memdiv = 655360;
		int memdiv = 8192;
		
		long num = len;
		int segs = 1;
		
		int intNum = (int) (num/4);
		int intMem = (int) (mem/4);
		
		for (int i = 1; i<=length; i++) {
			if (length%i == 0 && length/i<=intMem) {
				segs = i;
				num = len/segs;
				intNum =length/segs;
				break;
			}
		}
		
		//System.out.println(len);
		//System.out.println(num);
		//System.out.println(mem);
		//System.out.println(intNum);
		//System.out.println(intMem);
		//System.out.println(segs);
		
		//System.out.println();
		
		TreeMap<SegCodedInteger, DataInputStream> streamMap = new TreeMap<SegCodedInteger, DataInputStream>();
		HashMap<DataInputStream, Integer> bounds = new HashMap<DataInputStream, Integer>();
		
		int[] intern = new int[intNum];
		
		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(b.getFD()), memdiv));
		DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(a.getFD()), memdiv));
		
		int index = 0;
		
		for (int i = 0; i< segs; i++) {
			for (int j = 0; j< intNum; j++) {
				int temp = dis.readInt();
				//System.out.println(temp);
				intern[j] = temp;
			}
			Arrays.sort(intern);
			for (int j: intern) {
				//System.out.println(index++);
				dos.writeInt(j);
			}
		}
		dos.flush();
		a.seek(0);
		
		dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(a.getFD()), memdiv));
		
		for (int i = 0; i<segs; i++) {
			RandomAccessFile tempFile = new RandomAccessFile(f2,"rw");
			tempFile.seek(num*i);
			DataInputStream tempStream = new DataInputStream(new BufferedInputStream(new FileInputStream(tempFile.getFD()), memdiv));
			streamMap.put(new SegCodedInteger(i,tempStream.readInt()), tempStream);
			bounds.put(tempStream, 1);
			//System.out.println(streamMap.size());
			//System.out.println(bounds.size());
		}
		
		while (streamMap.firstEntry() != null) {
			Entry<SegCodedInteger, DataInputStream> e = streamMap.pollFirstEntry();
			SegCodedInteger SegKey = e.getKey();
			int write = SegKey.heldInt;
			DataInputStream input = (DataInputStream) e.getValue();
			//System.out.println(index++);
			dos.writeInt(write);
			int read = bounds.get(input);
			//System.out.println(read);
			if (read<intNum) {
				bounds.put(input, read+1);
				streamMap.put(SegKey.setHeld(input.readInt()), input);	
				//System.out.println(bounds.size());
			}
		}
		
		for (SegCodedInteger s: streamMap.keySet()) {
			dos.writeInt(s.heldInt);
		}
		
		dos.flush();
	}
	
	private static String byteToHex(byte b) {
		String r = Integer.toHexString(b);
		if (r.length() == 8) {
			return r.substring(6);
		}
		return r;
	}

	public static String checkSum(String f) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			DigestInputStream ds = new DigestInputStream(
					new FileInputStream(f), md);
			byte[] b = new byte[512];
			while (ds.read(b) != -1)
				;

			String computed = "";
			for(byte v : md.digest()) 
				computed += byteToHex(v);

			return computed;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "<error computing checksum>";
	}

	public static void main(String[] args) throws Exception {
		//long timer = System.currentTimeMillis();
		//int x = 17;
		String f1 = args[0]; //"C:/Users/Tom/Documents/Code/Java/Ticks/1B/tick 0/test-suite/test"+x+"a.dat";
		String f2 = args[1]; //"C:/Users/Tom/Documents/Code/Java/Ticks/1B/tick 0/test-suite/test"+x+"b.dat";
		sort(f1, f2);
		//Sort.sort4(f1,f2);
		//System.out.println((System.currentTimeMillis()-timer)/1000.);
		//System.out.println("The checksum is: "+checkSum("C:/Users/Tom/Documents/Code/Java/Ticks/1B/tick 0/test-suite/test"+x+"a.dat"));
		System.out.println("The checksum is: "+checkSum(f1));
	}
}