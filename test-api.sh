#!/bin/bash

# 视频流处理服务API测试脚本

BASE_URL="http://localhost:8080"

echo "=== 视频流处理服务API测试 ==="

# 1. 健康检查
echo "1. 健康检查..."
curl -s "$BASE_URL/actuator/health" | jq '.' || echo "Health check failed"
echo ""

# 2. 启动视频流处理
echo "2. 启动视频流处理..."
cat > /tmp/start_request.json << 'END'
{
  "deviceId": "test-camera-001",
  "streamUrl": "rtsp://wowzaec2demo.streamlock.net/vod-multitrack/_definst_/mp4:BigBuckBunny_115k.mov",
  "frameRate": 5,
  "bucketName": "my-test-bucket",
  "pathPrefix": "test-cameras"
}
END

curl -s -X POST \
  -H "Content-Type: application/json" \
  -d @/tmp/start_request.json \
  "$BASE_URL/api/video-stream/start" | jq '.' || echo "Start request failed"
echo ""

# 3. 查询状态
echo "3. 查询视频流状态..."
sleep 2
curl -s "$BASE_URL/api/video-stream/status/test-camera-001" | jq '.' || echo "Status query failed"
echo ""

# 4. 等待一段时间后再查询
echo "4. 等待10秒后再次查询状态..."
sleep 10
curl -s "$BASE_URL/api/video-stream/status/test-camera-001" | jq '.' || echo "Status query failed"
echo ""

# 5. 停止视频流处理
echo "5. 停止视频流处理..."
cat > /tmp/stop_request.json << 'END'
{
  "deviceId": "test-camera-001"
}
END

curl -s -X POST \
  -H "Content-Type: application/json" \
  -d @/tmp/stop_request.json \
  "$BASE_URL/api/video-stream/stop" | jq '.' || echo "Stop request failed"
echo ""

# 6. 确认停止
echo "6. 确认已停止..."
curl -s "$BASE_URL/api/video-stream/status/test-camera-001" | jq '.' || echo "Status query failed"

echo "=== 测试完成 ==="

# 清理临时文件
rm -f /tmp/start_request.json /tmp/stop_request.json

