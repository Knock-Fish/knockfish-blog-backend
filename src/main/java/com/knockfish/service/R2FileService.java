package com.knockfish.service;

import com.knockfish.dto.r2_file.R2FileUploadDTO;
import com.knockfish.vo.r2_file.R2FileInfoVO;
import com.knockfish.vo.r2_file.R2FileListVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface R2FileService {

    Map<String, Object> uploadR2File(R2FileUploadDTO uploadDTO);

    List<String> getAllR2Files();

    List<R2FileListVO> getAllR2FilesByPrefix(String prefix);

    String getR2FileUrl(String key);

    R2FileInfoVO getR2FileInfo(String key);

    boolean deleteR2File(String key);

    boolean batchDeleteR2File(List<String> keys);

    /**
     * 按指定 key 上传字节流（图库等需要自定义 key 与派生图的场景用）。
     * 不做任何转码，调用方决定 contentType 与内容。
     *
     * @param key         对象 key
     * @param bytes       文件字节
     * @param contentType MIME 类型
     */
    void putObject(String key, byte[] bytes, String contentType);

    /** 用 CDN 域名拼出可访问 URL */
    String buildUrl(String key);
}
