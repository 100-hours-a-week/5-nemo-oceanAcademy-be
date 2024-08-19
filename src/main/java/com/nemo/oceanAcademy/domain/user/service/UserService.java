import com.nemo.oceanAcademy.domain.user.entity.User;
import com.nemo.oceanAcademy.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // JWT 토큰에서 추출된 사용자 ID로 유저 정보를 조회하는 메서드
    public Optional<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }

    // 새로운 유저 생성 (OAuth를 통해 받은 유저 정보 저장)
    public User createUser(String userId, String nickname, String profileImagePath) {
        User newUser = User.builder()
                .id(userId)
                .nickname(nickname)
                .profileImagePath(profileImagePath)
                .build();
        return userRepository.save(newUser);
    }

    // 유저 정보 수정 (프로필, 닉네임 등)
    public User updateUser(String userId, String newNickname, String newProfileImagePath) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            User updatedUser = user.get();
            updatedUser.setNickname(newNickname);
            updatedUser.setProfileImagePath(newProfileImagePath);
            return userRepository.save(updatedUser);
        }
        return null;
    }

    // 유저 탈퇴 (soft delete)
    public void deleteUser(String userId) {
        Optional<User> user = userRepository.findById(userId);
        user.ifPresent(User::softDelete);
        userRepository.save(user.get());
    }
}
