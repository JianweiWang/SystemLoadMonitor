package com.wjw.info;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.BufferedInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.Map;

public class SystemInfoProcessor {
	private SystemInfo si = null;
	public SystemInfoProcessor() {
		
	}
	public NodeLoadInfo[] fetchSystemInfo(Socket s) {
		NodeLoadInfo[] nliArray = new NodeLoadInfo[10];
		//initialize nliArray
		for(int i = 0; i < 10; i++)
		{
			nliArray[i] = new NodeLoadInfo();
		}
		try {
			
			ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
			RequestMessage rm = new RequestMessage();
			rm.setMessageType(2);
			oos.writeObject(rm);
			oos.flush();
			ObjectInputStream ois = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
			//String str = (String) ois.readObject();
			//System.out.println(str);
			si = (SystemInfo) ois.readObject();
			Iterator iter = si.getGlobalLoadInfo().entrySet().iterator();
			int i = 0;
			while(iter.hasNext()) {
				Map.Entry entry = (Map.Entry) iter.next();
				//nliArray[i].setNodeId((String)entry.getKey());
				//System.out.println((String) entry.getKey());
				NodeLoadInfo nli = (NodeLoadInfo)entry.getValue(); 
				nliArray[i] = nli;
				//System.out.println(Float.toString((float) nli.getCpu()));
				i++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			return nliArray;
		}
		
	}
	public void printInfo(NodeLoadInfo[] nliArray) {
		int i = 0;
		for(; i < 10; i++)
		{
			if(nliArray[i].getNodeId() != null)
			{
				Print.print(nliArray[i].getNodeId() + "    ");
				Print.println(Float.toString(nliArray[i].getCpu()));
			}
			else
				break;
		}
	}
	public void printInfo() {
		
		Iterator iter = si.getGlobalLoadInfo().entrySet().iterator();
		while(iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			System.out.println((String) entry.getKey());
			NodeLoadInfo nli = (NodeLoadInfo)entry.getValue();
			 
			System.out.println(Float.toString((float) nli.getCpu()));
		}
	}
	public static void main(String[] args) {
		SystemInfoProcessor sip = new SystemInfoProcessor();
		Socket s = null;
		while(true) {
			try {
				Thread.sleep(6*1000);
				
				s = new Socket("127.0.0.1",8888);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("connecting to server ...");
			NodeLoadInfo[] nliArray = sip.fetchSystemInfo(s);
			 sort(nliArray);
			try {
				s.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Initial informance:");
			sip.printInfo(nliArray);
			
		}
		
	}
	private static void sort(NodeLoadInfo[] nliArray) {
		// TODO Auto-generated method stub
		int size = nliArray.length;
		for(int i = 0; i < size; i++)
			for(int j = i+1; j < size; j++)
			{
				NodeLoadInfo nli = new NodeLoadInfo();
				if(nliArray[i].getCpu() > nliArray[j].getCpu()) {
					nli = nliArray[i];
					nliArray[i] = nliArray[j];
					nliArray[j] = nli;
				}
			}
		
	}

}
