#!/bin/bash
# 通用重启脚本：杀掉旧进程并以生产参数启动 jar
set -euo pipefail

APP_JAR_NAME="aiguibin-platform-arch"
APP_JAR_PATH="$(cd "$(dirname "$0")" && pwd)"

JAVA_BIN="${JAVA_BIN:-java}"
JAVA_OPTS="${JAVA_OPTS:--Dfile.encoding=UTF-8 -Xms1G -Xmx2G -Duser.timezone=Asia/Shanghai}"

IDS=$(ps -ef | grep java | grep -v grep | grep "${APP_JAR_NAME}" | awk '{print $2}')
if [ -z "$IDS" ]; then
  echo "No running process found for ${APP_JAR_NAME}."
else
  for PID in $IDS; do
    kill -9 "$PID"
    echo "Stopped process PID=${PID}"
  done
fi

cd "${APP_JAR_PATH}"
nohup "${JAVA_BIN}" ${JAVA_OPTS} -jar "${APP_JAR_NAME}.jar" >nohup.out 2>&1 &
sleep 3
NEW_PID=$(ps -ef | grep "${APP_JAR_NAME}.jar" | grep -v grep | awk '{print $2}')
echo "Started. PID=${NEW_PID}"
echo "Log: tail -f ${APP_JAR_PATH}/nohup.out"
