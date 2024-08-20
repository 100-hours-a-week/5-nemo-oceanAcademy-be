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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Service
public class UserService {

    private final UserRepository userRepository;

    // S3 관련 설정 (임시 주석 처리)
    // private final S3Client s3Client;

    // 파일 저장 경로를 application.yml에서 불러옴
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        // this.s3Client = s3Client;
    }

    // 사용자 정보 조회
    public UserResponseDTO getUserInfoById(String userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(value -> new UserResponseDTO(value.getNickname(), value.getEmail(), value.getProfileImagePath()))
                .orElse(null); // 사용자가 없으면 null 반환
    }

    // 사용자 생성
    public void createUser(UserCreateDTO userCreateDTO) {
        User user = User.builder()
                .id(UUID.randomUUID().toString()) // 고유 UUID 생성
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

            // 닉네임 업데이트
            if (userUpdateDTO.getNickname() != null) {
                user.setNickname(userUpdateDTO.getNickname());
            }
            // 이메일 업데이트
            if (userUpdateDTO.getEmail() != null) {
                user.setEmail(userUpdateDTO.getEmail());
            }

            // 파일이 있으면 디렉토리에 업로드 후 경로 설정
            if (file != null && !file.isEmpty()) {
                String fileName = saveFileToDirectory(file);
                user.setProfileImagePath(fileName);
            }

            userRepository.save(user); // 수정된 사용자 정보 저장
        }
    }

    // 닉네임 중복 검사
    public boolean isNicknameAvailable(String nickname) {
        return !userRepository.existsByNickname(nickname); // 닉네임 중복 확인
    }

    // 로컬 디렉토리에 파일 저장 로직
    private String saveFileToDirectory(MultipartFile file) {
        try {
            String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();      // 고유한 파일 이름 생성
            Path filePath = Paths.get(uploadDir + File.separator + fileName);                  // images 폴더에 저장
            Files.createDirectories(filePath.getParent());                                          // 저장 경로가 없으면 생성
            Files.write(filePath, file.getBytes());                                                 // 파일 저장
            return filePath.toString();                                                             // 저장된 파일의 경로 반환
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file to directory", e);
        }
    }

    // S3 파일 업로드 로직 (임시 주석 처리)
    /*
    private String uploadFileToS3(MultipartFile file) {
        String fileName = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();          // 고유한 파일 이름 생성
        File tempFile = convertMultipartFileToFile(file);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.putObject(putObjectRequest, tempFile.toPath());                                // S3에 파일 업로드

            return "https://" + bucketName + ".s3.amazonaws.com/" + fileName;                       // 업로드된 파일의 경로 반환
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        } finally {
            tempFile.delete();                                                                      // 임시 파일 삭제
        }
    }
    */

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
