package com.baba.back.common;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.baba.back.content.domain.ImageFile;
import com.baba.back.content.exception.FileHandlerServerException;
import java.net.MalformedURLException;
import java.net.URL;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class FileHandlerS3Test {

    public static final String VALID_URL = "http://test";

    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private FileHandlerS3 fileHandlerS3;

    @Test
    void AWS_자체_오류로_업로드가_실패하면_예외를_던진다() {
        // given
        final MockMultipartFile mockFile = new MockMultipartFile("photo", "test_file.png", "image/png",
                "Spring Framework".getBytes());
        final ImageFile imageFile = new ImageFile(mockFile);

        given(amazonS3.putObject(any(PutObjectRequest.class))).willThrow(AmazonServiceException.class);

        // when & then
        Assertions.assertThatThrownBy(() -> fileHandlerS3.upload(imageFile))
                .isInstanceOf(FileHandlerServerException.class);
    }

    @Test
    void 업로드에_성공한다() throws MalformedURLException {
        // given
        final MockMultipartFile mockFile = new MockMultipartFile("photo", "test_file.png", "image/png",
                "Spring Framework".getBytes());

        given(amazonS3.getUrl(any(), any())).willReturn(new URL(VALID_URL));

        // when
        String imageSource = fileHandlerS3.upload(new ImageFile(mockFile));

        // then
        Assertions.assertThat(imageSource).isEqualTo(VALID_URL);
    }
}
