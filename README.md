# 多路摄像头视频流关键帧抽取服务

基于COLA架构的视频流处理服务，支持多路摄像头实时视频流关键帧抽取并自动上传到阿里云OSS。

## 技术栈

- **Spring Boot 3.1.5** - 主框架
- **JavaCV 1.5.12** - 视频处理
- **阿里云OSS SDK** - 对象存储
- **COLA 4.3.1** - 架构框架
- **Maven** - 项目管理

## 主要功能

✅ 多路摄像头并发处理  
✅ 实时视频流关键帧抽取（可配置帧率）  
✅ 自动上传到阿里云OSS  
✅ 按设备ID和日期组织存储目录  
✅ 实时任务状态监控  
✅ RESTful API接口  

## 快速开始

### 1. 环境要求

- Java 17+
- Maven 3.6+
- 阿里云OSS账号和配置

### 2. 配置OSS

编辑 `start/src/main/resources/application.yml`:

```yaml
aliyun:
  oss:
    endpoint: your-oss-endpoint
    accessKeyId: your-access-key-id
    accessKeySecret: your-access-key-secret
```

或使用环境变量：
```bash
export OSS_ENDPOINT=oss-cn-hangzhou.aliyuncs.com
export OSS_ACCESS_KEY_ID=your-access-key-id  
export OSS_ACCESS_KEY_SECRET=your-access-key-secret
```

### 3. 启动服务

```bash
# 方式1：使用启动脚本
./start.sh

# 方式2：手动启动
mvn clean package -DskipTests
java -jar start/target/start-1.0.0-SNAPSHOT.jar
```

### 4. 测试API

```bash
# 使用测试脚本
./test-api.sh

# 或手动测试
curl -X POST http://localhost:8080/api/video-stream/start \
  -H "Content-Type: application/json" \
  -d '{
    "deviceId": "camera001",
    "streamUrl": "rtsp://your-camera-url",
    "frameRate": 10,
    "bucketName": "your-bucket",
    "pathPrefix": "cameras"
  }'
```

## API文档

详细API文档请参考：[VIDEO_STREAM_API.md](./VIDEO_STREAM_API.md)

### 核心接口

- `POST /api/video-stream/start` - 启动视频流处理
- `POST /api/video-stream/stop` - 停止视频流处理  
- `GET /api/video-stream/status/{deviceId}` - 查询处理状态

## 存储结构

关键帧按以下目录结构存储到OSS：

```
{pathPrefix}/{deviceId}/{yyyy}/{MM}/{dd}/frame_{timestamp}.jpg
```

示例：
```
cameras/
├── device001/
│   └── 2024/01/15/
│       ├── frame_20240115_143052_123.jpg
│       └── frame_20240115_143053_456.jpg
└── device002/
    └── 2024/01/15/
        └── frame_20240115_143054_789.jpg
```

## 架构设计

基于COLA架构设计，模块清晰分离：

```
├── demo-web-client      # 接口定义层
├── demo-web-adapter     # 适配器层 (REST API)
├── demo-web-app         # 应用服务层
├── demo-web-domain      # 领域模型层
├── demo-web-infrastructure  # 基础设施层
└── start               # 启动模块
```

## 监控运维

- 健康检查：`GET /actuator/health`
- 应用指标：`GET /actuator/metrics`
- 日志文件：`logs/demo-web.log`

## 生产部署

### Docker部署

```dockerfile
FROM openjdk:17-jdk-slim
COPY start/target/start-1.0.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### 性能调优

推荐JVM参数：
```bash
-Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200
```

## 开发说明

### 新增摄像头处理

1. 调用启动API配置新的设备ID和视频流URL
2. 系统自动创建对应的OSS存储目录
3. 开始实时抽取和上传关键帧

### 扩展功能

- 支持更多视频格式（修改VideoStreamProcessor）
- 增加图像处理算法（修改processFrame方法）
- 添加数据库持久化（实现VideoStreamTaskGateway）

## 故障排查

常见问题及解决方案请参考：[VIDEO_STREAM_API.md](./VIDEO_STREAM_API.md#故障排查)

## 原始COLA框架文档

COLA框架相关文档请参考：[README_COLA.md](./README_COLA.md)

## 许可证

MIT License
