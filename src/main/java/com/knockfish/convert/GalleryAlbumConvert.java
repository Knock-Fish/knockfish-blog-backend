package com.knockfish.convert;

import com.knockfish.dto.gallery.GalleryAlbumDTO;
import com.knockfish.entity.GalleryAlbum;
import com.knockfish.vo.gallery.GalleryAlbumVO;
import org.mapstruct.Mapper;

/**
 * 相册转换器。coverUrl / photoCount / locked 由 SQL 或 Service 填，不走映射。
 */
@Mapper(componentModel = "spring")
public interface GalleryAlbumConvert {
    GalleryAlbumVO toVO(GalleryAlbum entity);

    GalleryAlbum toEntity(GalleryAlbumDTO dto);
}
