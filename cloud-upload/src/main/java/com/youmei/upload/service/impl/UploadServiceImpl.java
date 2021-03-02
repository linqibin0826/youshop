package com.youmei.upload.service.impl;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.youmei.upload.service.UploadService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class UploadServiceImpl implements UploadService {

    @Autowired
    private FastFileStorageClient fastClient;

    private static final List<String> CONTENT_TYPE = Arrays.asList("image/jpeg", "image/png", "image/gif");

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadServiceImpl.class);

    @Override
    public String uploadImage(MultipartFile file){

        String filename = file.getOriginalFilename();

        // 1.检验文件的媒体类型
        String contentType = file.getContentType();

        // 2.判断文件类型是否合法
        if (!CONTENT_TYPE.contains(contentType)) {
            LOGGER.info("文件类型不合法: {}", filename);
            return null;
        }

        // 3.检验文件内容
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (bufferedImage == null) {
            LOGGER.info("文件内容不合法", filename);
            return null;
        }

        String fullPath = null;
        String ext = StringUtils.substringAfterLast(filename, ".");

        // 保存到服务器
        try {
            // file.transferTo(new File("F:\\youshopImageServer\\images\\" + filename));

            StorePath storePath = fastClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);
            fullPath = storePath.getFullPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "http://image.youshop.com/" + fullPath;
    }
}
