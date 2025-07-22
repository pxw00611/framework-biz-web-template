package com.alibaba.demo.web;

import com.alibaba.cola.dto.Response;
import com.alibaba.demo.api.VideoStreamServiceI;
import com.alibaba.demo.dto.VideoStreamStartCmd;
import com.alibaba.demo.dto.VideoStreamStopCmd;
import com.alibaba.demo.dto.VideoStreamStatusQry;
import com.alibaba.demo.dto.data.VideoStreamStatusDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 视频流控制器
 * 
 * @author Generated
 */
@RestController
@RequestMapping("/api/video-stream")
public class VideoStreamController {

    @Autowired
    private VideoStreamServiceI videoStreamService;

    /**
     * 开始视频流处理
     * 
     * @param videoStreamStartCmd 视频流启动命令
     * @return 响应结果
     */
    @PostMapping("/start")
    public Response startVideoStream(@Valid @RequestBody VideoStreamStartCmd videoStreamStartCmd) {
        return videoStreamService.startVideoStream(videoStreamStartCmd);
    }

    /**
     * 停止视频流处理
     * 
     * @param videoStreamStopCmd 视频流停止命令
     * @return 响应结果
     */
    @PostMapping("/stop")
    public Response stopVideoStream(@Valid @RequestBody VideoStreamStopCmd videoStreamStopCmd) {
        return videoStreamService.stopVideoStream(videoStreamStopCmd);
    }

    /**
     * 查询视频流状态
     * 
     * @param deviceId 设备ID
     * @return 视频流状态
     */
    @GetMapping("/status/{deviceId}")
    public VideoStreamStatusDTO getVideoStreamStatus(@PathVariable String deviceId) {
        VideoStreamStatusQry qry = new VideoStreamStatusQry();
        qry.setDeviceId(deviceId);
        return videoStreamService.getVideoStreamStatus(qry);
    }
}
