package com.knockfish.utils;

import com.luciad.imageio.webp.WebPWriteParam;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


public class ImageWebpProcessor {
    /**
     * 将 MultipartFile 压缩为 WebP 格式的字节数组
     * @param file 原始图片文件
     * @return WebP 格式的字节数组
     */
    public static byte[] convertImageToWebp(MultipartFile file){
        try{
            // 读取原始图片
            BufferedImage image = ImageIO.read(file.getInputStream());
            // 创建webp写入器
            ImageWriter writer = ImageIO.getImageWritersByFormatName("webp").next();
            WebPWriteParam writeParam = new WebPWriteParam(writer.getLocale());
            // 设置无损
            // writeParam.setCompressionType(writeParam.getCompressionTypes()[WebPWriteParam.LOSSLESS_COMPRESSION]);
            // 设置有损压缩
            writeParam.setCompressionType(writeParam.getCompressionTypes()[WebPWriteParam.LOSSY_COMPRESSION]);
            // 设置 75% 的质量，设置范围为0-1
            writeParam.setCompressionQuality(0.75f);
            // 输出到字节数组流
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageOutputStream imageOutput = ImageIO.createImageOutputStream(baos);
            writer.setOutput(imageOutput);
            // 保存图片
//            writer.setOutput(ImageIO.createImageOutputStream(new File("sample.webp")));
            writer.setOutput(imageOutput);
            // 执行压缩
            writer.write(null, new IIOImage(image, null, null), writeParam);
            // 关闭资源
            imageOutput.close();
            writer.dispose();
            return baos.toByteArray();
        }catch(IOException e){
            throw new RuntimeException(e);
        }
    }
}
