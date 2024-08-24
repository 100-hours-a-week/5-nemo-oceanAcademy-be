package com.nemo.oceanAcademy.controller;

import com.nemo.oceanAcademy.entity.User;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private List<User> users = new ArrayList<>();

    // 사용자 생성
    @PostMapping
    public User createUser(@RequestBody User user) {
        user.setId((long) (users.size() + 1));  // ID 생성
        users.add(user);
        return user;
    }

    // 모든 사용자 조회
    @GetMapping
    public List<User> getUsers() {
        return users;
    }

    // 사용자 조회 by ID
    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return users.stream()
                .filter(user -> user.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // 사용자 업데이트
    @PutMapping("/{id}")
    public User updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        User user = getUserById(id);
        if (user != null) {
            user.setName(updatedUser.getName());
            user.setEmail(updatedUser.getEmail());
        }
        return user;
    }

    // 사용자 삭제
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        users.removeIf(user -> user.getId().equals(id));
    }
}
