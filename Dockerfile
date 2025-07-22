# 多阶段构建
FROM openjdk:17-jdk-slim AS builder

# 设置工作目录
WORKDIR /app

# 复制项目文件
COPY pom.xml .
COPY demo-web-client ./demo-web-client
COPY demo-web-adapter ./demo-web-adapter
COPY demo-web-app ./demo-web-app
COPY demo-web-domain ./demo-web-domain
COPY demo-web-infrastructure ./demo-web-infrastructure
COPY start ./start

# 安装Maven
RUN apt-get update && apt-get install -y maven

# 构建项目
RUN mvn clean package -DskipTests

# 运行阶段
FROM openjdk:17-jdk-slim

# 安装必要的库
RUN apt-get update && \
    apt-get install -y \
    libavcodec-dev \
    libavformat-dev \
    libavutil-dev \
    libswscale-dev \
    && rm -rf /var/lib/apt/lists/*

# 设置工作目录
WORKDIR /app

# 复制构建产物
COPY --from=builder /app/start/target/start-1.0.0-SNAPSHOT.jar app.jar

# 创建日志目录
RUN mkdir -p logs

# 暴露端口
EXPOSE 8080

# 健康检查
HEALTHCHECK --interval=30s --timeout=3s --start-period=5s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 启动应用
ENTRYPOINT ["java", "-Xms512m", "-Xmx2g", "-XX:+UseG1GC", "-jar", "app.jar"]
