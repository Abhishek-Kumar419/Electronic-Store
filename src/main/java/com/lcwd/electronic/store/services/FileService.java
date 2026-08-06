package com.lcwd.electronic.store.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

public interface FileService {


    public String uploadFile(MultipartFile image,String path) throws IOException;

    InputStream getResource(String path, String name) throws IOException;

}
