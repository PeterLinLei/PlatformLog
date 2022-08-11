source /opt/tomcat/profile
 
FP=0
if [ -f "/opt/tomcat/java/PlatformLog/PlatformLog.out" ];then
echo "File PlatformLog.out exists!"
FP=` cat /opt/tomcat/java/PlatformLog/PlatformLog.out | awk 'END {print}' | awk '{print $NF}' `
else
echo "File PlatformLog.out does not exist!"
FP=0
fi

echo $FP

/opt/tomcat/java/PlatformLog/Stop.sh
/opt/tomcat/java/PlatformLog/PlatformLog.sh $FP

