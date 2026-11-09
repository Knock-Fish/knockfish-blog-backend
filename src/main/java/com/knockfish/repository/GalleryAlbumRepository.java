package com.knockfish.repository;

import com.knockfish.entity.GalleryAlbum;
import com.knockfish.vo.gallery.GalleryAlbumVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface GalleryAlbumRepository {

    /**
     * 相册列表（带封面 key、照片数、是否加密）。
     *
     * @param status  0 正常 / 1 隐藏；null 查全部
     * @param keyword 相册名模糊匹配
     */
    List<GalleryAlbumVO> selectList(@Param("status") Integer status,
                                    @Param("keyword") String keyword);

    GalleryAlbumVO selectVOById(@Param("albumId") Long albumId);

    GalleryAlbum selectById(@Param("albumId") Long albumId);

    int insert(GalleryAlbum album);

    int update(GalleryAlbum album);

    /** 把该相册下的照片全部解绑（album_id 置空），删相册前调用，照片本身不删 */
    int clearPhotos(@Param("albumId") Long albumId);

    /** 设置密码；passwordHash 传 null 表示清除（变公开） */
    int updatePassword(@Param("albumId") Long albumId,
                       @Param("passwordHash") String passwordHash,
                       @Param("passwordVer") Integer passwordVer);

    /** 设置封面；photoId 传 null 表示清除封面 */
    int updateCover(@Param("albumId") Long albumId, @Param("photoId") Long photoId);

    /** 照片被彻底删除或移出相册时，清掉以它为封面的引用，避免封面指到别的相册的图 */
    int clearCoverByPhotoId(@Param("photoId") Long photoId);

    /**
     * 批量版本：清掉以这批照片为封面的引用。
     *
     * @param albumId 目标相册；传了则保留它自己的封面（这些照片还在它里面）
     */
    int clearCoverByPhotoIds(@Param("photoIds") List<Long> photoIds, @Param("albumId") Long albumId);

    int deleteById(@Param("albumId") Long albumId);
}
