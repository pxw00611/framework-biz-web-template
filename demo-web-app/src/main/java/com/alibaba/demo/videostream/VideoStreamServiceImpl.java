package com.alibaba.demo.videostream;

import com.alibaba.cola.dto.Response;
import com.alibaba.demo.api.VideoStreamServiceI;
import com.alibaba.demo.dto.VideoStreamStartCmd;
import com.alibaba.demo.dto.VideoStreamStopCmd;
import com.alibaba.demo.dto.VideoStreamStatusQry;
import com.alibaba.demo.dto.data.VideoStreamStatusDTO;
import com.alibaba.demo.videostream.VideoStreamTask;
import com.alibaba.demo.videostream.gateway.VideoStreamTaskGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 视频流服务实现
 * 
 * @author Generated
 */
@Slf4j
@Service
public class VideoStreamServiceImpl implements VideoStreamServiceI {

    @Autowired
    private VideoStreamTaskGateway videoStreamTaskGateway;

    @Autowired
    private VideoStreamProcessor videoStreamProcessor;

    @Override
    public Response startVideoStream(VideoStreamStartCmd videoStreamStartCmd) {
        log.info("Starting video stream for device: {}", videoStreamStartCmd.getDeviceId());
        
        try {
            // 检查设备是否已经在处理中
            if (videoStreamTaskGateway.existsByDeviceId(videoStreamStartCmd.getDeviceId())) {
                return Response.buildFailure("DEVICE_ALREADY_RUNNING", "设备已在处理中");
            }

            // 创建视频流任务
            VideoStreamTask task = new VideoStreamTask(
                videoStreamStartCmd.getDeviceId(),
                videoStreamStartCmd.getStreamUrl(),
                videoStreamStartCmd.getFrameRate(),
                videoStreamStartCmd.getBucketName(),
                videoStreamStartCmd.getPathPrefix()
            );

            // 保存任务
            videoStreamTaskGateway.save(task);

            // 启动视频流处理
            videoStreamProcessor.startProcessing(task);

            return Response.buildSuccess();
        } catch (Exception e) {
            log.error("Failed to start video stream for device: {}", videoStreamStartCmd.getDeviceId(), e);
            return Response.buildFailure("START_FAILED", "启动视频流处理失败: " + e.getMessage());
        }
    }

    @Override
    public Response stopVideoStream(VideoStreamStopCmd videoStreamStopCmd) {
        log.info("Stopping video stream for device: {}", videoStreamStopCmd.getDeviceId());
        
        try {
            // 检查任务是否存在
            if (!videoStreamTaskGateway.existsByDeviceId(videoStreamStopCmd.getDeviceId())) {
                return Response.buildFailure("DEVICE_NOT_FOUND", "设备任务不存在");
            }

            // 停止视频流处理
            videoStreamProcessor.stopProcessing(videoStreamStopCmd.getDeviceId());

            // 删除任务
            videoStreamTaskGateway.deleteByDeviceId(videoStreamStopCmd.getDeviceId());

            return Response.buildSuccess();
        } catch (Exception e) {
            log.error("Failed to stop video stream for device: {}", videoStreamStopCmd.getDeviceId(), e);
            return Response.buildFailure("STOP_FAILED", "停止视频流处理失败: " + e.getMessage());
        }
    }

    @Override
    public VideoStreamStatusDTO getVideoStreamStatus(VideoStreamStatusQry videoStreamStatusQry) {
        log.info("Getting video stream status for device: {}", videoStreamStatusQry.getDeviceId());
        
        VideoStreamTask task = videoStreamTaskGateway.findByDeviceId(videoStreamStatusQry.getDeviceId());
        if (task == null) {
            VideoStreamStatusDTO dto = new VideoStreamStatusDTO();
            dto.setDeviceId(videoStreamStatusQry.getDeviceId());
            dto.setStatus("NOT_FOUND");
            return dto;
        }

        // 转换为DTO
        VideoStreamStatusDTO dto = new VideoStreamStatusDTO();
        dto.setDeviceId(task.getDeviceId());
        dto.setStreamUrl(task.getStreamUrl());
        dto.setStatus(task.getStatus().name());
        dto.setProcessedFrames(task.getProcessedFrames());
        dto.setUploadedFiles(task.getUploadedFiles());
        dto.setStartTime(task.getStartTime());
        dto.setLastActiveTime(task.getLastActiveTime());
        dto.setErrorMessage(task.getErrorMessage());
        dto.setBucketName(task.getBucketName());
        dto.setPathPrefix(task.getPathPrefix());

        return dto;
    }
}
