package com.knockfish.service;

import com.github.pagehelper.PageInfo;
import com.knockfish.dto.gallery.GalleryPhotoQueryDTO;
import com.knockfish.dto.gallery.GalleryPhotoUpdateDTO;
import com.knockfish.vo.gallery.GalleryBatchUploadVO;
import com.knockfish.vo.gallery.GalleryPhotoVO;
import com.knockfish.vo.gallery.GalleryStatsVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface GalleryPhotoService {

    /** 分页查询（前台只查 status = 0，后台默认查 0） */
    PageInfo<GalleryPhotoVO> getPhotoPage(GalleryPhotoQueryDTO query, Integer pageNum, Integer pageSize);

    /** 按 ID 查询，前台会校验只返回正常状态 */
    GalleryPhotoVO getPhotoById(Long photoId, boolean onlyVisible);

    /**
     * 图库统计：各朝向数量与原图总大小。
     *
     * @param albumId        传相册 ID 只统计该相册；传 null 统计全部
     * @param onlyUnassigned 为 true 时只统计未归册（album_id 为空）的照片
     */
    GalleryStatsVO getStats(Long albumId, Boolean onlyUnassigned);

    /** 上传一张图：原图 + 缩略图 + 预览图（可选） */
    GalleryPhotoVO upload(MultipartFile file, String title, Long uploaderId, Long albumId);

    /**
     * 批量上传：并发处理，单张失败不影响其它文件。
     *
     * @param files      多张图片，单张 ≤ 20MB，一次最多 10 张
     * @param uploaderId 上传人
     */
    GalleryBatchUploadVO uploadBatch(List<MultipartFile> files, Long uploaderId, Long albumId);

    /**
     * 把照片归入相册。
     *
     * @param albumId 传 null 表示移出相册
     */
    void moveToAlbum(Long photoId, Long albumId);

    /**
     * 批量归入相册：选中的多张一次搬走，不逐张发请求。
     *
     * @param albumId 传 null 表示全部移出相册
     * @return 实际更新条数
     */
    int moveToAlbumBatch(List<Long> photoIds, Long albumId);

    /** 修改标题 / 相册 */
    void updatePhoto(GalleryPhotoUpdateDTO updateDTO);

    /**
     * 隐藏 / 取消隐藏：隐藏后前台列表与详情都取不到，后台仍可在「隐藏」视图里管理。
     *
     * @param hidden true 隐藏（status=1）/ false 取消隐藏（status=0）
     */
    void setHidden(Long photoId, boolean hidden);

    /** 从回收站恢复到正常状态 */
    void restore(Long photoId);

    /** 软删（进回收站，保留对象） */
    void softDelete(Long photoId);

    /** 彻底删除：删 3 个对象 + 删记录 */
    void purge(Long photoId);
}
