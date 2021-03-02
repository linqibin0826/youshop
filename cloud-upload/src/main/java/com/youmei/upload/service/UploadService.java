package com.youmei.upload.service;


import org.springframework.web.multipart.MultipartFile;


public interface UploadService {

    /**
     * Upload image.
     *
     * @param file the file
     * @return the string
     * @author youmei
     * @since 2021 /2/15 20:08
     */
    String uploadImage(MultipartFile file);
}
