package com.alibaba.demo.videostream;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 视频流任务领域对象
 * 
 * @author Generated
 */
@Data
public class VideoStreamTask {

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 视频流URL
     */
    private String streamUrl;

    /**
     * 帧率
     */
    private Integer frameRate;

    /**
     * OSS存储桶名称
     */
    private String bucketName;

    /**
     * OSS存储路径前缀
     */
    private String pathPrefix;

    /**
     * 任务状态
     */
    private TaskStatus status;

    /**
     * 已处理帧数
     */
    private Long processedFrames;

    /**
     * 已上传文件数
     */
    private Long uploadedFiles;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 最后活跃时间
     */
    private LocalDateTime lastActiveTime;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 构造器
     */
    public VideoStreamTask(String deviceId, String streamUrl, Integer frameRate, String bucketName, String pathPrefix) {
        this.deviceId = deviceId;
        this.streamUrl = streamUrl;
        this.frameRate = frameRate;
        this.bucketName = bucketName;
        this.pathPrefix = pathPrefix;
        this.status = TaskStatus.INITIALIZED;
        this.processedFrames = 0L;
        this.uploadedFiles = 0L;
        this.startTime = LocalDateTime.now();
        this.lastActiveTime = LocalDateTime.now();
    }

    /**
     * 启动任务
     */
    public void start() {
        this.status = TaskStatus.RUNNING;
        this.startTime = LocalDateTime.now();
        this.lastActiveTime = LocalDateTime.now();
    }

    /**
     * 停止任务
     */
    public void stop() {
        this.status = TaskStatus.STOPPED;
        this.lastActiveTime = LocalDateTime.now();
    }

    /**
     * 设置错误状态
     */
    public void setError(String errorMessage) {
        this.status = TaskStatus.ERROR;
        this.errorMessage = errorMessage;
        this.lastActiveTime = LocalDateTime.now();
    }

    /**
     * 增加处理帧数
     */
    public void incrementProcessedFrames() {
        this.processedFrames++;
        this.lastActiveTime = LocalDateTime.now();
    }

    /**
     * 增加上传文件数
     */
    public void incrementUploadedFiles() {
        this.uploadedFiles++;
        this.lastActiveTime = LocalDateTime.now();
    }

    /**
     * 任务状态枚举
     */
    public enum TaskStatus {
        INITIALIZED, RUNNING, STOPPED, ERROR
    }
}
