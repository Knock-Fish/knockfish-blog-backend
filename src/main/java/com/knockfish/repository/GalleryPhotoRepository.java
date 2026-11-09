package com.knockfish.repository;

import com.knockfish.dto.gallery.GalleryPhotoQueryDTO;
import com.knockfish.entity.GalleryPhoto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GalleryPhotoRepository {
    int insert(GalleryPhoto photo);

    GalleryPhoto selectById(@Param("photoId") Long photoId);

    List<GalleryPhoto> selectPage(@Param("query") GalleryPhotoQueryDTO query);

    int updateMeta(@Param("photoId") Long photoId,
                   @Param("title") String title,
                   @Param("albumId") Long albumId,
                   @Param("nsfw") Integer nsfw);

    /** 归入相册；albumId 传 null 表示移出相册（与 updateMeta 不同，这里允许把列置空） */
    int updateAlbum(@Param("photoId") Long photoId, @Param("albumId") Long albumId);

    /** 批量归册；albumId 传 null 表示全部移出相册 */
    int updateAlbumBatch(@Param("photoIds") List<Long> photoIds, @Param("albumId") Long albumId);

    int updateStatus(@Param("photoId") Long photoId, @Param("status") Integer status);

    int deleteById(@Param("photoId") Long photoId);

    /** 图库统计：正常状态各朝向数量 + 总大小 + 回收站数量；albumId 非空统计该相册，onlyUnassigned 统计未归册 */
    com.knockfish.vo.gallery.GalleryStatsVO selectStats(@Param("albumId") Long albumId,
                                                        @Param("onlyUnassigned") Boolean onlyUnassigned);
}
