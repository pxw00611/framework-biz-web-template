package com.alibaba.demo.videostream;

import com.alibaba.demo.videostream.VideoStreamTask;
import com.alibaba.demo.videostream.gateway.VideoStreamTaskGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 视频流任务网关实现 - 内存存储
 * 
 * @author Generated
 */
@Slf4j
@Component
public class VideoStreamTaskGatewayImpl implements VideoStreamTaskGateway {

    private final ConcurrentHashMap<String, VideoStreamTask> taskStore = new ConcurrentHashMap<>();

    @Override
    public void save(VideoStreamTask task) {
        log.debug("Saving video stream task for device: {}", task.getDeviceId());
        taskStore.put(task.getDeviceId(), task);
    }

    @Override
    public VideoStreamTask findByDeviceId(String deviceId) {
        log.debug("Finding video stream task for device: {}", deviceId);
        return taskStore.get(deviceId);
    }

    @Override
    public void deleteByDeviceId(String deviceId) {
        log.debug("Deleting video stream task for device: {}", deviceId);
        taskStore.remove(deviceId);
    }

    @Override
    public boolean existsByDeviceId(String deviceId) {
        log.debug("Checking if video stream task exists for device: {}", deviceId);
        return taskStore.containsKey(deviceId);
    }
}
