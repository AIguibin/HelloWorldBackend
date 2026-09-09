#!/bin/bash
# 本地构建并运行常用命令（Windows Git Bash）
set -euo pipefail

# 定位项目根目录（scripts/ 的上一级），任意位置执行均可
PROJECT_ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "${PROJECT_ROOT}"

# 后端（Maven Wrapper，自动下载 3.9.11；需 JDK 21，最低 17）
# JAVA_HOME=/d/Java/jdk21.0.12.1 scripts/start.sh
./mvnw -gs "D:/Maven/settings-aiguibin.xml" -Dmaven.repo.local="E:/Repository/Local" clean package -DskipTests
java -jar target/aiguibin-platform-arch.jar
