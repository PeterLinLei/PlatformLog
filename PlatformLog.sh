#!/bin/sh
source /opt/tomcat/profile
cd /opt/tomcat/java/PlatformLog/
# P0: jar file; P1: root directory; P2: lsb.stream file path; P3: file pointer; P4: update rusage status interval; P5: sleep interval. 

java -jar /opt/tomcat/java/PlatformLog/PlatformLog.jar /opt/tomcat/java/PlatformLog/ /fs1/apps/lsf/work/HPCC1/logdir/stream/lsb.stream $1 300 30 > /opt/tomcat/java/PlatformLog/PlatformLog.out &

# java -jar /opt/tomcat/java/PlatformLog/PlatformLog.jar /opt/tomcat/java/PlatformLog/ /opt/ibm/lsfsuite/lsf/work/LSFS1/logdir/stream/lsb.stream 0 300 10 > /opt/tomcat/java/PlatformLog/PlatformLog.out &

# java -jar /opt/tomcat/java/PlatformLog/PlatformLog.jar /opt/tomcat/java/PlatformLog/ /opt/tomcat/java/PlatformLog/lsb.stream_20181227-3 $1 300 60 >> /opt/tomcat/java/PlatformLog/PlatformLog.out &

