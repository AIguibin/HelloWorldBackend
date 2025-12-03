#!/bin/bash

JAVA_HOME=/usr/java/jdk1.8.0_212/bin/java

APP_JAR_NAME="aiguibin-online-table"
APP_JAR_PATH=/home/yw/aiguibin-online-table

JAVA_OPTS=" -Dfile.encoding=UTF-8 -Xms2G -Xmx2G -Dlog4j2.asyncQueueFullPolicy=Discard -Dlog4j2.discardThreshold=ERROR"
JAVA_OPTS="${JAVA_OPTS} -Dspring.cloud.nacos.discovery.enabled=true"
JAVA_OPTS="${JAVA_OPTS} -Dspring.cloud.nacos.discovery.server-addr=10.12.170.201:8848"
JAVA_OPTS="${JAVA_OPTS} -Dspring.cloud.nacos.discovery.namespace=ecms_dev"
JAVA_OPTS="${JAVA_OPTS} -Dspring.cloud.nacos.discovery.metadata.deployTime=$(date +%Y-%m-%d/%T)"
JAVA_OPTS="${JAVA_OPTS} -Dspring.cloud.nacos.config.enabled=true"
JAVA_OPTS="${JAVA_OPTS} -Dspring.cloud.nacos.config.server-addr=10.12.170.201:8848"
JAVA_OPTS="${JAVA_OPTS} -Dspring.cloud.nacos.config.namespace=ecms_dev"
JAVA_OPTS="${JAVA_OPTS} -Dspring.main.allow-bean-definition-overriding=true"
JAVA_OPTS="${JAVA_OPTS} -Dspring.main.allow-circular-references=true"

IDS=$(ps -ef | grep java | grep -v grep | grep ${APP_JAR_NAME} | awk '{print $2}')
if [ -z "$IDS" ]; then
	echo "No processes found for $APP_JAR_NAME !"
else
	for PID in $IDS
	do
		kill -9  $PID
		echo "stop processes with PID $PID"
	done
fi

echo -e "------------------------------app  str ------------------------------------------------------------"
cd $APP_JAR_PATH
nohup ${JAVA_HOME} ${JAVA_OPTS} -jar ${APP_JAR_NAME}.jar >nohup.out 2>&1 &
sleep 3
ID=$(ps -ef |grep ${APP_JAR_NAME} | grep -v grep | awk '{print $2}')
echo -e "** start success, CODE: $? PID: $ID"
echo -e "** HOST_IP: `hostname -I | awk '{print $1}'`"
echo -e "** APP_USR: `whoami`"
echo -e "** JAVA_OPTS: ${JAVA_OPTS}"
echo -e "** APP_LOG: tail -f ${APP_JAR_PATH}/nohup.out"
echo -e "------------------------------app  end ------------------------------------------------------------"
