# 视频流处理服务 API 文档

## 概述

本服务基于Spring Boot 3.1.5、JavaCV 1.5.12和阿里云OSS，实现多路摄像头实时视频流关键帧抽取和存储功能。

## 主要功能

- 支持多路摄像头并发处理
- 实时视频流关键帧抽取（可配置帧率，默认每秒10帧）
- 自动上传关键帧到阿里云OSS
- 按设备ID和日期自动组织存储目录结构
- 实时任务状态监控

## 目录结构

OSS存储目录结构：
```
{pathPrefix}/{deviceId}/{yyyy}/{MM}/{dd}/frame_{timestamp}.jpg
```

示例：
```
cameras/device001/2024/01/15/frame_20240115_143052_123.jpg
cameras/device002/2024/01/15/frame_20240115_143053_456.jpg
```

## API 接口

### 1. 启动视频流处理

**POST** `/api/video-stream/start`

**请求体:**
```json
{
  "deviceId": "device001",
  "streamUrl": "rtsp://192.168.1.100:554/stream",
  "frameRate": 10,
  "bucketName": "my-video-bucket",
  "pathPrefix": "cameras"
}
```

**参数说明:**
- `deviceId`: 设备唯一标识符（必填）
- `streamUrl`: 视频流URL，支持RTSP、HTTP等协议（必填）
- `frameRate`: 每秒抽取帧数，默认10帧（必填，最小值1）
- `bucketName`: OSS存储桶名称（必填）
- `pathPrefix`: OSS存储路径前缀（可选）

**响应:**
```json
{
  "success": true,
  "errCode": null,
  "errMessage": null
}
```

### 2. 停止视频流处理

**POST** `/api/video-stream/stop`

**请求体:**
```json
{
  "deviceId": "device001"
}
```

**响应:**
```json
{
  "success": true,
  "errCode": null,
  "errMessage": null
}
```

### 3. 查询视频流状态

**GET** `/api/video-stream/status/{deviceId}`

**响应:**
```json
{
  "deviceId": "device001",
  "streamUrl": "rtsp://192.168.1.100:554/stream",
  "status": "RUNNING",
  "processedFrames": 1520,
  "uploadedFiles": 1520,
  "startTime": "2024-01-15T14:30:45",
  "lastActiveTime": "2024-01-15T14:33:12",
  "errorMessage": null,
  "bucketName": "my-video-bucket",
  "pathPrefix": "cameras"
}
```

**状态说明:**
- `INITIALIZED`: 已初始化
- `RUNNING`: 运行中
- `STOPPED`: 已停止
- `ERROR`: 错误状态
- `NOT_FOUND`: 任务不存在

## 配置说明

### 应用配置 (application.yml)

```yaml
# 阿里云OSS配置
aliyun:
  oss:
    endpoint: oss-cn-hangzhou.aliyuncs.com
    accessKeyId: your-access-key-id
    accessKeySecret: your-access-key-secret
```

### 环境变量配置

生产环境建议使用环境变量：
```bash
export OSS_ENDPOINT=oss-cn-hangzhou.aliyuncs.com
export OSS_ACCESS_KEY_ID=your-access-key-id
export OSS_ACCESS_KEY_SECRET=your-access-key-secret
```

## 部署说明

### 系统要求

- Java 17+
- Maven 3.6+
- 内存: 最少2GB，推荐4GB+
- 网络: 稳定的网络连接，支持访问摄像头和阿里云OSS

### 快速启动

1. 克隆项目并配置OSS参数
2. 执行启动脚本：
   ```bash
   ./start.sh
   ```

### Docker部署

```dockerfile
FROM openjdk:17-jdk-slim
COPY start/target/start-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 监控和运维

### 健康检查

**GET** `/actuator/health`

### 应用指标

**GET** `/actuator/metrics`

### 日志

日志文件位置: `logs/demo-web.log`

日志级别配置:
- 开发环境: DEBUG
- 生产环境: INFO

## 错误处理

### 常见错误码

- `DEVICE_ALREADY_RUNNING`: 设备已在处理中
- `DEVICE_NOT_FOUND`: 设备任务不存在
- `START_FAILED`: 启动失败
- `STOP_FAILED`: 停止失败

### 故障排查

1. **视频流连接失败**
   - 检查streamUrl是否正确
   - 确认网络连接
   - 验证摄像头认证信息

2. **OSS上传失败**
   - 检查OSS配置参数
   - 验证访问权限
   - 确认存储桶存在

3. **内存不足**
   - 调整JVM堆内存设置
   - 减少并发处理的摄像头数量
   - 降低帧率设置

## 性能优化

### 建议配置

- **并发摄像头数量**: 建议不超过CPU核心数
- **帧率设置**: 根据业务需求和网络带宽调整
- **JVM参数**: 
  ```bash
  -Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200
  ```

### 监控指标

- 处理帧数/秒
- 上传成功率
- 内存使用率
- 网络带宽使用

## 技术架构

基于COLA架构设计：
- **adapter**: REST API适配层
- **app**: 应用服务层
- **domain**: 领域模型层
- **infrastructure**: 基础设施层
- **client**: 接口定义层

主要技术栈：
- Spring Boot 3.1.5
- JavaCV 1.5.12 (FFmpeg)
- 阿里云OSS SDK
- Maven多模块构建
