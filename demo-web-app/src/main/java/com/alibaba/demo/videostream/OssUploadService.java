package com.alibaba.demo.videostream;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;

/**
 * OSS上传服务
 * 
 * @author Generated
 */
@Slf4j
@Service
public class OssUploadService {

    @Value("${aliyun.oss.endpoint:}")
    private String endpoint;

    @Value("${aliyun.oss.accessKeyId:}")
    private String accessKeyId;

    @Value("${aliyun.oss.accessKeySecret:}")
    private String accessKeySecret;

    /**
     * 上传帧图片到OSS
     */
    public boolean uploadFrame(String bucketName, String objectKey, byte[] imageBytes) {
        if (endpoint == null || endpoint.isEmpty() || 
            accessKeyId == null || accessKeyId.isEmpty() ||
            accessKeySecret == null || accessKeySecret.isEmpty()) {
            log.warn("OSS configuration is missing, skipping upload. Object key: {}", objectKey);
            return false;
        }

        OSS ossClient = null;
        try {
            // 创建OSS客户端
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

            // 设置对象元数据
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(imageBytes.length);
            metadata.setContentType("image/jpeg");
            metadata.setCacheControl("no-cache");

            // 上传文件
            ossClient.putObject(bucketName, objectKey, new ByteArrayInputStream(imageBytes), metadata);
            
            log.debug("Successfully uploaded to OSS: bucket={}, key={}, size={}", bucketName, objectKey, imageBytes.length);
            return true;
            
        } catch (Exception e) {
            log.error("Failed to upload to OSS: bucket={}, key={}", bucketName, objectKey, e);
            return false;
        } finally {
            if (ossClient != null) {
                try {
                    ossClient.shutdown();
                } catch (Exception e) {
                    log.error("Error shutting down OSS client", e);
                }
            }
        }
    }

    /**
     * 检查存储桶是否存在
     */
    public boolean bucketExists(String bucketName) {
        if (endpoint == null || endpoint.isEmpty() || 
            accessKeyId == null || accessKeyId.isEmpty() ||
            accessKeySecret == null || accessKeySecret.isEmpty()) {
            log.warn("OSS configuration is missing");
            return false;
        }

        OSS ossClient = null;
        try {
            ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
            return ossClient.doesBucketExist(bucketName);
        } catch (Exception e) {
            log.error("Error checking bucket existence: {}", bucketName, e);
            return false;
        } finally {
            if (ossClient != null) {
                try {
                    ossClient.shutdown();
                } catch (Exception e) {
                    log.error("Error shutting down OSS client", e);
                }
            }
        }
    }
}
