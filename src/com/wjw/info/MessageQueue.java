package com.wjw.info;

import java.util.ArrayList;
import java.util.List;


public class MessageQueue {
	private List<NodeLoadInfo> queue = null;
	public MessageQueue() {
		queue = new ArrayList<NodeLoadInfo>();
	}
	
	public void addMessage(NodeLoadInfo nli) {
		if(queue.size() < 15) {
			queue.add(nli);
		}
		else {
			queue.remove(0);
			queue.add(nli);
		}
	}
	public NodeLoadInfo getMessage() {
		int size = queue.size();
		return queue.get(size - 1);
	} 
}
