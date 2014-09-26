package uk.ac.cam.tl364.fjava.tick4;

import java.util.HashSet;
import java.util.Set;

public class MultiQueue<T> {
	 private Set<MessageQueue<T>> outputs = new HashSet<MessageQueue<T>>();
	 
	 public void register(MessageQueue<T> q) { 
	  outputs.add(q);
	 }
	 public void deregister(MessageQueue<T> q) {
	  outputs.remove(q);
	 }
	 public void put(T message) {
	  for (MessageQueue<T> mq: outputs) {
		  mq.put(message);
	  }
	 }
	}
