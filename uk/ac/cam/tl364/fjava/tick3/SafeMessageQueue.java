package uk.ac.cam.tl364.fjava.tick3;

public class SafeMessageQueue<T> implements MessageQueue<T> {
 private static class Link<L> {
  L val;
  Link<L> next;
  Link(L val) { this.val = val; this.next = null; }
 }
 private Link<T> first = null;
 private Link<T> last = null;

 public synchronized void put(T val) {
	 Link<T> link = new Link(val);
	 if (first == null) {
		 first = last = link;
	 } else {
		 last.next = link;
		 last = link;
	 }
	 this.notify();
 }

 public synchronized T take() {
	while(first == null) //use a loop to block thread until data is available
		try {this.wait(100);} catch(InterruptedException ie) {}
  	Link<T> link = first;
  	if (link.next != null) first = link.next;
  	else first = last = null;
  	return link.val;
 }
}