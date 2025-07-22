package com.alibaba.demo.videostream.gateway;

import com.alibaba.demo.videostream.VideoStreamTask;

/**
 * 视频流任务网关接口
 * 
 * @author Generated
 */
public interface VideoStreamTaskGateway {

    /**
     * 保存任务
     */
    void save(VideoStreamTask task);

    /**
     * 根据设备ID查找任务
     */
    VideoStreamTask findByDeviceId(String deviceId);

    /**
     * 根据设备ID删除任务
     */
    void deleteByDeviceId(String deviceId);

    /**
     * 检查任务是否存在
     */
    boolean existsByDeviceId(String deviceId);
}
