package uk.ac.cam.tl364.fjava.tick0;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;

public class Sort {
	
	public static void sort(String f1, String f2) throws FileNotFoundException, IOException {
		RandomAccessFile a = new RandomAccessFile(f1, "r");
		RandomAccessFile a2 = new RandomAccessFile(f1, "rw");
		RandomAccessFile b = new RandomAccessFile(f2, "rw");
		DataOutputStream dosb;
		DataOutputStream dosa;
		DataInputStream dis;
		long length = a.length()/4;
		int bin;
		long off;
		long mem = Runtime.getRuntime().totalMemory();
		//int memdiv = (int)(mem/16);
		//int memdiv = 655360;
		int memdiv = 8192;
		
		for (int i = 0; i<31; i++) {
			bin = 1<<i;
			dosb = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(b.getFD()), memdiv));
			dosa = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(a2.getFD()), memdiv));
			dis = new DataInputStream(new BufferedInputStream(new FileInputStream(a.getFD()), memdiv));
			for (int j = 0; j<length; j++) {
				int temp = dis.readInt();
				if ((temp&bin) == 0) {
					dosa.writeInt(temp);
				} else {
					dosb.writeInt(temp);
				}
			}
			dosb.flush();
			
			off = b.getFilePointer()/4;
			b.seek(0);
			dis = new DataInputStream(new BufferedInputStream(new FileInputStream(b.getFD()), memdiv));
			for (long j = 0; j<off; j++) {
				//System.out.println(dis.readInt());
				dosa.writeInt(dis.readInt());
			}
			dosa.flush();
			a.seek(0);
			a2.seek(0);
			b.seek(0);
		}
		dosb = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(b.getFD()), memdiv));
		dosa = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(a2.getFD()), memdiv));
		dis = new DataInputStream(new BufferedInputStream(new FileInputStream(a.getFD()), memdiv));
		for (int j = 0; j<length; j++) {
			int temp = dis.readInt();
			if (temp>0) {
				dosb.writeInt(temp);
			} else {
				dosa.writeInt(temp);
			}
		}
		dosb.flush();
		
		off = b.getFilePointer()/4;
		b.seek(0);
		dis = new DataInputStream(new BufferedInputStream(new FileInputStream(b.getFD()), memdiv));
		for (long j = 0; j<off; j++) {
			dosa.writeInt(dis.readInt());
		}
		dosa.flush();
		a.seek(0);
		a2.seek(0);
		b.seek(0);
	}
	
	public static void sort2(String x, String y) throws FileNotFoundException, IOException {
		RandomAccessFile a = new RandomAccessFile("C:/Users/Tom/Documents/Code/Java/Ticks/1B/tick 0/test-suite/test11a.dat","rw");
		RandomAccessFile b = new RandomAccessFile("C:/Users/Tom/Documents/Code/Java/Ticks/1B/tick 0/test-suite/test11b.dat","rw");
		DataOutputStream dos;
		DataInputStream dis;
		long length = a.length()/4;
		int bin;
		for (int i = 0; i<31; i++) {
			bin = 1<<i;
			dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(b.getFD())));
			dis = new DataInputStream(new BufferedInputStream(new FileInputStream(a.getFD())));
			for (int j = 0; j<length; j++) {
				int temp = dis.readInt();
				if ((temp&bin) == 0) {
					dos.writeInt(temp);
				}
			}
			dos.flush();
			a.seek(0);
			dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(b.getFD())));
			dis = new DataInputStream(new BufferedInputStream(new FileInputStream(a.getFD())));
			for (long j = 0; j<length; j++) {
				int temp = dis.readInt();
				if ((temp&bin) != 0) {
					dos.writeInt(temp);
				}
			}
			dos.flush();
			a.seek(0);
			b.seek(0);
			dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(a.getFD())));
			dis = new DataInputStream(new BufferedInputStream(new FileInputStream(b.getFD())));
			for (long j = 0; j<length; j++) {
				int temp = dis.readInt();
				dos.writeInt(temp);
			}
			dos.flush();
			a.seek(0);
			b.seek(0);
		}
		dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(b.getFD())));
		dis = new DataInputStream(new BufferedInputStream(new FileInputStream(a.getFD())));
		for (int j = 0; j<length; j++) {
			int temp = dis.readInt();
			if (temp<0) {
				dos.writeInt(temp);
			}
		}
		dos.flush();
		a.seek(0);
		dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(b.getFD())));
		dis = new DataInputStream(new BufferedInputStream(new FileInputStream(a.getFD())));
		for (long j = 0; j<length; j++) {
			int temp = dis.readInt();
			if (temp>0) {
				dos.writeInt(temp);
			}
		}
		dos.flush();
		a.seek(0);
		b.seek(0);
		dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(a.getFD())));
		dis = new DataInputStream(new BufferedInputStream(new FileInputStream(b.getFD())));
		for (long j = 0; j<length; j++) {
			dos.writeInt(dis.readInt());
		}
		dos.flush();
		
	}
	
	public static void sort3(int x) throws FileNotFoundException, IOException {
		RandomAccessFile a = new RandomAccessFile("C:/Users/Tom/Documents/Code/Java/Ticks/1B/tick 0/test-suite/test"+x+"a.dat","rw");
		RandomAccessFile a2 = new RandomAccessFile("C:/Users/Tom/Documents/Code/Java/Ticks/1B/tick 0/test-suite/test"+x+"a.dat","rw");
		RandomAccessFile b = new RandomAccessFile("C:/Users/Tom/Documents/Code/Java/Ticks/1B/tick 0/test-suite/test"+x+"b.dat","rw");
		RandomAccessFile b2 = new RandomAccessFile("C:/Users/Tom/Documents/Code/Java/Ticks/1B/tick 0/test-suite/test"+x+"b.dat","rw");
		DataOutputStream dos1;
		DataOutputStream dos2;
		DataInputStream dis;
		
		long mem = Runtime.getRuntime().totalMemory();
		int memdiv = (int)(mem/16);
		//int memdiv = 655360;
		//int memdiv = 8192;
		
		long off = 0;
		int bin;
		long length = a.length()/4;
		
		dis = new DataInputStream(new BufferedInputStream(new FileInputStream(a.getFD()),memdiv));
		
		for (int i = 0; i<length; i++) {
			int temp = dis.readInt();
			if ((temp&1) == 0) {
				off++;
			}
		}
		
		a.seek(0);
		RandomAccessFile w1;
		RandomAccessFile w2;
		
		for (int i = 0; i<31; i++) {
			bin = 1<<i;
			if (i%2 == 0) {
				b2.seek(off*4);
				off = 0;
				dos1 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(b.getFD()), memdiv));
				dos2 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(b2.getFD()), memdiv));
				dis = new DataInputStream(new BufferedInputStream(new FileInputStream(a.getFD()), memdiv));
			} else {
				a2.seek(off*4);
				off = 0;
				dos1 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(a.getFD()), memdiv));
				dos2 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(a2.getFD()), memdiv));
				dis = new DataInputStream(new BufferedInputStream(new FileInputStream(b.getFD()), memdiv));
			}
			
			for (int j = 0; j<length; j++) {
				int temp = dis.readInt();
				//System.out.println(new StringBuilder(Integer.toBinaryString(temp)).reverse().toString());
				if ((temp&bin) == 0) {
					dos1.writeInt(temp);
				} else {
					dos2.writeInt(temp);
				}
				if ((temp&(bin<<1)) == 0) {
					off++;
				}
			}
			
			//System.out.println();
			dos1.flush();
			dos2.flush();
			a.seek(0);
			b.seek(0);
		}
		
		a2.seek(b2.length()-(off*4));
		dos1 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(a.getFD()), memdiv));
		dos2 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(a2.getFD()), memdiv));
		dis = new DataInputStream(new BufferedInputStream(new FileInputStream(b.getFD()), memdiv));
		
		for (int k = 0; k<length; k++) {
			int temp = dis.readInt();
			//System.out.println(new StringBuilder(Integer.toBinaryString(temp)).reverse().toString());
			if (temp<0) {
				dos1.writeInt(temp);
			} else {
				dos2.writeInt(temp);
			}
		}
		
		//System.out.println();
		dos1.flush();
		dos2.flush();
	}
	
	public static void sort4(String f1, String f2) throws FileNotFoundException, IOException {
		long mem = Runtime.getRuntime().freeMemory();
		//int memdiv = (int)(mem/16);
		//int memdiv = 655360;
		int memdiv = 8192;
		
		RandomAccessFile a = new RandomAccessFile(f1,"rw");
		
		long len = a.length();
		
		if (len<(mem*0.75)) {
			int[] internal = new int[(int) (len/4)];
			DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(a.getFD()),memdiv));
			for (int i = 0; i<internal.length; i++) {
				internal[i] = dis.readInt();
			}
			a.seek(0);
			DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(a.getFD()), memdiv));
			Arrays.sort(internal);
			for (int i: internal) {
				dos.writeInt(i);
			}
			dos.flush();
			return;
		}
		
		RandomAccessFile a2 = new RandomAccessFile(f1,"rw");
		RandomAccessFile b = new RandomAccessFile(f2,"rw");
		RandomAccessFile b2 = new RandomAccessFile(f2,"rw");
		DataOutputStream dos1;
		DataOutputStream dos2;
		DataInputStream dis;
		
		long off = 0;
		int bin;
		long length = len/4;
		
		dis = new DataInputStream(new BufferedInputStream(new FileInputStream(a.getFD()),memdiv));
		
		for (int i = 0; i<length; i++) {
			int temp = dis.readInt();
			if ((temp&1) == 0) {
				off++;
			}
		}
		
		a.seek(0);
		
		for (int i = 0; i<31; i++) {
			bin = 1<<i;
			if (i%2 == 0) {
				b2.seek(off*4);
				off = 0;
				dos1 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(b.getFD()), memdiv));
				dos2 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(b2.getFD()), memdiv));
				dis = new DataInputStream(new BufferedInputStream(new FileInputStream(a.getFD()), memdiv));
			} else {
				a2.seek(off*4);
				off = 0;
				dos1 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(a.getFD()), memdiv));
				dos2 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(a2.getFD()), memdiv));
				dis = new DataInputStream(new BufferedInputStream(new FileInputStream(b.getFD()), memdiv));
			}
			
			for (int j = 0; j<length; j++) {
				int temp = dis.readInt();
				//System.out.println(new StringBuilder(Integer.toBinaryString(temp)).reverse().toString());
				if ((temp&bin) == 0) {
					dos1.writeInt(temp);
				} else {
					dos2.writeInt(temp);
				}
				if ((temp&(bin<<1)) == 0) {
					off++;
				}
			}
			
			//System.out.println();
			dos1.flush();
			dos2.flush();
			a.seek(0);
			b.seek(0);
		}
		
		a2.seek(b2.length()-(off*4));
		dos1 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(a.getFD()), memdiv));
		dos2 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(a2.getFD()), memdiv));
		dis = new DataInputStream(new BufferedInputStream(new FileInputStream(b.getFD()), memdiv));
		
		for (int k = 0; k<length; k++) {
			int temp = dis.readInt();
			//System.out.println(new StringBuilder(Integer.toBinaryString(temp)).reverse().toString());
			if (temp<0) {
				dos1.writeInt(temp);
			} else {
				dos2.writeInt(temp);
			}
		}
		
		//System.out.println();
		dos1.flush();
		dos2.flush();
	}
	
	public static void sort5(String f1, String f2) throws FileNotFoundException, IOException {
		RandomAccessFile a = new RandomAccessFile(f1,"rw");
		RandomAccessFile b = new RandomAccessFile(f2,"rw");
		
		long length = a.length()/4;
		int num = Math.min(2, (int)length);
		//int memdiv = (int)(mem/16);
		//int memdiv = 655360;
		int memdiv = 8192;
		int index = 0;
		int passes = (int)Math.ceil(length/(float)num);
		
		int[] intern = new int[num];
		
		DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(b.getFD()), memdiv));
		DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(a.getFD()), memdiv));
		
		for (int i = 0; i<length; i++) {
			if (index < intern.length) {
				int temp = dis.readInt();
				System.out.println(temp);
				intern[index] = temp;
				index++;
			} else {
				Arrays.sort(intern);
				for (int j = 0; j<index; j++) {
					System.out.println(intern[j]);
					dos.writeInt(intern[j]);
				}
				index = 0;
			}
		}
		
		System.out.println("a");
		
		Arrays.sort(intern);
		for (int j = 0; j<index; j++) {
			System.out.println(intern[j]);
			dos.writeInt(intern[j]);
		}
		index = 0;
		
		dos.flush();
		
		System.out.println("b");
		
		int peek = (int) Math.ceil(num/(float)passes);
		
		a.seek(0);
		index = 0;
		
		dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(a.getFD()), memdiv));
		dis = new DataInputStream(new BufferedInputStream(new FileInputStream(b.getFD()), memdiv));
		
		for (int i = 0; i< passes; i++) {
			b.seek(i*peek);
			for (int j = 0; j<num; j++) {
				if (index < intern.length) {
					int temp = dis.readInt();
					System.out.println(temp);
					intern[index] = temp;
					index++;
					if (index == peek) {
						dis.skipBytes((num-peek)*4);
					}
				} else {
					Arrays.sort(intern);
					for (int k = 0; k<index; k++) {
						dos.writeInt(intern[k]);
						System.out.println(intern[k]);
					}
					index = 0;
				}
			}
		}
		
		System.out.println("c");
		
		Arrays.sort(intern);
		for (int k = 0; k<index; k++) {
			dos.writeInt(intern[k]);
			System.out.println(intern[k]);
		}
		index = 0;
		passes++;
		
		dos.flush();
		
	}
}
