  
##################################################

SourcePath=/arm/cluster/shenzhen/work/shenzhen/logdir/stream
DestinationPath=/data/backup/stream
ST=3600

##################################################

while [ 1 ]; do

##################################################

Files=$(ls $SourcePath | grep lsb.stream. )
for FileName in $Files
do
  echo $FileName
  if [ ! -f "$DestinationPath/$FileName" ]; then
    echo "File $DestinationPath/$FileName does not exist! Copy file to destination directory."
    cp $SourcePath/$FileName $DestinationPath
  fi 
done

##################################################

if [ ! -f "$PD/STOP" ]; then
	sleep $ST
else
	exit
fi

##################################################

done
