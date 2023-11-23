package com.github.alvaronaschez.crm.application;

import org.springframework.web.multipart.MultipartFile;

import com.github.alvaronaschez.crm.application.CustomerService.UploadFailureException;

public interface CustomerStorageInterface {
    public String uploadFile(MultipartFile file) throws UploadFailureException;

}
