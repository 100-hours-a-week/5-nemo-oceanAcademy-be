package com.nemo.oceanAcademy.common.s3;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RequiredArgsConstructor
@Component
public class S3ImageUtils {
    private final S3ImageService imageService;

    // 파일을 S3에 저장
    public String saveFileToS3(MultipartFile imagefile) {
        try {
            String fileName = imageService.upload(imagefile);
            return fileName;
        } catch (RuntimeException e) {
            Sentry.withScope(scope -> {
                scope.setTag("file_name", imagefile.getOriginalFilename());
                scope.setTag("file_size", String.valueOf(imagefile.getSize()));
                Sentry.captureException(e);
            });
            throw new RuntimeException("파일 저장에 실패했습니다.", e);
        }
    }

    // S3에 저장된 이미지를 삭제
    public void deleteFileFromS3(String addr){
        try {
            imageService.deleteImageFromS3(addr);
        } catch (RuntimeException e) {
            Sentry.withScope(scope -> {
                scope.setTag("file_url", addr);
                Sentry.captureException(e);
            });
            throw new RuntimeException("파일 삭제에 실패했습니다.", e);
        }
    }
}
