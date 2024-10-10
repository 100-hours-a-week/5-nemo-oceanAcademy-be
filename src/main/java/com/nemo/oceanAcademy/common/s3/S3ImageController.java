package com.nemo.oceanAcademy.common.s3;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * S3 업로드 테스트를 위한 API입니다.
 * 이미지 업로드, 삭제 기능을 테스트합니다.
 */
@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
public class S3ImageController {
    private final S3ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<?> s3Upload(@RequestPart(value = "image", required = false) MultipartFile image){
        String profileImage = imageService.upload(image);
        return ResponseEntity.ok(profileImage);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> s3delete(@RequestParam String addr){
        imageService.deleteImageFromS3(addr);
        return ResponseEntity.ok(null);
    }

    @GetMapping("/{fileName}")
    public ResponseEntity<?> getPresignedUrl(@PathVariable(name = "fileName") String fileName) {
        return ResponseEntity.ok(imageService.getPreSignedUrl("images", fileName));
    }
}
