package com.wjw.info;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SystemInfo implements Serializable {
	HashMap<String,NodeLoadInfo> globalLoadInfo = new HashMap<String,NodeLoadInfo>();
	public void setGlobalLoadInfo(String inet,NodeLoadInfo nli) {
		globalLoadInfo.put(inet, nli);
		
	}
	public HashMap<String,NodeLoadInfo> getGlobalLoadInfo() {
		return globalLoadInfo;
	}
}
