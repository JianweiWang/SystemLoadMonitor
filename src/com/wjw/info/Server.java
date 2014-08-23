package com.wjw.info;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Server {
	public static HashMap<String,MessageQueue> hm = new HashMap<String,MessageQueue>();
	
	//private static MessageQueue[] mq = new MessageQueue[10];
	//private static List<MessageQueue> mq = new ArrayList<MessageQueue>();
	public static Object synObject = new Object();
	/*public void init() {
		int i = 0;
		for(; i < 10 ; i++ ) {
			mq.add(new MessageQueue());
		}
	}*/
	public static void main(String[] args) {
		Server server = new Server();
		//server.init();
		try {
			
			final ServerSocket ss = new ServerSocket(9999);
			final ServerSocket ss1 = new ServerSocket(8888);
			Print.println("Server is starting...");
//			new Thread(new Runnable() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					while(true) {
//						Socket s = null;
//						try {
//							s = ss.accept();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						invokeRec(s);
//					}
//					
//				}
//				
//			}).start();
//			
//			new Thread(new Runnable() {
//
//				@Override
//				public void run() {
//					// TODO Auto-generated method stub
//					while(true) {
//						Socket s1 = null;
//						try {
//							s1 = ss1.accept();
//						} catch (IOException e) {
//							// TODO Auto-generated catch block
//							e.printStackTrace();
//						}
//						invokeRec(s1);
//					}
//					
//				}
//				
//			}).start();
			while(true) {
//				Socket s = ss.accept();
//				invokeRec(s);
				Socket s1 = ss1.accept();
				//invokeSend(s1);
				identify_operation(s1);
				
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void identify_operation(Socket s) {
		try {
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
			RequestMessage rm = (RequestMessage) ois.readObject();
			if(rm.getMessageType() == MessageType.SEND_MESSAGE)
				invokeRec(s);
			else if(rm.getMessageType() == MessageType.GET_MESSAGE)
				invokeSend(s);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	private static void invokeSend(Socket s1) {
		// TODO Auto-generated method stub
		final Socket s = s1;
		Thread sendThread = new Thread(new Runnable(){

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Iterator iter = hm.entrySet().iterator();
					ObjectOutputStream  oos = new ObjectOutputStream(s.getOutputStream());
					SystemInfo si = new SystemInfo();
					int i = 0;
					synchronized(synObject) {
						while(iter.hasNext()) {
							Map.Entry entry = (Map.Entry) iter.next();
							MessageQueue mq = (MessageQueue)entry.getValue();
							NodeLoadInfo nli = mq.getMessage();
							si.setGlobalLoadInfo((String)entry.getKey(),nli);
							
						}
					}
					System.out.println("sending message ...");
					oos.writeObject(si);
					oos.flush();
					//oos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
		});
		sendThread.start();
	}

	private  static void invokeRec(final Socket s) {
		// TODO Auto-generated method stub
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true) {
					try {
						ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
						NodeLoadInfo nli = (NodeLoadInfo)ois.readObject();
						
						Print.println("receiving message from" + nli.getNodeId());
						//hm.put(nli.getNodeId(), nli);
						if( !hm.containsKey(nli.getNodeId()) ) {
							hm.put(nli.getNodeId(), new MessageQueue());
						}
						synchronized (synObject) {
							//mq.get(nli.getTid()).addMessage(nli);
							hm.get(nli.getNodeId()).addMessage(nli);
						}
						Print.println(Float.toString(nli.getCpu())); 
						//s.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				
				
			}
			
		}).start();
	}
}
