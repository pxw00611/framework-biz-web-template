#!/bin/bash

# 视频流处理服务部署脚本

set -e

echo "=== 视频流处理服务部署脚本 ==="

# 检查Docker环境
if ! command -v docker &> /dev/null; then
    echo "Error: Docker is not installed"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "Error: Docker Compose is not installed"
    exit 1
fi

# 检查环境变量
if [ -z "$OSS_ACCESS_KEY_ID" ] || [ -z "$OSS_ACCESS_KEY_SECRET" ]; then
    echo "Warning: OSS credentials not set in environment variables"
    echo "Please set OSS_ACCESS_KEY_ID and OSS_ACCESS_KEY_SECRET"
    echo "Example:"
    echo "  export OSS_ACCESS_KEY_ID=your-access-key-id"
    echo "  export OSS_ACCESS_KEY_SECRET=your-access-key-secret"
    echo "  export OSS_ENDPOINT=oss-cn-hangzhou.aliyuncs.com"
    echo ""
    read -p "Continue anyway? (y/N): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        exit 1
    fi
fi

# 创建日志目录
mkdir -p logs

echo "Building and starting services..."

# 构建并启动服务
docker-compose down --remove-orphans
docker-compose build --no-cache
docker-compose up -d

echo ""
echo "=== 部署完成 ==="
echo ""
echo "服务地址: http://localhost:8080"
echo "健康检查: http://localhost:8080/actuator/health"
echo "API文档: 详见 VIDEO_STREAM_API.md"
echo ""
echo "查看日志: docker-compose logs -f"
echo "停止服务: docker-compose down"
echo ""

# 等待服务启动
echo "等待服务启动..."
for i in {1..30}; do
    if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
        echo "✅ 服务启动成功!"
        break
    fi
    echo "⏳ 等待中... ($i/30)"
    sleep 2
done

if [ $i -eq 30 ]; then
    echo "❌ 服务启动超时，请检查日志"
    echo "查看日志: docker-compose logs"
    exit 1
fi

