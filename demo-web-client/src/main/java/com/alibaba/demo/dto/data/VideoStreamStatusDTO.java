package com.alibaba.demo.dto.data;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 视频流状态DTO
 * 
 * @author Generated
 */
@Data
public class VideoStreamStatusDTO {

    /**
     * 设备ID
     */
    private String deviceId;

    /**
     * 视频流URL
     */
    private String streamUrl;

    /**
     * 运行状态 RUNNING, STOPPED, ERROR
     */
    private String status;

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
     * OSS存储桶名称
     */
    private String bucketName;

    /**
     * OSS存储路径前缀
     */
    private String pathPrefix;
}
