package com.wjw.thrift;

/*
import org.apache.thrift7.TException;
import org.apache.thrift7.protocol.TBinaryProtocol;
import org.apache.thrift7.protocol.TProtocol;
import org.apache.thrift7.transport.TFramedTransport;
import org.apache.thrift7.transport.TSocket;
import org.apache.thrift7.transport.TTransport;
import org.apache.thrift7.transport.TTransportException;

import backtype.storm.generated.ClusterSummary;
import backtype.storm.generated.Nimbus.Client.Factory;
import backtype.storm.generated.Nimbus;


public class StormMonitor {
	public static TSocket tsocket = null;
	public static TFramedTransport tTransport = null;
	public static TBinaryProtocol tBinaryProtocol = null;
	public static Nimbus.Client client = null;
	public static final String SERVER_IP = "localhost";
	public static final int SERVER_PORT	= 6627;
	public static final int TIMEOUT = 30000;
	public static ClusterSummary cs = null;
	public void startClient() {
		tsocket = new TSocket(SERVER_IP, SERVER_PORT);
		tTransport = new TFramedTransport(tsocket);
		tBinaryProtocol = new TBinaryProtocol(tTransport);
		client = new Nimbus.Client(tBinaryProtocol);
		try {
			
			tTransport.open();
			
			
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		try {
				cs = client.getClusterInfo();
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(cs);
	}
	public static void main(String[] args) {
		StormMonitor sm = new StormMonitor();
		sm.startClient();
	}

}*/


/*
 * 监控，统计storm集群中的作业运行信息
 * @author : wjw
 * @date : 2014-03-11
 * 
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.log4j.Logger;
import org.apache.thrift7.TException;
import org.apache.thrift7.protocol.TBinaryProtocol;
import org.apache.thrift7.transport.TFramedTransport;
import org.apache.thrift7.transport.TSocket;
import org.apache.thrift7.transport.TTransportException;





import backtype.storm.generated.ClusterSummary;
import backtype.storm.generated.ExecutorInfo;
import backtype.storm.generated.ExecutorStats;
import backtype.storm.generated.ExecutorSummary;
import backtype.storm.generated.Nimbus;
import backtype.storm.generated.NotAliveException;
import backtype.storm.generated.StormTopology;
import backtype.storm.generated.TopologyInfo;
import backtype.storm.generated.TopologySummary;



public class StormMonitor {

	public static Logger LOG = Logger.getLogger(StormMonitor.class);    
	
	public static TSocket tsocket = null;
	public static TFramedTransport tTransport = null;
	public static TBinaryProtocol tBinaryProtocol = null;
	public static Nimbus.Client client = null;
	
	public String host = "127.0.0.1";      //default nimbus host
	public int port = 6627;                    //default nimbus port
	
	public static CopyOnWriteArraySet<String> jobNames = null;    //job names to monit
	
//	public static TimerScheduler ts = null;
	
	
	public StormMonitor(){
		new StormMonitor(host, port);
	}
	
	public StormMonitor(String nimbusHost, int nimbusPort){
		tsocket = new TSocket(nimbusHost, nimbusPort);
		tTransport = new TFramedTransport(tsocket);
		tBinaryProtocol = new TBinaryProtocol(tTransport);
		client = new Nimbus.Client(tBinaryProtocol);
		try {
			tTransport.open();
		} catch (TTransportException e) {
			e.printStackTrace();
		}
		
		jobNames = new CopyOnWriteArraySet<String>();
		
		//ts = new TimerScheduler();
		//if (ts == null)
		//	System.out.println("m.ts is null");
		
	}
	
	/*
	 * get the topology id
	 * 
	 * @param : name topology name
	 * @return : topology id
	 */
	public static String getTopologyId(String name) {
		if (name == null)
			return null;
        try {
        	ClusterSummary summary = client.getClusterInfo();
        	
            for(TopologySummary s : summary.get_topologies()) {
                if(s.get_name().equals(name)) {  
                	
                	String id = s.get_id();
                	LOG.info("Topology " + name + " exists ! " + "id : " + id);
                    return id;
                } 
            }  
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
        LOG.info("Topology " + name + " not exists ! ");
        return null;
    }
	
	public void close(){
		tTransport.close();
	}
	
	public static ArrayList<String> getTopologyIds(String[] names){
		ArrayList<String> rs = new ArrayList<String>();
		for (String name : names){
			rs.add(getTopologyId(name));
		}
		return rs;
	}
	
	/*
	 * stastic the topology info
	 */
	public static HashMap<String, ArrayList<String>> stasticTopologyInfo(String topologyId){
		if (topologyId == null){
			LOG.warn("Topology id is null !");
			return null;
		}
		
		//key : host_component
		//value : emit or transit value
		HashMap<String, ArrayList<String>> rs = new HashMap<String, ArrayList<String>>();
		
		try{
//			ClusterSummary clusterSummary = client.getClusterInfo();
//			StormTopology stormTopology = client.getTopology(topologyId);
			TopologyInfo topologyInfo = client.getTopologyInfo(topologyId);
			
			List<ExecutorSummary> executorSummaries = topologyInfo.get_executors();
//			List<TopologySummary> topologies = clusterSummary.get_topologies();
			
			for(ExecutorSummary executorSummary : executorSummaries) {
				String id = executorSummary.get_component_id();
				
//				ExecutorInfo executorInfo = executorSummary.get_executor_info();
				
				ExecutorStats executorStats = executorSummary.get_stats();
				
				String host = executorSummary.get_host();
				String component = id;
				String host_componet = String.format("%s\t%s", host,component);
				
				
				// 处理 transit 类型的数据 
				if (executorStats.get_transferred().get(":all-time").size() == 0){
					
					if (!rs.containsKey(host_componet)){
						ArrayList<String> tmpArray = new ArrayList<String>();
						tmpArray.add("transit\t" + 0);
						rs.put(host_componet, tmpArray);
					}
					else {
						rs.get(host_componet).add("transit\t" + 0);
					}
				} else {
					
					String tmp = executorStats.get_transferred().get(":all-time").get("default") + "";
					
					if (!rs.containsKey(host_componet)){
						ArrayList<String> tmpArray = new ArrayList<String>();
						tmpArray.add("transit\t" + (tmp.equals("null") ? "0" : tmp));
						rs.put(host_componet, tmpArray);
					} else {
						
						rs.get(host_componet).add("transit\t" + (tmp.equals("null") ? "0" : tmp));
					}
					
				}
				
				//处理 emitted 类型的数据
				if (executorStats.get_emitted().get(":all-time").size() == 0){
					if (!rs.containsKey(host_componet)){
						ArrayList<String> tmpArray = new ArrayList<String>();
						tmpArray.add("emmitted\t" + 0);
						rs.put(host_componet, tmpArray);
					}
					else {
						rs.get(host_componet).add("emitted\t" + 0);
					}
					
				} else {
					String tmp = executorStats.get_emitted().get(":all-time").get("default") + "";
					
					if (!rs.containsKey(host_componet)){
						ArrayList<String> tmpArray = new ArrayList<String>();
						tmpArray.add("emitted\t" + (tmp.equals("null") ? "0" : tmp));
						rs.put(host_componet, tmpArray);
					} else {
						rs.get(host_componet).add("emitted\t" + (tmp.equals("null") ? "0" : tmp));
					}
					
				}
				
			}
			
		}catch(TTransportException e){
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		} catch (NotAliveException e) {
			e.printStackTrace();
		}
		
		return rs;
	}
	
	/*public static void insertDB(HashMap<String, ArrayList<String>> data, String jobName, String jobId) throws SQLException{
		for (Entry<String, ArrayList<String>> en : data.entrySet()){
			String[] tmpKey = en.getKey().split("\t");       //0:host 1:component
			
			
			for (String value : en.getValue()) {
				String[] tmpValue = value.split("\t");	 //0:type 1:value
				String sql = String.format("insert into jobstatus%s (reportdate, jobname, jobid, host,component,type,value)" +
						" values ('%s','%s','%s','%s','%s','%s','%s')", DateUtil.getMonth(),DateUtil.getToday(),jobName,jobId,tmpKey[0],tmpKey[1],tmpValue[0],tmpValue[1]);
				System.out.println(sql);
				communicator.executeSql(sql);
			}
			
		}
	}*/
	
	public static void monit(){
		try {
			for (String job : jobNames){
				String jobId = getTopologyId(job);
				HashMap<String, ArrayList<String>> jobStatus = stasticTopologyInfo(jobId);
				
				/*if (jobStatus != null){
					insertDB(jobStatus, job, jobId);
				} */
				
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/*public class TimerScheduler {
		
		public   Log Log = LogFactory.getLog(TimerScheduler.class);
		
		private  SchedulerFactory gSchedulerFactory = new StdSchedulerFactory();  
	    private  String JOB_GROUP_NAME = "DATATEAM_JOBGROUP";  
	    private  String TRIGGER_GROUP_NAME = "DATATEAM_TRIGGERGROUP";
	    
	    public void addJob(String jobName, String jobClass, String time) {  
	        try {
	            Scheduler sched = gSchedulerFactory.getScheduler();  
	            JobDetail jobDetail = new JobDetail(jobName, JOB_GROUP_NAME, Class.forName(jobClass));// 任务名，任务组，任务执行类   
	            
	            jobDetail.getJobDataMap().put("jobNamesSet", jobNames);
	            jobDetail.getJobDataMap().put("dbConnection", communicator);
	            
	            // 触发器   
	            CronTrigger trigger = new CronTrigger(jobName, TRIGGER_GROUP_NAME);// 触发器名,触发器组   
	            trigger.setCronExpression(time);// 触发器时间设定   
	            sched.scheduleJob(jobDetail, trigger);  
	            // 启动   
	            if (!sched.isShutdown()){  
	                sched.start();  
	            }  
	        } catch (Exception e) {  
	        	Log.error(e.getMessage());
	        	e.printStackTrace();
	        }  
	    }  

	}*/
	
	public static void main(String[] args) throws InterruptedException{
		StormMonitor m = new StormMonitor();
		try {
			System.out.println(m.client.getClusterInfo());
		} catch (TException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		String cronHour = "00 * * * *  ?";
//		String cronHour1 = "10 * * * *  ?";
//		if (m.ts == null)
//			System.err.println("m.ts is null");
//		m.ts.addJob("update-job-names", "com.qunar.datateam.storm.monitor.service.UpdateJobNameJob", cronHour);
//		m.ts.addJob("update-status", "com.qunar.datateam.storm.monitor.service.updateStatus", cronHour1);
		
	}
	
}
