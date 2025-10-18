package com.blog.demo.service;

import com.blog.demo.dto.UserRequest;
import com.blog.demo.dto.UserResponse;
import com.blog.demo.entity.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface UserService {
    List<UserResponse> findAll();
    UserResponse findById(int id);
    UserResponse findByUsername(String username);
    UserResponse addUser(UserRequest user);
    UserResponse updateUser(Map <String, Object> payload);
    void deleteById(int id);
}
