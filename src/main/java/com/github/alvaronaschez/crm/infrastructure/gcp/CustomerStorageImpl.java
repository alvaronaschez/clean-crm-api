package com.github.alvaronaschez.crm.infrastructure.gcp;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.github.alvaronaschez.crm.application.CustomerStorageInterface;
import com.github.alvaronaschez.crm.application.CustomerService.UploadFailureException;

@Component
public class CustomerStorageImpl implements CustomerStorageInterface {

    @Override
    public String uploadFile(MultipartFile file) throws UploadFailureException {
        String fileUri;
        try {
            fileUri = GcpStorage.uploadFile(file);
        } catch (IOException e) {
            throw new UploadFailureException();
        }
        return fileUri;
    }

}
