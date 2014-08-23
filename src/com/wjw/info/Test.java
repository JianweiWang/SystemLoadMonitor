package com.wjw.info;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Test {
	public static void main(String[] args) {
//		HashMap<Integer,String> hm = new HashMap<Integer,String>();
//		for(int i = 0; i < 5; i++) {
//			hm.put(i, Integer.toString(i));
//		}
//		Iterator iter = hm.entrySet().iterator();
//		while(iter.hasNext()) {
//			Map.Entry entry = (Map.Entry) iter.next();
//			Object key = entry.getKey();
//			Object val = entry.getValue();
//			System.out.println(val.toString());
//		}
		//String[] s = (String[]) str.toArray();
//		NodeLoadInfo[] nli = new NodeLoadInfo[5];
//		
//		for(int i = 0 ; i < 5 ; i++ ) {
//			nli[i] = new NodeLoadInfo();
//			System.out.println(nli[i].getNodeId());
//			System.out.println(nli[i].getCpu());
//		}
		Integer[] i = new Integer[3];
		for(int j = 0; j < 3; j++)
			i[j] = j;
		f(i);
		System.out.println(i[2]);
	
		
	}
	private static void f(Integer[] i) {
		i[2] = 5;
	}

}
