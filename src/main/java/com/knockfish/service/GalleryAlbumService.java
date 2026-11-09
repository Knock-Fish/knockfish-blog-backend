package com.knockfish.service;

import com.knockfish.dto.gallery.AlbumUnlockDTO;
import com.knockfish.dto.gallery.GalleryAlbumDTO;
import com.knockfish.vo.gallery.AlbumUnlockVO;
import com.knockfish.vo.gallery.GalleryAlbumVO;

import java.util.List;

/**
 * 图库相册。相册只是照片的分组容器，照片本体仍由 GalleryPhotoService 管理。
 */
public interface GalleryAlbumService {

    /** 后台列表：不限状态，返回封面与照片数 */
    List<GalleryAlbumVO> getAlbumList(Integer status, String keyword);

    /** 前台列表：只返回正常状态；私密相册不给封面与照片数（避免“锁着”却漏图） */
    List<GalleryAlbumVO> getPublicAlbumList();

    /** 前台单个相册（锁态也要能看到名字，所以不过滤私密） */
    GalleryAlbumVO getPublicAlbum(Long albumId);

    /** 后台单个相册 */
    GalleryAlbumVO getAlbum(Long albumId);

    GalleryAlbumVO create(GalleryAlbumDTO dto);

    void update(GalleryAlbumDTO dto);

    /**
     * 设置 / 清除封面。
     *
     * <p>封面只能从该相册内的照片里挑，展示的是它的 thumb_key。私密相册不设封面 ——
     * 相册锁着却摆封面等于漏一张图，所以有密码时直接拒绝；改成私密时会顺带清掉旧封面。
     *
     * @param photoId 为空表示清除封面
     */
    void setCover(Long albumId, Long photoId);

    /** 删除相册：先解绑照片（album_id 置空），照片本身不动 */
    void delete(Long albumId);

    /**
     * 设置 / 清除相册密码。
     *
     * @param rawPassword 空表示清除密码（相册变公开）；否则 4~32 位
     */
    void setPassword(Long albumId, String rawPassword);

    /**
     * 解锁私密相册：校验验证码 + 密码，通过后签发访问令牌。
     * 失败计入 (相册, IP) 计数，连续 5 次锁 10 分钟。
     */
    AlbumUnlockVO unlock(Long albumId, AlbumUnlockDTO dto, String ip);
}
