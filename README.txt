程序编译打包步骤 
#######################################################################
*使用MyEclipse ant build生成jar包
	右键build.xml,点击Run As，2 Ant Build。
*创建PlatformLog.bat
	java -jar "C:\PlatformLog\PlatformLog.jar"  C:\PlatformLog  C:\PlatformLog\lsb.stream  0 10 >> "C:\\PlatformLog\\PlatformLog.out"
*创建PlatformLog.vbs
Set ws = CreateObject("Wscript.Shell")
ws.run "cmd /c C:\PlatformLog\PlatformLog.bat", vbhide	
*创建PlatformLog.sh
	#!/bin/sh

	### PATH, CLASSPATH, LD_LIBRARY_PATH for Java, JSP, MySQL.
	export JAVA_HOME=/usr/java/latest
	export TOMCAT_HOME=/opt/tomcat
	export CLASSPATH=.:$CLASSPATH
	export CLASSPATH=$TOMCAT_HOME/lib/servlet-api.jar:$CLASSPATH
	export PATH=$JAVA_HOME/bin:$PATH

	cd /opt/tomcat/java/PlatformLog/
	java -jar /opt/tomcat/java/PlatformLog/PlatformLog.jar /opt/tomcat/java/PlatformLog/ /opt/lsf913/work/pcluster/logdir/stream/lsb.stream 0 10 > /opt/tomcat/java/PlatformLog/PlatformLog.out &
chmod +x PlatformLog.sh

