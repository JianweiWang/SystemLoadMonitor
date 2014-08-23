package com.wjw.info;

import java.io.Serializable;

public class NodeLoadInfo implements Serializable{
	private String nodeId;
	private float cpuUsage;
	private String time;
	private int tid;
	public NodeLoadInfo() {
		nodeId = null;
		cpuUsage = -1;
		time = null;
		tid = -1;
		
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getNodeId() {
		return nodeId;
	}
	public void setCpu(float cpuUsage) {
		this.cpuUsage = cpuUsage;
	}
	public float getCpu() {
		return cpuUsage;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getTime() {
		return time;
	}
	public void setTid(int tid) {
		this.tid = tid;
	}
	public int getTid() {
		return tid;
	}
}
