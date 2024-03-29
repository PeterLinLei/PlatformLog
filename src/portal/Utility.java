// 
// GridLS, LicenseStatistic, Initialization, Portal.
package portal; 
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.*;
import java.text.ParseException;
import java.text.DecimalFormat;

import javax.servlet.http.HttpServletRequest;

/*
	public static String rm( String fileName )
	public static String submitToSGE( String commandFileName )
	public static String fetch( String dbDriver, String dbUrl, String dbUser, String dbPassword, String condition, String table, String column)
	public static String makeDir( String jobDir, String user, String userGroup )
	public static String dos2unix( String fileName )
	public static String getWordsAt(String line,int index)
	public static int execute( String dbDriver, String dbUrl, String dbUser,String dbPassword,String statement)	
*/
public class Utility{

	public Utility(){
	}

	public static boolean createFile(String file,String filePath)
	{
		//write file
		RandomAccessFile f = null;
		try{
			File f2 = new File(filePath);
			if (f2.exists()) {
				f2.delete();
				f2.createNewFile();
			}	
			f = new RandomAccessFile(f2,"rw");
			f.writeBytes(file);
			f.close();
		}catch(IOException e){ 
			System.out.println(e);
			return false;	
		}
		return true;
	}
	
	// execute INSERT,UPDATE or DELETE statement. 
	public static int executeSQL( String dbDriver, String dbUrl, String dbUser,String dbPassword,
			String statement)
	{
		int executeRecordNumber = -1;
		try{
			Connection conn1;
			Statement stmt1;	
			Class.forName(dbDriver);
			conn1 = DriverManager.getConnection(dbUrl,dbUser,dbPassword);
			stmt1 = conn1.createStatement();
			executeRecordNumber = stmt1.executeUpdate(statement);
			stmt1.close();
			conn1.close();	
		}	
		catch(Exception e)
		{
			System.out.println("public static int execute( String dbDriver, String dbUrl, String dbUser,String dbPassword,String statement)");
			System.out.println(e);
		}
		return executeRecordNumber;		
	}

	// Log to table log. 
	/*
		time datetime,
		is_admin_log enum('Y','N'),
		is_security_log enum('Y','N'),
		is_audit_log enum('Y','N'),
		is_user_log enum('Y','N'),
		type varchar(16),
		initiator varchar(32),
		action varchar(32),
		involve varchar(32),
		detail varchar(1024)
	*/
	public static int logToDB( String dbDriver, String dbUrl, String dbUser,String dbPassword,
			String time,String is_admin_log,String is_security_log,String is_audit_log,String is_user_log,
			String type,String initiator,String action,String involve,String detail)
	{
		int executeRecordNumber = -1;
		if(time == null || time.toString().equals(""))
		{
			Date date = new Date();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			time = sdf.format(date);
		}
		String statement = "insert into log(time,is_admin_log,is_security_log,is_audit_log,is_user_log,type,initiator,action,involve,detail) "
				+ "values(\'"+time+"\',\'"+is_admin_log+"\',\'"+is_security_log+"\',\'"+is_audit_log+"\',\'"+is_user_log+"\',\'"
				+type+"\',\'"+initiator+"\',\'"+action+"\',\'"+involve+"\',\'"+detail+"\')";
		try{
			Connection conn1;
			Statement stmt1;	
			Class.forName(dbDriver);
			conn1 = DriverManager.getConnection(dbUrl,dbUser,dbPassword);
			stmt1 = conn1.createStatement();
			executeRecordNumber = stmt1.executeUpdate(statement);
			stmt1.close();
			conn1.close();	
		}	
		catch(Exception e)
		{
			System.out.println("public static int logToDB");
			System.out.println(e);
		}
		return executeRecordNumber;		
	}
	
	public static String getNextNMonthDate( int nextNMonth)
	{
		String returnValueSting = new String("");
		Date returnValueDate;		
		TimeZone timezone = TimeZone.getTimeZone("GMT+8");
		Calendar calendar1 = Calendar.getInstance(timezone);
		Calendar calendar2 = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		sdf.setCalendar(calendar1); 
		calendar2.set(Calendar.DATE, calendar2.getActualMinimum(Calendar.DAY_OF_MONTH));
		calendar2.add(Calendar.MONTH, nextNMonth);
		returnValueDate = calendar2.getTime();
		returnValueSting = sdf.format(returnValueDate);
		return returnValueSting;
	}

	public static String addCalendar(String inputDateString, int nDays)
	{
		String outputDateString = new String("");
 
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date inputDateDate = new Date();
		Date outputDateDate = new Date();
		try{
			inputDateDate = sdf.parse(inputDateString);	
		}
		catch(Exception e)
		{}
		
		Calendar c1 = Calendar.getInstance();  
		c1.setTime(inputDateDate);  
		c1.add(Calendar.DAY_OF_MONTH, nDays);       
 
		outputDateDate = c1.getTime();
		outputDateString = sdf.format(outputDateDate);
		
		return outputDateString;
	}
	
	// fetch column from table where condition. 
	public static String[][] selectSQL( String dbDriver, String dbUrl, String dbUser, String dbPassword, String condition, String table, String column)
	{
		String result[][] = new String[512][16];
		int columnNumber = 0;
		int rowNumber = 0;
		if(!column.equals(""))
		{
			for(int i=0;i<column.length();i++)
			{
				if(column.charAt(i)==',')
					columnNumber ++;
			}
			columnNumber ++;
			result[0][1] = String.valueOf(columnNumber);
		}
		else{ return result;}
		try{
			Connection conn1;
			Statement stmt1;	
			ResultSet rs1;
			Class.forName(dbDriver);
			conn1 = DriverManager.getConnection(dbUrl,dbUser,dbPassword);
			String fetch_column = new String();
			if(condition.equals(""))
				fetch_column = "select "+column+" from "+table;
			else
				fetch_column = "select "+column+" from "+table+" "+condition;
			System.out.println(fetch_column);
			stmt1 = conn1.createStatement();
			rs1 = stmt1.executeQuery(fetch_column);
			rowNumber = 1;
			while(rs1.next() && rowNumber<510)
			{
				for(int i=0;i<columnNumber;i++)
				{
					result[rowNumber][i] = rs1.getString( i+1 );
				}
				rowNumber++;
			}
			result[0][0] = String.valueOf( rowNumber-1 );
			rs1.close();
			stmt1.close();
			conn1.close();	
		}	
		catch(Exception e)
		{
		}
		return result;		
	}
	
	// remove file
	public static String rm( String fileName )
	{
		try{
			Process process = Runtime.getRuntime().exec("rm -rf " + fileName);
			InputStreamReader ir=new InputStreamReader(process.getInputStream());
			BufferedReader br = new BufferedReader(ir);
			String line = br.readLine();
			if(line!=null)
				return "fail";
		}catch(IOException e){
			System.err.println("IOException " + e.getMessage());
			return "fail";
		}
		return "success"; 			
	}
	
	// Submit to SGE. 
	public static String submitToSGE( String commandFileName, String isRemoteSubmit, String remoteSubmitHost, String mpiRemsh )
	{
		String jobId = new String("NULL"); 
		try{
			Runtime.getRuntime().exec("chmod +x "+commandFileName);
			Process process;			
			if(isRemoteSubmit != null && isRemoteSubmit.equals("yes"))
			{
				System.out.print("Remote submission! ");
				process = Runtime.getRuntime().exec( mpiRemsh +" "+remoteSubmitHost+" "+commandFileName);
			}
			else
			{
				System.out.print("Local submission! ");
				// System.out.println(commandFileName);
				process = Runtime.getRuntime().exec(commandFileName);
			}
			InputStreamReader ir=new InputStreamReader(process.getInputStream());
			BufferedReader br = new BufferedReader(ir);
			String line = new String();
			do{
				line = br.readLine();
				if(line!=null && line.contains("Your job") && line.contains("has been submitted"))
				{
					jobId=Utility.getWordsAt(line,3);
					System.out.print("Job ID: " + jobId + ".\n" );
					break;
				}
			}
			while(line !=null);
		}catch(IOException e){
			System.err.println("IOException " + e.getMessage());
			return "NULL";
		}
		return jobId; 	
	}

	public static void submitToPortal(HttpServletRequest request)
	{
	    Class myClass;
	    Object myObject;
	    MyClassLoader mcl = new MyClassLoader();
	    try{
	    	
	    	System.out.println(System.getProperty("java.class.path"));
	    	myClass = mcl.load("/opt/tomcat/webapps/SanYuan/encrypt/JobSubmitBean.class","portal.JobSubmitBean");	    
	    	System.out.println("JobSubmitBean load success!");
	    	myObject = myClass.newInstance();
	    	/*
	    	java.lang.reflect.Method methodConstruct = myClass.getMethod("construct",new Class[]{request.getClass()});
	    	System.out.println("methodConstruct name=" + methodConstruct.getName());
	    	methodConstruct.invoke(myObject,new Object[]{request});	    	
	    	*/
	    	((JobSubmitInterface)myObject).construct(request);
	    	((JobSubmitInterface)myObject).submitJob();
	    }
	    catch(Exception e)
	    {
	    	System.out.println("submitToPortal: "+e);
	    }
	}
	
	// fetch column from table where condition. 
	public static String fetch( String dbDriver, String dbUrl, String dbUser, String dbPassword, String condition, String table, String column)
	{
		String result = new String("null");
		try{
			Connection conn1;
			Statement stmt1;	
			ResultSet rs1; 
			Class.forName(dbDriver);
			conn1 = DriverManager.getConnection(dbUrl,dbUser,dbPassword);
			String fetch_column;
			if(condition==null || condition.equals("null") || condition.equals(""))
				fetch_column = "select "+column+" from "+table;
			else
				fetch_column = "select "+column+" from "+table+" where "+condition;
			// System.out.println(fetch_column);
			stmt1 = conn1.createStatement();
			rs1 = stmt1.executeQuery(fetch_column);
			if(rs1.first())
			{
				result = rs1.getString(1);
				// System.out.println(result);
			}
			rs1.close();
			stmt1.close();
			conn1.close();	
		}	
		catch(Exception e)
		{
			System.out.println("public static String fetch( String dbDriver, String dbUrl, String dbUser, String dbPassword, String condition, String table, String column)" + e);
			return "fail";
		}
		return result;		
	}
	
	// Make directory, and run "chown -R user:userGroup dir"	
	// Make directory, and run "chown -R user dir"
	public static String makeDir( String jobDir, String user, String userGroup )
	{
		try
		{
			String parentDir = jobDir.substring(0,jobDir.lastIndexOf('/'));
			//System.out.println(parentDir);
			if(!(new File(parentDir)).isDirectory())
				makeDir(parentDir,user,userGroup);
			if(!(new File(jobDir).isDirectory()))
			{
				//System.out.println("jobDir "+jobDir);
				new File(jobDir).mkdir();
				try
				{
					String[] commands = new String[]{"chown","-R",user,jobDir};
					for(int i=0;i<commands.length;i++)
					{
						//System.out.println(commands[i]);
					}
					Process process = Runtime.getRuntime().exec(commands);
					InputStreamReader er=new InputStreamReader(process.getErrorStream());
					BufferedReader input = new BufferedReader(er);
					String line;
					while ((line = input.readLine()) != null){
						System.out.println(line);
					}
				}catch (IOException e){
					System.out.println("public String makeDir(){");
					System.err.println ("chown ERROR! " + e.getMessage());
					return "fail";
				}
			}
			else
			{
				System.out.println("Job directory already exist! ");
				return "exist";
			}
		}catch(SecurityException e)
		{
			System.out.println("Can not make directory! " + e);
			return "fail";
		}
		return "success";
	}

	public static boolean isFileExist(String fileAbsoluteName)
	{
		boolean exist = false;
		try{
			File file = new File(fileAbsoluteName);
			if (file.exists()) 
				exist = true;
			else
				exist = false;
		}catch(Exception e){ 
			System.out.println(e);
		}
		return exist;
	}

	// Compress 
	public static boolean compress(String workingDir,String compressedFileName,String compressFileList)
	{
		// Write zip.sh file 
		RandomAccessFile zipShellFile = null;
		try{
			File zipShellFile2 = new File(workingDir+"/zip.sh");
			if (zipShellFile2.exists()) {
				zipShellFile2.delete();
				zipShellFile2.createNewFile();
			}	
			zipShellFile = new RandomAccessFile(zipShellFile2,"rw");
			zipShellFile.writeBytes("cd "+workingDir+"\n");
			zipShellFile.writeBytes("rm -rf "+compressedFileName+"\n");
			zipShellFile.writeBytes("zip -r "+compressedFileName+" "+compressFileList+"\n");
			zipShellFile.close();
		}catch(IOException e){ 
			System.out.println(e);
		}
		// chmod +x zip.sh and run zip.sh
		try{
			Runtime.getRuntime().exec("chmod +x "+workingDir+"/zip.sh");
			Process process = Runtime.getRuntime().exec(workingDir+"/zip.sh");
			InputStreamReader is=new InputStreamReader(process.getInputStream());
			BufferedReader input = new BufferedReader(is);
			String line;
			while ((line = input.readLine()) != null){
				System.out.println(line);
			}
		}catch(IOException e){
			System.err.println("IOException " + e.getMessage());
		}
		return true;
	}
	
	// Run command, return true or false. 
	public static Boolean RunCommand( String command)
	{
		Boolean returnValue = false;
		try{
			Runtime rt=Runtime.getRuntime(); 
			Process pcs;
			if(command!=null && !command.toString().equals(""))
				pcs=rt.exec(command);		
			else
				return false;
			InputStream is=pcs.getInputStream();
			BufferedReader br= new BufferedReader(new InputStreamReader(is));
			String line=new String();
			while((line = br.readLine()) != null)
			{
				System.out.println(line+"\n");
				if(line.toString().equals("1"))
					returnValue = true;
			}
		}
		catch(IOException e){
			System.out.println("public static Boolean RunCommand( String command)");
			System.out.println(e);
			return false;
		}
		return returnValue;
	}

	// Run command, return a string. 
	// return "1 returnString" means success or "0" means failure. 
	public static String RunCommand2( String command)
	{
		String returnValue = new String("0");
		try{
			Runtime rt=Runtime.getRuntime(); 
			Process pcs;
			if(command!=null && !command.toString().equals(""))
				pcs=rt.exec(command);		
			else
				return new String("0");
			InputStream is=pcs.getInputStream();
			BufferedReader br= new BufferedReader(new InputStreamReader(is));
			String line=new String();
			while((line = br.readLine()) != null)
			{
				// System.out.println(line+"\n");
				if(Utility.getWordsAt(line.toString(),1).equals("1"))
					returnValue = new String(line);
			}
		}
		catch(IOException e){
			System.out.println("public static String RunCommand2( String command)");
			System.out.println(e);
			return new String("0");
		}
		return returnValue;
	}

	public static String getLSBUserGroup( String user)
	{
		String returnValue = new String("NA");
		String t1 = new String("");
		t1=Utility.RunCommand2("MyGetUserGroup.sh " + user);
		if(t1!=null && Utility.getWordsAt(t1, 1).equals("1"))
		{
			returnValue=Utility.getWordsAt(t1, 2);
		}
		return returnValue;
	}
	
	// Run dos2unix over a file. 	
	public static String dos2unix( String fileName,String user)
	{
		try{
			Runtime rt=Runtime.getRuntime(); 
			Process pcs;
			if(user!=null && !user.toString().equals(""))
				pcs=rt.exec("dos2unix "+fileName);		
			else
				pcs=rt.exec("dos2unix "+fileName);	
			InputStream is=pcs.getInputStream();
			BufferedReader br= new BufferedReader(new InputStreamReader(is));
			String line=new String();
			while((line = br.readLine()) != null)
			{
				System.out.println(line+"\n");
			}	
		}
		catch(IOException e){
			System.out.println("public String dos2unix( String fileName)");
			System.out.println(e);
			return "fail";
		}
		return "success";
	}

	/* Check whether a complex password string.
	 * Must contain lower character, upper character and number,
	 * Also length is 10 or longer than 10 and shorter than 20.
	 */
	public static boolean isComplexString( String password)
	{
		boolean returnValue = false;
		boolean containLowerCharacter = false;
		boolean containUpperCharacter = false;
		boolean containNumber = false;
		boolean longEnough = false;
		for(int i=0; i<password.length(); i++)
		{
			if (Character.isLowerCase(password.charAt(i)))
			{
				containLowerCharacter = true;
			}
			if (Character.isUpperCase(password.charAt(i)))
			{
				containUpperCharacter = true;
			}
			if (Character.isDigit(password.charAt(i)))
			{
				containNumber = true;
			}
		}
		if(password.length()>=10 && password.length()<=20)
		{
			longEnough = true;
		}
		returnValue = containLowerCharacter && containUpperCharacter && containNumber && longEnough;
		return returnValue;
	}

/*
 * Check whether the parameter is valid account.
 * If this account contain any character which isn't a lower case, an upper case or a digital
 * Then this account is not a valid account.
 */
	public static boolean isValidAccount( String account)
	{
		boolean returnValue = true;
		for(int i=0; i<account.length(); i++)
		{
			if (Character.isLowerCase(account.charAt(i))
					|| Character.isUpperCase(account.charAt(i))
					|| Character.isDigit(account.charAt(i))
					)
			{
			}
			else
			{
				returnValue = false;
			}
		}
		return returnValue;
	}

	
	// Get words (which is seperated by space or '\t') at position index.  	
	public static String getWordsAt(String line,int index)
	{
		int begin=-1,end=-1;
		char last=' ';
		for(int i=0;i<line.length();i++)
		{
			if(((line.toCharArray()[i]!=' ') && (line.toCharArray()[i]!='\t') && last==' ')||((line.toCharArray()[i]!=' ') && (line.toCharArray()[i]!='\t') && last=='\t'))
			{
				index--;
				if(index==0)
				{
					begin=i;
					break;
				}
			}
			last=line.toCharArray()[i];
		}
		if(begin==-1) return "null";
		for(int j=begin;j<=line.length();j++)
		{
			if( j==line.length() || line.toCharArray()[j]==' ' || line.toCharArray()[j]=='\t' || line.toCharArray()[j]=='\n')
			{
				end=j;break;
			}
		}
		if(begin!=-1 && end!=-1)
			return line.substring(begin,end);
		else
			return "null";
	}

	// Get words (which is seperated by space or '\t') after substring, the first one if substring exist in many places.  	
	public static String getWordsAfter(String line,String subString)
	{
		String returnValue = new String("");
		if(line.length()==0)
		{
			System.out.println("Input line is null!");
		}
		if(subString.length()==0)
		{
			System.out.println("Substring line is null!");
		}
		if(!line.contains(subString))
		{
			System.out.println("Warning: Input line does not contain substring!");
			System.out.println(line);
			System.out.println(subString);
		}
		if(line.contains(subString))
		{
			int n1 = line.indexOf(subString);
			int n2 = subString.length();
			int n3 = n1 + n2 + 1;
			returnValue = Utility.getWordsAt(line.substring(n3),1);
		}
		return returnValue;
	}

	public static String getWordsBetween(String line,String begin, String end)
	{
		String returnValue = new String("");
		if(line!=null && !line.contains(begin))
			return "";
		if(line!=null && !line.contains(end))
			return "";
		if(line!=null && line.indexOf(end) < line.indexOf(begin))
			return "";
		if(line!=null && line.indexOf(end) > line.indexOf(begin))
		{
			int beginIndex = line.indexOf(begin) + begin.length();
			int endIndex = line.indexOf(end);
			returnValue = line.substring(beginIndex, endIndex);
		}
		return returnValue;
	}
	
	public static int getSubstringNumber(String originalString, String substring)
	{
		int count = 0;
		String os1 = new String(originalString);
		String ss1 = new String(substring);
		String ns1 = os1.replace(ss1, "");
		count = (os1.length() - ns1.length()) / ss1.length();
		// System.out.println(count);
		return count;
	}

	// PE Step one.
	public static String peStep1( String P, String C )
	{
		try{
			Process process = Runtime.getRuntime().exec(C);
			InputStreamReader ir=new InputStreamReader(process.getInputStream());
			BufferedReader br = new BufferedReader(ir);
			String line = br.readLine();
		}catch(IOException e){
			System.err.println("IOException " + e.getMessage());
			return "fail";
		}
		return "success"; 			
	}
	
	// Input string "yyyy-MM-dd HH:mm:ss", return Date.
	public static Date stringToDate (String psDate)
	{
		Date returnDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try{
			returnDate = sdf.parse(psDate);
		}catch(ParseException pe)
		{
			System.err.println("ParseException " + pe.getMessage());
		}
		return returnDate;
	}
	
	// Input Date, return String "yyyy-MM-dd HH:mm:ss".
	public static String dateToString (Date pdDate)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(pdDate);
	}

	// Get random Int from [0,randomP).
	public static int getRandomInt(int randomP)
	{
		Random random = new Random();
		return random.nextInt(randomP);
	}
	
	public static void printStringArray(String pstring[], int pstringLength)
	{
		for(int i=0;i<pstringLength;i++)
		{
			System.out.print(pstring[i] + "\t");
		}
		System.out.println();
	}
	
	public static void main(String args[])
	{
		/*
		String d1 = Utility.getNextNMonthDate(1);
		System.out.println(d1);
		*/
		
		/*
		float resource_amount_accurateF = 0;
		String resource_amount_accurateS = new String();

		resource_amount_accurateF = Float.parseFloat("1") * ((float)(5) / (float)300);
		DecimalFormat fnum = new DecimalFormat("##0.00");
		resource_amount_accurateS = fnum.format(resource_amount_accurateF);
		System.out.println(resource_amount_accurateS);
		*/
		
		/*
		float scale = (float)34.2363345;
		DecimalFormat fnum = new DecimalFormat("##0.00");
		String dd = fnum.format(scale);
		System.out.println(dd);  
		*/
		
		/*
		String s1 = new String();
		if(s1!=null)
			System.out.println("s1 is null!");
		else
			System.out.println("s1 is not null!");
		*/
		
		/*
		String s1 = new String("2022-01-20 22:24:00");
		Date d1 = Utility.stringToDate(s1);
		System.out.println(d1);
		long d1l = d1.getTime();
		System.out.println(d1l);
		
		Date d2 = new Date();
		String s2 = Utility.dateToString(d2);
		System.out.println(s2);
		*/
		
		//Utility.compress("/home/linlei/workdir/ansys12.0/vm240", "vm240.zip", ".");

		/*
		String rootPath = new String("/opt/tomcat/webapps/SanYuan/");
		String users[][] = new String[8][8];
		users = Utility.selectSQL(Initialization.fetch(rootPath+"parameters","dbDriver"), Initialization.fetch(rootPath+"parameters","dbUrl"),Initialization.fetch(rootPath+"parameters","dbUser"), ThreeDES.decryptKeyFromFile(rootPath+"password"), "", "host", "name");
		for(int i=0;i<8;i++)
		{
			for(int j=0;j<8;j++)
				System.out.print(users[i][j]+"\t");
			System.out.print("\n");
		}
		*/
		//HttpServletRequest request = null;
		//Utility.submitToPortal(request);
		//System.out.println("Hello!");
		//System.out.println(System.getProperty("java.class.path"));
		
		/*
		System.out.println("Hello!");
		String tStrDN = new String("CN=脕脰脌脷, T=12345, UN=12345, CN=09876abc ");
		String tStrDN1 = tStrDN.substring(tStrDN.indexOf("T="),tStrDN.length());
		System.out.println(tStrDN1);
		String tStrDN2 = tStrDN1.substring(2, tStrDN1.indexOf(","));
		System.out.println(tStrDN2);
		*/
		//System.out.println(Utility.getNextNMonthDate(0));
		//System.out.println(Utility.isComplexString("b5asdfvcCxz234"));
		
		//Date date = new Date();
		//System.out.println(date.getTime());
		
		
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss");
		String time=sdf.format(new Date((1495678422*1000L)));
		System.out.println(time);
		
		/*
		if(Utility.RunCommand("/opt/tomcat/webapps/SanYuan/expect/MyAuthenticate.sh lsfadmin lsfadmin2"))
			System.out.println("True!");
		else
			System.out.println("False!");
		*/
		
		/*
		String outputDate = Utility.addCalendar("2022-05-29", 5);
		System.out.println(outputDate);
		*/
			
	}
}

