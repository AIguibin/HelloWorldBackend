# 企业开发常用命令
mvn -gs "D:\Maven\settings-aiguibin.xml" -Dmaven.repo.local="E:\Repository\Local" -T 1C clean package -DskipTests -U -Dmaven.compile.fork=true -Dmaven.test.skip=true && java -jar target/aiguibin-online-table.jar
mvn -gs "D:\Maven\config\settings.xml" -Dmaven.repo.local="E:\Repository\Local" -T 1C clean package -DskipTests -U -Dmaven.compile.fork=true -Dmaven.test.skip=true && java -jar target/aiguibin-online-table.jar
