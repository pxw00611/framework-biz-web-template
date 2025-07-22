#!/bin/bash

# 视频流处理服务启动脚本

echo "Starting Video Stream Processing Service..."

# 检查Java环境
if ! command -v java &> /dev/null; then
    echo "Error: Java is not installed or not in PATH"
    exit 1
fi

# 检查Java版本
JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
if [[ $JAVA_VERSION < "17" ]]; then
    echo "Error: Java 17 or higher is required, current version: $JAVA_VERSION"
    exit 1
fi

# 设置环境变量（如果需要）
export JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC"

# 构建项目
echo "Building project..."
mvn clean package -DskipTests

if [ $? -ne 0 ]; then
    echo "Error: Build failed"
    exit 1
fi

# 启动应用
echo "Starting application..."
cd start
java $JAVA_OPTS -jar target/start-1.0.0-SNAPSHOT.jar

