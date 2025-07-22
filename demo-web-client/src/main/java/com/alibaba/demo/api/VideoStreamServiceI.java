package com.alibaba.demo.api;

import com.alibaba.cola.dto.Response;
import com.alibaba.demo.dto.VideoStreamStartCmd;
import com.alibaba.demo.dto.VideoStreamStopCmd;
import com.alibaba.demo.dto.VideoStreamStatusQry;
import com.alibaba.demo.dto.data.VideoStreamStatusDTO;

/**
 * 视频流服务接口
 * 
 * @author Generated
 */
public interface VideoStreamServiceI {

    /**
     * 开始视频流处理
     * 
     * @param videoStreamStartCmd 视频流启动命令
     * @return 响应结果
     */
    Response startVideoStream(VideoStreamStartCmd videoStreamStartCmd);

    /**
     * 停止视频流处理
     * 
     * @param videoStreamStopCmd 视频流停止命令
     * @return 响应结果
     */
    Response stopVideoStream(VideoStreamStopCmd videoStreamStopCmd);

    /**
     * 查询视频流状态
     * 
     * @param videoStreamStatusQry 视频流状态查询
     * @return 视频流状态
     */
    VideoStreamStatusDTO getVideoStreamStatus(VideoStreamStatusQry videoStreamStatusQry);
}
