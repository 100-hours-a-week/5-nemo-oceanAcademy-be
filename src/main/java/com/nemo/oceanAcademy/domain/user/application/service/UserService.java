package com.nemo.oceanAcademy.domain.user.application.service;

import com.nemo.oceanAcademy.domain.user.dataAccess.entity.User;
import com.nemo.oceanAcademy.domain.user.dataAccess.repository.UserRepository;
import com.nemo.oceanAcademy.domain.user.application.dto.UserCreateDTO;
import com.nemo.oceanAcademy.domain.user.application.dto.UserResponseDTO;
import com.nemo.oceanAcademy.domain.user.application.dto.UserUpdateDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Autowired
    public UserService(UserRepository userRepository, S3Client s3Client) {
        this.userRepository = userRepository;
        this.s3Client = s3Client;
    }

    // 사용자 정보 조회
    public UserResponseDTO getUserInfoById(String userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(value -> new UserResponseDTO(value.getNickname(), value.getProfileImagePath()))
                .orElse(null);
    }

    // 사용자 생성
    public void createUser(UserCreateDTO userCreateDTO) {
        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .nickname(userCreateDTO.getNickname())
                .email(userCreateDTO.getEmail())
                .profileImagePath(userCreateDTO.getProfileImagePath())
                .build();
        userRepository.save(user);
    }

    // 사용자 정보 업데이트 (프로필 이미지와 닉네임)
    public void updateUserProfile(String userId, UserUpdateDTO userUpdateDTO, MultipartFile file) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (userUpdateDTO.getNickname() != null) {
                user.setNickname(userUpdateDTO.getNickname());
            }
            if (userUpdateDTO.getEmail() != null) {
                user.setEmail(userUpdateDTO.getEmail());
            }

            // 파일이 있으면 S3에 업로드 후 경로 설정
            if (file != null && !file.isEmpty()) {
                String fileName = uploadFileToS3(file);
                user.setProfileImagePath(fileName);
            }

            userRepository.save(user);
        }
    }

    // 닉네임 중복 검사
    public boolean isNicknameAvailable(String nickname) {
        return !userRepository.existsByNickname(nickname);
    }

    // S3 파일 업로드 로직
    private String uploadFileToS3(MultipartFile file) {
        // 고유한 파일 이름 생성
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        // 파일을 임시 디렉토리에 저장
        File tempFile = convertMultipartFileToFile(file);

        try {
            // S3에 파일 업로드 요청
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.putObject(putObjectRequest, tempFile.toPath());

            // S3에 저장된 파일 경로 반환
            return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        } finally {
            // 임시 파일 삭제
            tempFile.delete();
        }
    }

    // MultipartFile을 File로 변환하는 메서드
    private File convertMultipartFileToFile(MultipartFile file) {
        File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        } catch (IOException e) {
            throw new RuntimeException("Failed to convert MultipartFile to File", e);
        }
        return convFile;
    }
}
