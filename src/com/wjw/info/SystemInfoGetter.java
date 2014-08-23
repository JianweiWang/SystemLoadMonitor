package com.wjw.info;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Random;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

public class SystemInfoGetter implements Runnable {
	private Logger log = Logger.getLogger(SystemInfoGetter.class);

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while (true) {
			try {
				exec();
				Thread.sleep(5000);
			} catch (Exception e) {
				log.error("Performance Monitoring error: "+e.getMessage());
				e.printStackTrace();
			}
		}
		
	}

	public void exec() throws Exception {
		// TODO Auto-generated method stub
		InetAddress inet = InetAddress.getLocalHost();
		System.out.println("Performance Monitoring ip:"+inet.toString());
		
		String ip = inet.toString().substring(inet.toString().indexOf("/")+1);
		log.info("Performance Monitoring ip: "+ip);
		
		/*int[] memInfo = SystemInfoGetter.getMemInfo();
		System.out.println("MemTotal: "+ memInfo[0]);
		System.out.println("MemFree: "+ memInfo[1]);*/
		
		//Snmp util = new Snmp();
		
	}

	private  int[] getMemInfo() throws IOException,InterruptedException {
		// TODO Auto-generated method stub
		File file = new File("/proc/meminfo");
		BufferedReader br = new BufferedReader(new InputStreamReader( new FileInputStream(file)));
		int[] result = new int[4];
		String str = null;
		StringTokenizer token = null;
		while((str = br.readLine()) != null) {
			token = new StringTokenizer(str);
			if(!token.hasMoreTokens())
				continue;
			
			str = token.nextToken();
			if(!token.hasMoreTokens())
				continue;
			
			if(str.equalsIgnoreCase("MemTotal:"))
				result[0] = Integer.parseInt(token.nextToken());
			else if (str.equalsIgnoreCase("MemFree:"))
				result[1] = Integer.parseInt(token.nextToken());
			else if (str.equalsIgnoreCase("SwapTotal:"))
				result[2] = Integer.parseInt(token.nextToken());
			else if (str.equalsIgnoreCase("SwapFree:"))
				result[3] = Integer.parseInt(token.nextToken());
	
		}
		return result;
	}
	
	public  float getCpuInfo() throws IOException, InterruptedException {
		
		File file = new File("/proc/stat");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		StringTokenizer token = new StringTokenizer(br.readLine());
		token.nextToken();
		int user1 = Integer.parseInt(token.nextToken());
		int nice1 = Integer.parseInt(token.nextToken());
		int sys1 = Integer.parseInt(token.nextToken());
		int idle1 = Integer.parseInt(token.nextToken());
		
		Thread.sleep(5000);
		
		br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		token = new StringTokenizer(br.readLine());
		token.nextToken();
		int user2 = Integer.parseInt(token.nextToken());
		int nice2 = Integer.parseInt(token.nextToken());
		int sys2 = Integer.parseInt(token.nextToken());
		int idle2 = Integer.parseInt(token.nextToken());
		
		return (float) ((user2 + sys2 + nice2) - (user1 + sys1 + nice1)) / (float) ((user2 + nice2 + sys2 + idle2) - (user1 + nice1 + sys1 + idle1));
		
		
	}
	public  void connectServer(Object obj , Socket s)
	{
		try {
			
			ObjectOutputStream os = new ObjectOutputStream(s.getOutputStream());
			os.writeObject(obj);
			os.flush();
			//s.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public  NodeLoadInfo setMessage(float cpu, String ip) {
		NodeLoadInfo nli = new NodeLoadInfo();
		nli.setCpu(cpu);
		nli.setNodeId(ip);
		nli.setTime(new Date().toString());
		//nli.setTid(tid);
		return nli;
	}
	public NodeLoadInfo setMessage() {
		float cpu;
		int ip;
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());
		ip = rand.nextInt(10);
		cpu = rand.nextFloat();
		NodeLoadInfo nli = new NodeLoadInfo();
		nli.setCpu(cpu);
		nli.setNodeId(Integer.toString(ip));
		nli.setTime(new Date().toString());
		return nli;
		
	}
	public static void main(String[] args) throws Exception {
		final InetAddress inet = InetAddress.getLocalHost();
		final SystemInfoGetter sig = new SystemInfoGetter();
		int[] memInfo = sig.getMemInfo();
		Print.println(inet.toString());
		Socket s = new Socket("127.0.0.1",8888);
		RequestMessage rm = new RequestMessage();
		rm.setMessageType(1);
		sig.connectServer(rm, s);
		while(true) {
			Thread.sleep(2000);
			
			/*float cpu;
			try {
				cpu = sig.getCpuInfo();
				NodeLoadInfo nli = sig.setMessage(cpu, inet.toString());
				sig.connectServer(nli, s);
				Print.println("sending message...");
				
			} catch (IOException | InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			NodeLoadInfo nli = sig.setMessage();
			sig.connectServer(nli, s);
			Print.println("sending message...");
			
		}
		
		/*System.out.println("MemTotal: " + memInfo[0]);
		System.out.println("MemFree: " + memInfo[1]);
		System.out.println("SwapTotal: " + memInfo[2]);
		System.out.println("SwapFree: " + memInfo[3]);
		float cpu = SystemInfoGetter.getCpuInfo();
		System.out.println("CPU usage: " + cpu );*/
		// int count = 0;
		/*while(count < 10) {
			final int k = count;
			Thread.sleep(5000);
			new Thread(new Runnable(){

				@Override
				public void run() {
					// TODO Auto-generated method stub
					float cpu;
					try {
						cpu = sig.getCpuInfo();
						NodeLoadInfo nli = sig.setMessage(cpu, inet.toString(),k);
						sig.connectServer(nli);
						
					} catch (IOException | InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
				
			}).start();
			count++;
		}*/
		
		
		
	}
}