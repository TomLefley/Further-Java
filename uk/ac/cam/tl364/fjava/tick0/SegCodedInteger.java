package uk.ac.cam.tl364.fjava.tick0;

public class SegCodedInteger implements Comparable<SegCodedInteger> {
	
	public int heldInt;
	private int codeInt;
	
		public SegCodedInteger(int c, int h) {
			heldInt = h;
			codeInt = c;
		}
		
		public SegCodedInteger setHeld(int h) {
			heldInt = h;
			return this;
		}

		@Override
		public int compareTo(SegCodedInteger sci) {
			if (sci.heldInt>heldInt) return -1;
			return 1;
		}
	
}
