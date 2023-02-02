package com.baba.back.common;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.baba.back.content.exception.FileHandlerServerException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class S3HandlerTest {

    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private S3Handler s3Handler;

    @Test
    void 업로드가_실패하면_예외를_던진다() {
        // given
        final MockMultipartFile mockFile = new MockMultipartFile("photo", "test_file.png", "image/png",
                "Spring Framework".getBytes());

        given(amazonS3.putObject(any(PutObjectRequest.class))).willThrow(AmazonServiceException.class);

        // when
        Assertions.assertThatThrownBy(() -> s3Handler.upload(mockFile))
                .isInstanceOf(FileHandlerServerException.class);

        // then
    }
}