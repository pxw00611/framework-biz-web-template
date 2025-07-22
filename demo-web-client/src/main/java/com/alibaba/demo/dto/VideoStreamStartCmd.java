package com.alibaba.demo.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;

/**
 * 视频流启动命令
 * 
 * @author Generated
 */
@Data
public class VideoStreamStartCmd {

    /**
     * 设备ID
     */
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;

    /**
     * 视频流URL
     */
    @NotBlank(message = "视频流URL不能为空")
    private String streamUrl;

    /**
     * 帧率 (每秒抽取的帧数，默认10帧)
     */
    @NotNull(message = "帧率不能为空")
    @Min(value = 1, message = "帧率必须大于0")
    private Integer frameRate = 10;

    /**
     * OSS存储桶名称
     */
    @NotBlank(message = "OSS存储桶名称不能为空")
    private String bucketName;

    /**
     * OSS存储路径前缀
     */
    private String pathPrefix;
}
