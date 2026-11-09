package com.knockfish.service.impl;

import com.knockfish.convert.GalleryAlbumConvert;
import com.knockfish.dto.gallery.AlbumUnlockDTO;
import com.knockfish.dto.gallery.GalleryAlbumDTO;
import com.knockfish.entity.GalleryAlbum;
import com.knockfish.entity.GalleryPhoto;
import com.knockfish.exception.CustomException;
import com.knockfish.repository.GalleryAlbumRepository;
import com.knockfish.repository.GalleryPhotoRepository;
import com.knockfish.service.AlbumTokenService;
import com.knockfish.service.CaptchaService;
import com.knockfish.service.GalleryAlbumService;
import com.knockfish.service.R2FileService;
import com.knockfish.vo.gallery.AlbumUnlockVO;
import com.knockfish.vo.gallery.GalleryAlbumVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GalleryAlbumServiceImpl implements GalleryAlbumService {

    /** 0 正常 / 1 隐藏 */
    private static final int STATUS_NORMAL = 0;

    /** 2 回收站：回收站里的图不能当封面 */
    private static final int STATUS_RECYCLED = 2;

    private final GalleryAlbumRepository galleryAlbumRepository;
    private final GalleryPhotoRepository galleryPhotoRepository;
    private final GalleryAlbumConvert galleryAlbumConvert;
    private final R2FileService r2FileService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final CaptchaService captchaService;
    private final AlbumTokenService albumTokenService;

    @Override
    public List<GalleryAlbumVO> getAlbumList(Integer status, String keyword) {
        List<GalleryAlbumVO> list = galleryAlbumRepository.selectList(status, keyword);
        list.forEach(vo -> {
            vo.setCoverUrl(r2FileService.buildUrl(vo.getCoverKey()));
            vo.setCoverKey(null);
            if (vo.getLocked() == null) {
                vo.setLocked(Boolean.FALSE);
            }
        });
        return list;
    }

    @Override
    public List<GalleryAlbumVO> getPublicAlbumList() {
        List<GalleryAlbumVO> list = getAlbumList(STATUS_NORMAL, null);
        // 私密相册不露封面、不露数量：只留名字与锁标记，前端画占位
        list.forEach(vo -> {
            if (Boolean.TRUE.equals(vo.getLocked())) {
                vo.setCoverUrl(null);
                vo.setCoverPhotoId(null);
                vo.setPhotoCount(null);
            }
        });
        return list;
    }

    @Override
    public GalleryAlbumVO getPublicAlbum(Long albumId) {
        GalleryAlbumVO vo = getAlbum(albumId);
        if (Integer.valueOf(STATUS_NORMAL).equals(vo.getStatus())) {
            return vo;
        }
        throw new CustomException(404, "相册不存在");
    }

    @Override
    public GalleryAlbumVO getAlbum(Long albumId) {
        if (albumId == null) {
            throw new CustomException(400, "相册ID不能为空");
        }
        GalleryAlbumVO vo = galleryAlbumRepository.selectVOById(albumId);
        if (vo == null) {
            throw new CustomException(404, "相册不存在");
        }
        vo.setCoverUrl(r2FileService.buildUrl(vo.getCoverKey()));
        vo.setCoverKey(null);
        if (vo.getLocked() == null) {
            vo.setLocked(Boolean.FALSE);
        }
        return vo;
    }

    @Override
    public GalleryAlbumVO create(GalleryAlbumDTO dto) {
        GalleryAlbum album = galleryAlbumConvert.toEntity(dto);
        album.setAlbumId(null);
        album.setPasswordHash(null);
        album.setPasswordVer(0);
        album.setSortOrder(dto.getSortOrder() == null ? 0 : dto.getSortOrder());
        album.setStatus(dto.getStatus() == null ? STATUS_NORMAL : dto.getStatus());
        album.setCreateTime(LocalDateTime.now());
        galleryAlbumRepository.insert(album);
        log.info("新建相册 albumId={} name={}", album.getAlbumId(), album.getName());
        return getAlbum(album.getAlbumId());
    }

    @Override
    public void update(GalleryAlbumDTO dto) {
        if (dto.getAlbumId() == null) {
            throw new CustomException(400, "相册ID不能为空");
        }
        GalleryAlbum exist = galleryAlbumRepository.selectById(dto.getAlbumId());
        if (exist == null) {
            throw new CustomException(404, "相册不存在");
        }
        GalleryAlbum album = galleryAlbumConvert.toEntity(dto);
        // 密码字段不在这里改，走独立的设置密码接口
        album.setPasswordHash(null);
        album.setPasswordVer(null);
        galleryAlbumRepository.update(album);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setCover(Long albumId, Long photoId) {
        GalleryAlbum album = galleryAlbumRepository.selectById(albumId);
        if (album == null) {
            throw new CustomException(404, "相册不存在");
        }
        if (album.getPasswordHash() != null) {
            throw new CustomException(400, "私密相册不能设置封面，请先解除密码");
        }
        if (photoId != null) {
            GalleryPhoto photo = galleryPhotoRepository.selectById(photoId);
            if (photo == null) {
                throw new CustomException(404, "图片不存在");
            }
            if (!albumId.equals(photo.getAlbumId())) {
                throw new CustomException(400, "只能选本相册里的图片当封面");
            }
            if (Integer.valueOf(STATUS_RECYCLED).equals(photo.getStatus())) {
                throw new CustomException(400, "回收站里的图片不能当封面");
            }
            // 封面是公开位置：敏感图放上去等于绕过前台的模糊处理
            if (Integer.valueOf(1).equals(photo.getNsfw())) {
                throw new CustomException(400, "敏感图不能当封面");
            }
        }
        galleryAlbumRepository.updateCover(albumId, photoId);
        log.info("相册{}封面 albumId={} photoId={}", photoId == null ? "清除" : "设置", albumId, photoId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void setPassword(Long albumId, String rawPassword) {
        GalleryAlbum album = galleryAlbumRepository.selectById(albumId);
        if (album == null) {
            throw new CustomException(404, "相册不存在");
        }
        // 版本号 +1：改密码后，之前签发的令牌全部失效
        int nextVer = (album.getPasswordVer() == null ? 0 : album.getPasswordVer()) + 1;

        if (rawPassword == null || rawPassword.isBlank()) {
            galleryAlbumRepository.updatePassword(albumId, null, nextVer);
            log.info("相册改为公开 albumId={}", albumId);
            return;
        }
        String password = rawPassword.trim();
        if (password.length() < 4 || password.length() > 32) {
            throw new CustomException(400, "密码长度需在 4~32 位之间");
        }
        galleryAlbumRepository.updatePassword(albumId, passwordEncoder.encode(password), nextVer);
        // 私密相册不留封面：锁着的相册还摆一张封面，等于把里面的图漏出去一张
        galleryAlbumRepository.updateCover(albumId, null);
        log.info("相册已设为私密 albumId={}，已清除封面", albumId);
    }

    @Override
    public AlbumUnlockVO unlock(Long albumId, AlbumUnlockDTO dto, String ip) {
        GalleryAlbum album = galleryAlbumRepository.selectById(albumId);
        if (album == null || !Integer.valueOf(STATUS_NORMAL).equals(album.getStatus())) {
            throw new CustomException(404, "相册不存在");
        }
        if (album.getPasswordHash() == null) {
            throw new CustomException(400, "该相册不需要密码");
        }
        if (captchaService.isLocked(albumId, ip)) {
            throw new CustomException(429, "密码错误次数过多，请 10 分钟后再试");
        }

        // 验证码一次性消费；错了直接抛出，不消耗密码尝试次数
        captchaService.verifyAndConsume(dto.getCaptchaId(), dto.getCaptcha(), ip);

        if (!passwordEncoder.matches(dto.getPassword(), album.getPasswordHash())) {
            int fail = captchaService.recordFail(albumId, ip);
            int left = Math.max(0, captchaService.getMaxFail() - fail);
            throw new CustomException(400, left > 0
                    ? "密码不正确，还可尝试 " + left + " 次"
                    : "密码错误次数过多，请 10 分钟后再试");
        }

        captchaService.clearFail(albumId, ip);
        int ver = album.getPasswordVer() == null ? 0 : album.getPasswordVer();

        AlbumUnlockVO vo = new AlbumUnlockVO();
        vo.setToken(albumTokenService.issue(albumId, ver));
        vo.setExpiresIn(albumTokenService.getTtlSeconds());
        log.info("相册解锁成功 albumId={} ip={}", albumId, ip);
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long albumId) {
        GalleryAlbum exist = galleryAlbumRepository.selectById(albumId);
        if (exist == null) {
            throw new CustomException(404, "相册不存在");
        }
        // 先解绑再删，照片本身不删
        int cleared = galleryAlbumRepository.clearPhotos(albumId);
        galleryAlbumRepository.deleteById(albumId);
        log.info("删除相册 albumId={} name={}，解绑照片 {} 张", albumId, exist.getName(), cleared);
    }
}
