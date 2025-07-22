package com.alibaba.demo.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 视频流停止命令
 * 
 * @author Generated
 */
@Data
public class VideoStreamStopCmd {

    /**
     * 设备ID
     */
    @NotBlank(message = "设备ID不能为空")
    private String deviceId;
}
