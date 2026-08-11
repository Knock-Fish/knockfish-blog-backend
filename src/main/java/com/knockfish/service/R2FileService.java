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
}
