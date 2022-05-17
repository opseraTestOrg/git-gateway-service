#!/bin/bash
nohup java -jar -Xms128m -Xmx256m -Dspring.profiles.active=$1 /apps/OpsERA/components/git-gateway-service/live/git-gateway-service-$2.jar &
echo $! > ././pid.file