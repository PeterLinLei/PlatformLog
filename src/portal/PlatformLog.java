// 2019-06-06
package portal;

import java.io.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.sql.*;

public class PlatformLog{
	public String rootPath;	
	public String logFileName;
	public long logFilePointer;
	public long logFilePointer2;
	public String dbDriver;
	public String dbUrl;
	public String dbUser;
	public String dbPassword;
	public String language;

	public int updateRusageStatusInterval = 300;
	
	public void setRootPath(String rp)
	{
		this.rootPath = rp;
	}	
	public String getRootPath()
	{
		return this.rootPath;
	}
	public void setLogFileName(String lfn)
	{
		this.logFileName = lfn;
	}
	public String getLogFileName()
	{
		return this.logFileName;
	}  
	public void setLogFilePointer(long lfp)
	{
		this.logFilePointer = lfp;
	}	
	public long getLogFilePointer()
	{
		return this.logFilePointer;
	}
  	
	public PlatformLog(){
		this.rootPath = new String("./");
		this.updateRusageStatusInterval = 300;
		initialization();
	}
  
	public PlatformLog( String prootPath){
		this.rootPath = prootPath;  
		this.updateRusageStatusInterval = 300;
		initialization();
	}

	public PlatformLog( String prootPath, int pUpdateRusageStatusInterval){
		this.rootPath = prootPath;  
		this.updateRusageStatusInterval = pUpdateRusageStatusInterval;
		initialization();
	}

	public void initialization(){
		// For Database.
		dbDriver = Initialization.fetch(rootPath+"parameters","dbDriver");
		dbUrl = Initialization.fetch(rootPath+"parameters","dbUrl");
		dbUser = Initialization.fetch(rootPath+"parameters","dbUser");
		dbPassword = ThreeDES.decryptKeyFromFile(rootPath+"password");
		this.language = Utility.fetch(dbDriver,dbUrl,dbUser,dbPassword,"name=\'language\'","constant","value");
		// this.language = new String("cn");
	}
	
	public void logLSBStreamToTableLog()
	{
		String line = new String();
		RandomAccessFile logFile = null;
		try{
			logFile = new RandomAccessFile(logFileName,"r");
			if(logFile.length() < this.logFilePointer){
				System.out.println("File lsb.stream has been renewed!");
				logFile.seek(0);
			}
			else
				logFile.seek(this.logFilePointer);
			String timeString = new String();	String timeOne = new String(); long timeTwo=0;
			String type = new String();
			String initiator = new String();	String initiatorOne = new String();
			String action = new String();	String actionOne = new String();
			String involve = new String();
			String detail = new String();
			String jobID = new String();	String jobName = new String();	String jobNameOne = new String();
			SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
			while((line = logFile.readLine ()) != null)
			{
				//System.out.println(line);
				actionOne = Utility.getWordsAt(line, 1);
				action = actionOne.substring(1, actionOne.length()-1);
				// "JOB_NEW" "9.13" 1495678422 101 506 33554435 1 1495678422 0 0 -65535 0 0 "userA" -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 -1 "" 116.10 18 "normal" "" "hpcm" "/Repository/userA/generic_1495678421600zcs1B" "" "" "" "" "/home/userA" "1495678422.101" 0 "" "" "generic_1495678421600" "touch aaa; sleep 30; touch bbb" 0 "" "default" 1 "X86_64" "" "" "" "" 5136 0 "" "" "" -1 -1 -1 64 "" "" -1 "" 0 -1 "" 0 0 0 "" -1 "" -1 "" "/Repository/userA/generic_1495678421600zcs1B" "" "" "" 0 -1 0 "" "" 0 ""
				if(action!=null && action.toString().equals("JOB_NEW"))
				{
					//System.out.println(line);
					timeOne = Utility.getWordsAt(line, 3);	timeTwo = Integer.parseInt(timeOne);	timeString = sdf.format(new Date((timeTwo*1000L)));
					type = new String("INFO"); 
					initiatorOne = Utility.getWordsAt(line, 14);	initiator = initiatorOne.substring(1, initiatorOne.length()-1);
					involve = initiator;
					initiator = new String("USER");
					jobID = Utility.getWordsAt(line, 4);	jobNameOne = Utility.getWordsAt(line, 42);	jobName = jobNameOne.substring(1, jobNameOne.length()-1);
					if(this.language != null && this.language.toString().equals("cn"))
						detail = new String(involve + "提交作业，作业编号是：" + jobID + "，作业名字是：" + jobName);
					else
						detail = new String(involve + " submit job, job id is: " + jobID + ", job name is: " + jobName);
				}
				// "JOB_EXECUTE" "9.13" 1495678422 101 506 15694 "/Repository/userA/generic_1495678421600zcs1B" "/home/userA" "userA" 15694 0 "" -1 "" 0 2147483647 "" -1 "" -1 "" -1
				if(action!=null && action.toString().equals("JOB_EXECUTE"))
				{
					//System.out.println(line);
					timeOne = Utility.getWordsAt(line, 3);	timeTwo = Integer.parseInt(timeOne);	timeString = sdf.format(new Date((timeTwo*1000L)));
					type = new String("INFO"); 
					initiatorOne = Utility.getWordsAt(line, 9);	initiator = initiatorOne.substring(1, initiatorOne.length()-1);
					involve = initiator;
					initiator = new String("USER");
					jobID = Utility.getWordsAt(line, 4);	
					if(this.language != null && this.language.toString().equals("cn"))
						detail = new String("作业（编号：" + jobID + "）开始计算！");
					else
						detail = new String("Job (ID: " + jobID + ") started!");
				}
				// "JOB_FINISH" "9.13" 1495678453 101 506 33554435 1 1495678422 0 0 1495678422 "userA" "normal" "" "" "" "hpcm" "/Repository/userA/generic_1495678421600zcs1B" "" "" "" "1495678422.101" 0 1 "hpcm" 64 116.1 "generic_1495678421600" "touch aaa; sleep 30; touch bbb" 0.061990 0.274958 1480 0 -1 0 0 1477 0 0 0 0 -1 0 0 0 13 8 -1 "" "default" 0 1 "" "" 0 2048 228352 "" "" "" "" 0 "" 0 "" -1 "/userA" "" "" "" -1 "" "" 5136  "" 0 0 -1 64 2048 "select[type == local] order[r15s:pg] " "" -1 "" -1 0 "" "" 31 "/Repository/userA/generic_1495678421600zcs1B" 0 1 "hpcm"
				if(action!=null && action.toString().equals("JOB_FINISH"))
				{
					//System.out.println(line);
					timeOne = Utility.getWordsAt(line, 3);	timeTwo = Integer.parseInt(timeOne);	timeString = sdf.format(new Date((timeTwo*1000L)));
					type = new String("INFO"); 
					initiatorOne = Utility.getWordsAt(line, 12);	initiator = initiatorOne.substring(1, initiatorOne.length()-1);
					involve = initiator;
					initiator = new String("USER");
					jobID = Utility.getWordsAt(line, 4);	jobNameOne = Utility.getWordsAt(line, 28);	jobName = jobNameOne.substring(1, jobNameOne.length()-1);
					if(this.language != null && this.language.toString().equals("cn"))
						detail = new String("作业" + jobID + "（作业名字:" + jobName + "）" + "计算结束！");
					else
						detail = new String("Job " + jobID + "(job name:" + jobName + ")" + " finished!");
				}
				if(action!=null && 
						(action.toString().equals("JOB_NEW") || action.toString().equals("JOB_EXECUTE") || action.toString().equals("JOB_FINISH"))
					)
				{
					Utility.logToDB(dbDriver, dbUrl, dbUser, dbPassword, timeString, "N", "N", "N", "Y", type, initiator, action, involve, detail);
				}
			}		
			// End of while.
			this.logFilePointer = logFile.getFilePointer();
			if(this.logFilePointer != this.logFilePointer2)
			{
				System.out.println((new Date())+" File Pointer: "+this.logFilePointer);
				this.logFilePointer2 = this.logFilePointer;
			}
			logFile.close();
		}
		catch(IOException e)
		{
			System.out.println(e);
		}
	}

	/*
	create table job_log
	(
						job_id varchar(32),			JOB_NEW	/ JOB_FINISH2	4 / 4
						user varchar(32),			JOB_NEW	/ JOB_FINISH2	14 / 9
						user_group varchar(32),		
						job_name varchar(128),		JOB_NEW	/ JOB_FINISH2	42 / 39
						submit_time datetime,		JOB_NEW	/ JOB_FINISH2	3 / 17
						start_time datetime,		JOB_FINISH2				21	"startTime" ->	
						end_time datetime,			JOB_FINISH2				23
						status varchar(32),			
						time_consumed varchar(32),	JOB_FINISH2				64	runtime-> <-maxMem
						queue varchar(32),			JOB_NEW	/ JOB_FINISH2	29 / 25 queue-> <-"resReq" | "fromHost"
						cpu_weight float,
						nodes varchar(32),			JOB_FINISH2				33	"execHosts" -> <-"slotUsages"
						core_number int,			JOB_FINISH2				35	"numProcessors" -> <-"options"
						cpu_time varchar(32),		JOB_FINISH2				"cpuTime"
						exit_info varchar(8),		JOB_FINISH2				"exitInfo"
						expense float,
						accurate enum('Y','N')
	);
	*/
	public void logLSBStreamToTableJobLog()
	{
		String line = new String();
		RandomAccessFile logFile = null;
		try{
			logFile = new RandomAccessFile(logFileName,"r");
			if(logFile.length() < this.logFilePointer){
				System.out.println("File lsb.stream has been renewed!");
				logFile.seek(0);
			}
			else
				logFile.seek(this.logFilePointer);			
			while((line = logFile.readLine()) != null)
			{
				// Get all words after "JOB_*" , because there maybe some "\n" or "^M" separate the line. 
				long logFilePointerT1 = logFile.getFilePointer();
				String nextLine = logFile.readLine();
				if(nextLine!=null){
					if(nextLine.startsWith("\"JOB_"))
					{
						logFile.seek(logFilePointerT1);
					}else
					{
						line = new String(line + nextLine);
						logFilePointerT1 = logFile.getFilePointer();
						while((nextLine = logFile.readLine()) != null && !nextLine.startsWith("\"JOB_"))
						{
							line = new String(line + nextLine);
							logFilePointerT1 = logFile.getFilePointer();
						}
						logFile.seek(logFilePointerT1);
						System.out.println("This line seperate by \"^M\" ");
						System.out.println(line);
					}
				}
				// System.out.println(line);
				// Initialize all parameters. 
				String action = new String();
				String job_id = new String("NA");
				String user = new String("NA");
				String user_group = new String("NA");
				String job_name = new String("NA");
				String project_name = new String("NA");
				String ls_project_name = new String("NA");
				String timeSubString = new String();	String timeSubOne = new String(); long timeSubTwo=0;
				String timeStaString = new String();	String timeStaOne = new String(); long timeStaTwo=0;
				String timeEndString = new String();	String timeEndOne = new String(); long timeEndTwo=0;
				SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
				String submit_time = new String("0000-00-00 00:00:00");
				String start_time = new String("0000-00-00 00:00:00");
				String end_time = new String("0000-00-00 00:00:00");
				String status = new String("NA");
				String time_consumed = new String("0");				String time_consumed_2 = new String("0");
				String queue = new String("NA");
				String cpu_weight = new String("0");
				String nodes = new String("NA");
				String core_number = new String("0");
				String cpu_time = new String("NA");
				String exit_info = new String("NA");
				String resReq = new String("NA");
				String expense = new String("0");		float expenseFloat = 0;
				String accurate = new String("Y");

				action = Utility.getWordsAt(line, 1).replaceAll("\"","");
				if(action!=null && action.toString().equals("JOB_FINISH2"))
				{
					//System.out.println(line);
					job_id = Utility.getWordsAt(line, 4);
					user = Utility.getWordsAfter(line, "userName").replaceAll("\"","");
					user_group = Utility.getLSBUserGroup(user);
					job_name = Utility.getWordsAfter(line, "jobName").replaceAll("\"","");
					if(job_name.length()>32)
						job_name = job_name.substring(0,32);
					project_name = Utility.getWordsAfter(line, "projectName").replaceAll("\"","");
					ls_project_name = Utility.getWordsAfter(line, "licenseProject").replaceAll("\"","");
					timeSubOne = Utility.getWordsAfter(line, "submitTime").replaceAll("\"","");	timeSubTwo = Integer.parseInt(timeSubOne);	timeSubString = sdf.format(new Date((timeSubTwo*1000L)));
					timeStaOne = Utility.getWordsAfter(line, "startTime").replaceAll("\"","");	timeStaTwo = Integer.parseInt(timeStaOne);	timeStaString = sdf.format(new Date((timeStaTwo*1000L)));
					timeEndOne = Utility.getWordsAfter(line, "endTime").replaceAll("\"","");	timeEndTwo = Integer.parseInt(timeEndOne);	timeEndString = sdf.format(new Date((timeEndTwo*1000L)));
					if(timeStaOne.equals("0"))
					{
						submit_time = timeSubString;
						start_time = timeEndString;
						end_time = timeEndString;
						nodes = new String("NA");
						accurate = new String("N");
					}
					else
					{
						submit_time = timeSubString;
						start_time = timeStaString;
						end_time = timeEndString;
						// nodes may contain several nodes: "execHosts" "node157" "execHosts" "node151", now get the first node. 
						nodes = Utility.getWordsAfter(line, "execHosts").replaceAll("\"","");
						if(line.substring((line.indexOf("execHosts")+11),line.length()).contains("execHosts"))
							nodes = nodes + "+";
						accurate = new String("Y");
					}
					status = new String("NA");
					time_consumed = Utility.getWordsAfter(line, "runtime").replaceAll("\"","");
					time_consumed_2 = String.valueOf(timeEndTwo - timeStaTwo);
					if(!time_consumed.equals(time_consumed_2) && (timeStaTwo!=0))
					{
						System.out.println("Job ID:" +job_id + " Time Consumed 1 : " + time_consumed + " Time Consumed 2 : " + time_consumed_2);
						time_consumed = time_consumed_2;
					}
					queue = Utility.getWordsAfter(line, "queue").replaceAll("\"","");
					cpu_weight = new String("1");
					core_number = Utility.getWordsAfter(line, "numProcessors").replaceAll("\"","");
					cpu_time = Utility.getWordsAfter(line, "cpuTime").replaceAll("\"","");
					exit_info = Utility.getWordsAfter(line, "exitInfo").replaceAll("\"","");
					if(exit_info.equals("0"))
						status = new String("DONE");
					if(exit_info.equals("14"))
						status = new String("EXIT");
					//
					expenseFloat = Float.parseFloat(time_consumed) * Float.parseFloat(core_number);
					expense = String.valueOf(expenseFloat);
					String statementJobLog = "insert into job_log(job_id,user,user_group,job_name,project_name,ls_project_name,submit_time,start_time,end_time,status,time_consumed,queue,cpu_weight,nodes,core_number,cpu_time,exit_info,expense,accurate) "
							+ "values(\'"+job_id+"\',\'"+user+"\',\'"+user_group+"\',\'"+job_name+"\',\'"+project_name+"\',\'"+ls_project_name+"\',\'"+submit_time+"\',\'"+start_time+"\',\'"+end_time+"\',\'"
							+status+"\',\'"+time_consumed+"\',\'"+queue+"\',\'"+cpu_weight+"\',\'"+nodes+"\',\'"+core_number+"\',\'"+cpu_time+"\',\'"
							+exit_info+"\',\'"+expense+"\',\'"+accurate+"\')";
					Utility.executeSQL(this.dbDriver, this.dbUrl, this.dbUser, this.dbPassword, statementJobLog);
					// Insert LSF Project name if not exist.
					String statementLSFProject = "insert into lsf_project(cluster_name,project_name) "
							+ "values(\'"+"NA"+"\',\'"+project_name+"\')";
					Utility.executeSQL(this.dbDriver, this.dbUrl, this.dbUser, this.dbPassword, statementLSFProject);
					/*
					 * "JOB_FINISH2" "10.1" 1555144809 1219 38 "userId" "505" "userName" "user2" "numProcessors" "1" "options" "33816578" "jStatus" "64" "submitTime" "1555144746" "termTime" "0" "startTime" "1555144789" "endTime" "1555144809" "queue" "normal" "resReq" "type=any" "fromHost" "master" "jobFile" "1555144746.1219" "numExHosts" "1" "execHosts" "master" "slotUsages" "1" "cpuTime" "0.048992" "command" "sleep 20" "ru_utime" "0.022996" "ru_stime" "0.025996" "ru_maxrss" "0" "ru_nswap" "0" "projectName" "default" "exitStatus" "0" "maxNumProcessors" "1" "exitInfo" "0" "chargedSAAP" "/user2" "numhRusages" "0" "runtime" "44" "maxMem" "0" "avgMem" "0" "effectiveResReq" "select[(type = any ) && (type == any)] order[r15s:pg] " "serial_job_energy" "0.000000" "numAllocSlots" "1" "allocSlots" "master" "ineligiblePendingTime" "-1" "options2" "1040" "hostFactor" "116.099998"
					 * "JOB_FINISH2" "10.1" 1556009100 2181 39 "userId" "502" "userName" "lsfadmin" "numProcessors" "1" "options" "33816578" "jStatus" "64" "submitTime" "1556009050" "termTime" "0" "startTime" "1556009070" "endTime" "1556009100" "queue" "normal" "resReq" " rusage[mem=1000,swp=100,ansys=2,mechhpc=4]" "fromHost" "master" "jobFile" "1556009050.2181" "numExHosts" "1" "execHosts" "master" "slotUsages" "1" "cpuTime" "0.046991" "command" "sleep 30" "ru_utime" "0.019996" "ru_stime" "0.026995" "ru_maxrss" "0" "ru_nswap" "0" "projectName" "P2" "exitStatus" "0" "maxNumProcessors" "1" "exitInfo" "0" "chargedSAAP" "/lsfadmin" "licenseProject" "default" "numhRusages" "0" "runtime" "30" "maxMem" "0" "avgMem" "0" "effectiveResReq" "select[type == local] order[r15s:pg] rusage[swp=100.00,mem=1000.00,ansys=2.00,mechhpc=4.00] " "serial_job_energy" "0.000000" "numAllocSlots" "1" "allocSlots" "master" "ineligiblePendingTime" "-1" "options2" "2102288" "hostFactor" "116.099998"
					 * "JOB_FINISH2" "10.1" 1559642892 8844000 45 "userId" "40011" "userName" "chaqiu01" "numProcessors" "1" "options" "33833011" "jStatus" "64" "submitTime" "1559642497" "termTime" "0" "startTime" "1559642497" "endTime" "1559642892" "queue" "low" "resReq" "select[(type == any )] order[slots:mem] rusage[Xcelium_Single_Core=1:mem=4096]" "fromHost" "bc-c18-1-7" "cwd" "/project/ai/scratch01/chaqiu01/regression/zy_aipu_core_xprop_sanity_session.chaqiu01.19_06_04_17_25_54_9905/chain_0" "outFile" "/project/ai/scratch01/chaqiu01/regression/zy_aipu_core_xprop_sanity_session.chaqiu01.19_06_04_17_25_54_9905/chain_0/debug_logs/vm_remote_job_2bc30dbd-e0bd-44d4-b659-9f862be2a6d5.out" "jobFile" "224/1559642497.8844000" "numExHosts" "1" "execHosts" "bc-f19-2-9" "slotUsages" "1" "cpuTime" "390.053711" "jobName" "zy_aipu_core_xprop_sanity_session.chaqiu01.19_06_04_17_25_54_9905_zy_aipu_core_xprop_sanity_session___core_base_tests___avs_vector_arith_tests___test_avs_TCUGE_H_PD_PG_TN_TM__run_1533" "command" "/arm/tools/cadence/vmanager/19.03.001/tools/vmgr/jobs_manager/bin/vm_remote_job -host 10.193.8.72 -port 44507 -id 2bc30dbd-e0bd-44d4-b659-9f862be2a6d5" "ru_utime" "380.915091" "ru_stime" "9.138610" "ru_maxrss" "2085255" "ru_nswap" "2851352" "projectName" "pjzhouyi" "exitStatus" "0" "maxNumProcessors" "1" "exitInfo" "0" "chargedSAAP" "/TOP/E1/E1MISC/pjzhouyi/chaqiu01" "licenseProject" "default" "numhRusages" "0" "runtime" "395" "maxMem" "2085255" "avgMem" "2085255" "effectiveResReq" "select[((type == any )) && (type == any )] order[slots:mem] rusage[mem=4096.00,Xcelium_Single_Core=1.00] " "options3" "33554432" "subcwd" "/project/ai/scratch01/chaqiu01/regression/zy_aipu_core_xprop_sanity_session.chaqiu01.19_06_04_17_25_54_9905/chain_0" "serial_job_energy" "0.000000" "numAllocSlots" "1" "allocSlots" "bc-f19-2-9" "jobDescription" "support-universal" "ineligiblePendingTime" "0" "options2" "2104400" "hostFactor" "870.000000"
					 * Sample 1: "resReq" "type=any" "fromHost" "master" 
					 * Sample 2: "resReq" " rusage[mem=1000,swp=100,ansys=2,mechhpc=4]" "fromHost" "master"
					 * Sample 3: "resReq" "select[(type == any )] order[slots:mem] rusage[Xcelium_Single_Core=1:mem=4096]" "fromHost" "bc-c18-1-7" 
					 */
					//
					resReq = Utility.getWordsBetween(line, "resReq", "fromHost").replaceAll("\"","");
					if(resReq!=null && resReq.contains("rusage"))
					{
						int rusageBeginIndex = resReq.indexOf("rusage");
						resReq = resReq.substring(rusageBeginIndex);
						int rusageEndIndex = resReq.indexOf("]");
						resReq = resReq.substring(0, rusageEndIndex);
						resReq = resReq.replace("rusage[", " ");	// Replace "rusage[", ",", "]" with " "
						resReq = resReq.replace(",", " ");
						resReq = resReq.replace(":", " ");
						resReq = resReq.replace("]", " ");
						resReq = resReq.replace("  ", " ");		resReq = resReq.replace("  ", " ");	 // Replace multiply "  " with " "
					}
					else
						resReq = new String("");
					// Add " numProcessors=XXX" to resReq, for report convenient. 
					resReq = resReq + " numProcessors=" + core_number;
					// System.out.println(resReq);
					if(resReq!=null && !resReq.equals(""))
					{
						int resourceNumber = 0;
						resourceNumber = Utility.getSubstringNumber(resReq, "=");
						// System.out.println(resourceNumber);
						for(int i=0; i<resourceNumber; i++)
						{
							String resourceI = Utility.getWordsAt(resReq, i+1);
							// System.out.println(resourceI);
							String resource_name = new String();
							String resource_amount = new String();
							if(resourceI!=null && resourceI.contains("="))
							{
								resource_name = resourceI.substring(0, resourceI.indexOf("="));
								resource_amount = resourceI.substring(resourceI.indexOf("=")+1, resourceI.length());								
								String statementRusageLog = "insert into rusage_log(job_id,user,user_group,job_name,project_name,ls_project_name,submit_time,start_time,end_time,status,time_consumed,queue,cpu_weight,nodes,core_number,cpu_time,exit_info,resource_name,resource_amount,expense,accurate) "
										+ "values(\'"+job_id+"\',\'"+user+"\',\'"+user_group+"\',\'"+job_name+"\',\'"+project_name+"\',\'"+ls_project_name+"\',\'"+submit_time+"\',\'"+start_time+"\',\'"+end_time+"\',\'"
										+status+"\',\'"+time_consumed+"\',\'"+queue+"\',\'"+cpu_weight+"\',\'"+nodes+"\',\'"+core_number+"\',\'"+cpu_time+"\',\'"
										+exit_info+"\',\'"+resource_name+"\',\'"+resource_amount+"\',\'"+expense+"\',\'"+accurate+"\')";
								Utility.executeSQL(this.dbDriver, this.dbUrl, this.dbUser, this.dbPassword, statementRusageLog);
								for(long t1=timeStaTwo; t1<timeEndTwo; t1=t1+this.updateRusageStatusInterval)
								{
									if(!timeStaOne.equals("0"))
									{
										long updateTimeL = t1 - t1 % this.updateRusageStatusInterval;
										String updateTimeString = new String();
										float resource_amount_accurateF = 0;
										String resource_amount_accurateS = new String();
										if(timeEndTwo - t1 < this.updateRusageStatusInterval)
										{
											resource_amount_accurateF = Float.parseFloat(resource_amount) * ((float)(timeEndTwo - t1) / (float)this.updateRusageStatusInterval);
											DecimalFormat fnum = new DecimalFormat("##0.00");
											resource_amount_accurateS = fnum.format(resource_amount_accurateF);
										}
										else
											resource_amount_accurateS = resource_amount;
										updateTimeString = sdf.format(new Date((updateTimeL*1000L)));
										String statementRusageStatus = "insert into rusage_status(job_id,user,user_group,job_name,project_name,ls_project_name,submit_time,start_time,end_time,status,time_consumed,queue,cpu_weight,nodes,core_number,cpu_time,exit_info,resource_name,resource_amount,expense,update_time,accurate) "
												+ "values(\'"+job_id+"\',\'"+user+"\',\'"+user_group+"\',\'"+job_name+"\',\'"+project_name+"\',\'"+ls_project_name+"\',\'"+submit_time+"\',\'"+start_time+"\',\'"+end_time+"\',\'"
												+status+"\',\'"+time_consumed+"\',\'"+queue+"\',\'"+cpu_weight+"\',\'"+nodes+"\',\'"+core_number+"\',\'"+cpu_time+"\',\'"
												+exit_info+"\',\'"+resource_name+"\',\'"+resource_amount_accurateS+"\',\'"+expense+"\',\'"+updateTimeString+"\',\'"+accurate+"\')";
										Utility.executeSQL(this.dbDriver, this.dbUrl, this.dbUser, this.dbPassword, statementRusageStatus);
									}
								}
							}
							else
							{
								System.out.println("Error: resourceI didn't contain '='!");
								System.out.println(resourceI);
								System.out.println(resReq);
								System.out.println(line);
							}
						}
					}
					System.out.println((new Date())+" File Pointer: "+logFile.getFilePointer());
				}
			}
			// End of while.
			this.logFilePointer = logFile.getFilePointer();
			if(this.logFilePointer != this.logFilePointer2)
			{
				System.out.println((new Date())+" File Pointer: "+this.logFilePointer);
				this.logFilePointer2 = this.logFilePointer;
			}
			logFile.close();
		}
		catch(IOException e)
		{
			System.out.println(e);
		}
	}
	
	public void logToDB()
  {
    String line = new String();
    RandomAccessFile logFile = null;
    try{
      logFile = new RandomAccessFile(logFileName,"r");
      if(logFile.length() < this.logFilePointer){
    	  System.out.println("Flex LM has restarted!");
    	  logFile.seek(0);
      }
      else
    	  logFile.seek(this.logFilePointer);
      while((line = logFile.readLine ()) != null)
      {
        long file_pointer = 0 ;
        String date_time = new String();
        String feature = new String();
        String account = new String();
        String host_name = new String();
        String feature_num = new String();
        // System.out.println(line);
        if(line.contains("OUT:")||line.contains("IN:"))
        {
          // System.out.println(line);
          // Machince date yyyy-MM-dd plus log hour HH:mm:ss .
          Date date = new Date(); 
          date_time = Utility.getWordsAt(line,1);
          SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");                   
		  date_time = sdf.format(date)+" "+date_time;	
		  // Compare hour and decide whether at midnight turn, and adjust date.
		  String hour1 = new String();
		  String hour2 = new String();
		  hour1 = line.substring(0,2);
		  SimpleDateFormat sdf_hour = new SimpleDateFormat("HH");
		  hour2 = sdf_hour.format(date);
		  SimpleDateFormat sdf_all = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		  try{
            Date date_time2 = sdf_all.parse(date_time);
		    if( hour1.equals("23") && hour2.equals("00") )
		    {
		      date_time2 = new Date(date_time2.getTime() - 24*3600*1000 );
			  date_time = sdf_all.format(date_time2);
		    }
		    if( hour1.equals(" 0") && hour2.equals("23") )
		    {
			  date_time2 = new Date(date_time2.getTime() + 24*3600*1000 );
			  date_time = sdf_all.format(date_time2);
		    }
          }catch(Exception e){
		  	System.out.println("1"+e);
          }
		  // fetch feature, account, host_name and feature_num. 
          feature = line.substring(line.indexOf("\"")+1,line.lastIndexOf("\""));
          account = line.substring(line.lastIndexOf("\"")+2,line.indexOf("@"));
          if(line.contains("license"))
		  {
            feature_num = line.substring(line.lastIndexOf("(")+1,line.indexOf("license")-1);
          	host_name = line.substring(line.indexOf("@") + 1 ,line.lastIndexOf("(") - 2);
		  }
		  else
		  { 
            feature_num = "1";
			host_name = line.substring(line.indexOf("@") + 1 ,line.length());
          }
		  // System.out.println(date_time+"\t"+feature+"\t"+account+"\t"+host_name+"\t"+feature_num);
          Connection conn;
          Statement stmt;										
          try{
            Class.forName(dbDriver);
            conn = DriverManager.getConnection(dbUrl,dbUser,dbPassword);
            stmt = conn.createStatement();
            if(line.contains("OUT:"))
            {
			  // Get file pointer.
			  file_pointer = logFile.getFilePointer(); 
			  // Insert. 
              String sql = "insert into license_usage_log(file_pointer,user_name,host_name,feature,feature_num,begin) values(\'"+String.valueOf(file_pointer)+"\',\'"+account+"\',\'"+host_name+"\',\'"+feature+"\',\'"+feature_num+"\',\'"+date_time+"\')";
              stmt.executeUpdate(sql);
            }
            if(line.contains("IN:"))
            {
			  // fetch company and remain token. 
              String company = new String("NULL");
			  long token = 0 ;
              String sql_fetch_company_and_token = "select company,token from user where account=\'"+account+"\'";
              ResultSet rs = stmt.executeQuery(sql_fetch_company_and_token);
              if(rs.first())
              {
                company = rs.getString("company");
				token = rs.getInt("token");
                //System.out.println(company + "\t" + token);
              }
			  else
			  {
			    company = "NULL";
				token = -1;
			  }
              rs.close();
			
			  // fetch feature_token.									
              String feature_token = new String("0");
              String sql_fetch_feature_token = "select token from product_feature where feature=\'"+feature+"\'";
              rs = stmt.executeQuery(sql_fetch_feature_token);
              if(rs.first())
              {
                feature_token = rs.getString("token");
                //System.out.println(feature_token);
              }
			  else{
			  	feature_token = "0";
			  }
              rs.close();		
											
              // fetch begin time and calculate total used time(s). 
			  String begin_time = new String("1970-01-01 00:00:00.0");
              String sql_fetch_begin_time = "select begin, file_pointer from license_usage_log where user_name=\'"+account+"\' and host_name=\'"+host_name+"\' and feature=\'"+feature+"\' and feature_num=\'"+feature_num+"\' and end is null order by file_pointer";
              rs = stmt.executeQuery(sql_fetch_begin_time);
              if(rs.first())
              {
                begin_time = rs.getString("begin");
				file_pointer = rs.getLong("file_pointer");
                // System.out.println(begin_time);
				// System.out.println(file_pointer);
              }
			  else
			  {
			    begin_time = "1970-01-01 00:00:00.0";
				file_pointer = -1;
			  }
              rs.close();			
              
              String end_time = new String(date_time);
												
              long total_time=0; //second
											
              SimpleDateFormat bdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");						
              SimpleDateFormat edf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
              try{
                Date begin = bdf.parse(begin_time);
                Date end = edf.parse(end_time);
                total_time = (end.getTime()-begin.getTime())/1000;
                //System.out.println(total_time);
              }catch(Exception e){
			    System.out.println("Date begin = bdf.parse(begin_time);");
                System.out.println(e.getMessage());
              }
				
			  // calculate total token. 								
              long total_token=0;
              total_token = total_time * Integer.parseInt(feature_num) * (Integer.parseInt(feature_token)); 
              //System.out.println(total_token);									
												
              String sql = "update license_usage_log set company=\'"+company+"\', feature_token=\'"+feature_token+"\', end=\'"+date_time+"\', total_time=\'"+String.valueOf(total_time)+"\', total_token=\'"+String.valueOf(total_token)+"\' where file_pointer=\'"+String.valueOf(file_pointer)+"\' and user_name=\'"+account+"\' and host_name=\'"+host_name+"\' and feature=\'"+feature+"\' and feature_num=\'"+feature_num+"\' and end is null";
              stmt.executeUpdate(sql);	
			  
			  // Decrease user's remain tooken. 
			  token = token - total_token;
			  String sql_decrease_token = "update user set token=\'"+String.valueOf(token)+"\' where account=\'"+account+"\'";
			  stmt.executeUpdate(sql_decrease_token);		  											
            }
            stmt.close();
            conn.close();
          }
		  catch(Exception e)
          {
            System.out.println(e);
          }										
        }		// end of if. 
      }			// end of while. 
	  this.logFilePointer = logFile.getFilePointer();
	  if(this.logFilePointer != this.logFilePointer2)
	  {
		  System.out.println((new Date())+" File Pointer: "+this.logFilePointer);
		  this.logFilePointer2 = this.logFilePointer;
	  }
	  logFile.close();
    }
    catch(IOException e)
	{
      System.out.println(e);
    }
  }

	public static void main(String args[])
	{
		// # P0: jar file; P1: root directory; P2: lsb.stream file path; P3: file pointer; P4: update rusage status interval; P5: sleep interval.
		PlatformLog fls = new PlatformLog(args[0],Integer.parseInt(args[3]));
		fls.setLogFileName(args[1]); 
		fls.setLogFilePointer(Integer.parseInt(args[2]));
		//PlatformLog fls = new PlatformLog("C:\\Users\\IBM_ADMIN\\Workspaces\\PlatformLog\\");
		//fls.setLogFileName("C:\\Users\\IBM_ADMIN\\Workspaces\\PlatformLog\\lsb.stream"); 
		//fls.setLogFilePointer(0);
		while(true)
		{
			// fls.logLSBStreamToTableLog();
			fls.logLSBStreamToTableJobLog();
			try{
				Thread.sleep(Integer.parseInt(args[4]) * 1000);
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		}
	}
}
