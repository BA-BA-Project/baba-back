package com.baba.back.common;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.baba.back.content.domain.FileHandler;
import com.baba.back.content.domain.ImageFile;
import com.baba.back.content.exception.FileHandlerServerException;
import java.io.InputStream;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
public class FileHandlerS3 implements FileHandler {

    private final AmazonS3 amazonS3;
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Override
    public String upload(ImageFile imageFile) {
        MultipartFile file = imageFile.getFile();
        final String key = UUID.randomUUID().toString() + '_' + file.getOriginalFilename();

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());

        try (InputStream inputStream = file.getInputStream()) {
            amazonS3.putObject(
                    new PutObjectRequest(bucketName, key, inputStream, objectMetadata)
                            .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (Exception e) {
            throw new FileHandlerServerException("파일 업로드에 실패하였습니다.");
        }

        return amazonS3.getUrl(bucketName, key).toString();
    }
}
