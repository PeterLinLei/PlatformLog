  
kill -9 `ps -ef | grep PlatformLog | grep -v grep | awk '{print $2}'`

