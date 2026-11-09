package com.knockfish.utils;

import com.luciad.imageio.webp.WebPWriteParam;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 图片缩放 + WebP 编码工具。
 *
 * <p>与 {@link ImageWebpProcessor} 的区别：这里支持按目标尺寸缩放，并且「只缩不放」——
 * 目标尺寸大于原图时直接按原图编码，避免把小图拉伸成马赛克。
 *
 * <p>WebP 写入沿用项目已有的 {@code webp-imageio}（com.luciad WebPWriteParam），不额外引依赖。
 */
public final class ImageThumbnailUtil {

    private ImageThumbnailUtil() {
    }

    /**
     * 按宽度等比缩放并编码为 WebP。
     *
     * @param source   原图
     * @param maxWidth 目标宽度上限（不会放大）
     * @param quality  0~1
     */
    public static byte[] resizeToWebpByWidth(BufferedImage source, int maxWidth, float quality) {
        int targetWidth = Math.min(maxWidth, source.getWidth());
        int targetHeight = Math.max(1, Math.round((float) source.getHeight() * targetWidth / source.getWidth()));
        return encodeWebp(scale(source, targetWidth, targetHeight), quality);
    }

    /**
     * 按长边等比缩放并编码为 WebP。
     *
     * @param source   原图
     * @param maxEdge  长边上限（不会放大）
     * @param quality  0~1
     */
    public static byte[] resizeToWebpByLongEdge(BufferedImage source, int maxEdge, float quality) {
        int w = source.getWidth();
        int h = source.getHeight();
        int longEdge = Math.max(w, h);
        if (longEdge <= maxEdge) {
            return encodeWebp(source, quality);
        }
        float ratio = (float) maxEdge / longEdge;
        int targetWidth = Math.max(1, Math.round(w * ratio));
        int targetHeight = Math.max(1, Math.round(h * ratio));
        return encodeWebp(scale(source, targetWidth, targetHeight), quality);
    }

    /** 等比缩放到指定宽高（调用方保证比例正确） */
    public static BufferedImage scale(BufferedImage source, int width, int height) {
        if (width == source.getWidth() && height == source.getHeight()) {
            return source;
        }
        // 用 TYPE_INT_ARGB 保留 PNG 的透明通道，转成 RGB 会把透明区域填黑
        BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = target.createGraphics();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.drawImage(source, 0, 0, width, height, null);
        } finally {
            g.dispose();
        }
        return target;
    }

    /** 编码为有损 WebP */
    public static byte[] encodeWebp(BufferedImage image, float quality) {
        ImageWriter writer = ImageIO.getImageWritersByFormatName("webp").next();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ImageOutputStream out = ImageIO.createImageOutputStream(baos)) {

            WebPWriteParam param = new WebPWriteParam(writer.getLocale());
            param.setCompressionType(param.getCompressionTypes()[WebPWriteParam.LOSSY_COMPRESSION]);
            param.setCompressionQuality(quality);

            writer.setOutput(out);
            writer.write(null, new IIOImage(image, null, null), param);
            out.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("WebP 编码失败", e);
        } finally {
            writer.dispose();
        }
    }
}
