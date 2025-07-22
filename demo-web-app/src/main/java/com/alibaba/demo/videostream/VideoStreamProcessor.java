package com.alibaba.demo.videostream;

import com.alibaba.demo.videostream.VideoStreamTask;
import com.alibaba.demo.videostream.gateway.VideoStreamTaskGateway;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 视频流处理器
 * 
 * @author Generated
 */
@Slf4j
@Component
public class VideoStreamProcessor {

    @Autowired
    private VideoStreamTaskGateway videoStreamTaskGateway;

    @Autowired
    private OssUploadService ossUploadService;

    private final ExecutorService executorService = Executors.newCachedThreadPool();
    private final ConcurrentHashMap<String, Future<?>> runningTasks = new ConcurrentHashMap<>();
    private final Java2DFrameConverter converter = new Java2DFrameConverter();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_SSS");

    /**
     * 开始处理视频流
     */
    public void startProcessing(VideoStreamTask task) {
        log.info("Starting video stream processing for device: {}", task.getDeviceId());
        
        Future<?> future = executorService.submit(() -> processVideoStream(task));
        runningTasks.put(task.getDeviceId(), future);
        
        task.start();
        videoStreamTaskGateway.save(task);
    }

    /**
     * 停止处理视频流
     */
    public void stopProcessing(String deviceId) {
        log.info("Stopping video stream processing for device: {}", deviceId);
        
        Future<?> future = runningTasks.remove(deviceId);
        if (future != null) {
            future.cancel(true);
        }

        VideoStreamTask task = videoStreamTaskGateway.findByDeviceId(deviceId);
        if (task != null) {
            task.stop();
            videoStreamTaskGateway.save(task);
        }
    }

    /**
     * 处理视频流的核心方法
     */
    private void processVideoStream(VideoStreamTask task) {
        FFmpegFrameGrabber grabber = null;
        
        try {
            grabber = new FFmpegFrameGrabber(task.getStreamUrl());
            grabber.setFormat("rtsp"); // 根据实际流格式调整
            grabber.start();

            double frameRate = grabber.getFrameRate();
            int skipFrames = (int) Math.max(1, frameRate / task.getFrameRate());
            
            log.info("Video stream info - Frame rate: {}, Skip frames: {}", frameRate, skipFrames);

            Frame frame;
            int frameCount = 0;
            
            while ((frame = grabber.grabFrame()) != null && !Thread.currentThread().isInterrupted()) {
                if (frame.image != null && frameCount % skipFrames == 0) {
                    processFrame(task, frame);
                }
                frameCount++;
            }
            
        } catch (Exception e) {
            log.error("Error processing video stream for device: {}", task.getDeviceId(), e);
            task.setError("视频流处理错误: " + e.getMessage());
            videoStreamTaskGateway.save(task);
        } finally {
            if (grabber != null) {
                try {
                    grabber.stop();
                    grabber.release();
                } catch (Exception e) {
                    log.error("Error releasing grabber for device: {}", task.getDeviceId(), e);
                }
            }
            runningTasks.remove(task.getDeviceId());
        }
    }

    /**
     * 处理单个帧
     */
    private void processFrame(VideoStreamTask task, Frame frame) {
        try {
            // 转换帧为BufferedImage
            BufferedImage bufferedImage = converter.convert(frame);
            if (bufferedImage == null) {
                return;
            }

            // 生成文件名
            String timestamp = LocalDateTime.now().format(formatter);
            String fileName = String.format("frame_%s.jpg", timestamp);
            
            // 生成OSS对象键
            String objectKey = generateObjectKey(task, fileName);

            // 转换为字节数组
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, "jpg", baos);
            byte[] imageBytes = baos.toByteArray();

            // 上传到OSS
            boolean uploaded = ossUploadService.uploadFrame(task.getBucketName(), objectKey, imageBytes);
            
            if (uploaded) {
                task.incrementProcessedFrames();
                task.incrementUploadedFiles();
                log.debug("Successfully uploaded frame for device: {}, file: {}", task.getDeviceId(), objectKey);
            } else {
                log.warn("Failed to upload frame for device: {}, file: {}", task.getDeviceId(), objectKey);
            }

            // 更新任务状态
            videoStreamTaskGateway.save(task);
            
        } catch (IOException e) {
            log.error("Error processing frame for device: {}", task.getDeviceId(), e);
        }
    }

    /**
     * 生成OSS对象键
     */
    private String generateObjectKey(VideoStreamTask task, String fileName) {
        StringBuilder keyBuilder = new StringBuilder();
        
        if (task.getPathPrefix() != null && !task.getPathPrefix().isEmpty()) {
            keyBuilder.append(task.getPathPrefix());
            if (!task.getPathPrefix().endsWith("/")) {
                keyBuilder.append("/");
            }
        }
        
        keyBuilder.append(task.getDeviceId()).append("/");
        
        // 按日期分目录
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        keyBuilder.append(dateDir).append("/");
        
        keyBuilder.append(fileName);
        
        return keyBuilder.toString();
    }
}
