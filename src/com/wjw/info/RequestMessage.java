package com.wjw.info;

import java.io.Serializable;

public class RequestMessage implements Serializable {
	private int messageType ;
	public void setMessageType(int type) {
		this.messageType = type;
	}
	public int getMessageType() {
		return messageType;
	}
}
