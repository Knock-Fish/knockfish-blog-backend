package com.knockfish.convert;

import com.knockfish.entity.GalleryPhoto;
import com.knockfish.vo.gallery.GalleryPhotoVO;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * 图库照片转换器。
 * 三个 URL 字段（thumbUrl / previewUrl / originalUrl）不在 Mapper 里生成，
 * 由 Service 拼 cdnDomain，避免换域名时改映射。
 */
@Mapper(componentModel = "spring")
public interface GalleryPhotoConvert {
    GalleryPhotoVO toVO(GalleryPhoto entity);

    List<GalleryPhotoVO> toVOList(List<GalleryPhoto> entities);
}
