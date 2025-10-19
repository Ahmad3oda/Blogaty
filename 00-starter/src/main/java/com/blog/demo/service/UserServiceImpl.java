package com.blog.demo.service;

import com.blog.demo.CacheService;
import com.blog.demo.dto.BlogResponse;
import com.blog.demo.dto.UserRequest;
import com.blog.demo.dto.UserResponse;
import com.blog.demo.entity.Blog;
import com.blog.demo.entity.Role;
import com.blog.demo.repository.UserRepository;
import com.blog.demo.entity.User;
import com.blog.demo.exception.GlobalException;
import com.blog.demo.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;

    public UserResponse toResponse(User user) {
        return new UserResponse(user);
    }

    @Override
    public String login(UserRequest userRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userRequest.getUsername(), userRequest.getPassword())
        );
        User user = userRepository.findByUsername(userRequest.getUsername())
                .orElseThrow();
        return jwtUtil.generateToken(user);
    }

    @Override
    public List<UserResponse> findAll() {
        List <User> users = userRepository.findAll();
        List<UserResponse> userResponses = new ArrayList<>();
        users.forEach(user -> userResponses.add(toResponse(user)));
        return userResponses;
    }

    @Override
    public UserResponse findById(int id) {
        User user = userRepository.findById((long) id)
                .orElseThrow(() -> new GlobalException("User not found - id: " + id));
        return toResponse(user);
    }


    @Override
    public UserResponse findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new GlobalException("User not found - username: " + username))    ;
        return toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse register(UserRequest user) {
        User dbUser = objectMapper.convertValue(user, User.class);
        if(dbUser.getPassword().isEmpty() || dbUser.getUsername().isEmpty()){
            throw new GlobalException("Username or password is empty.");
        }
        dbUser.setPassword(passwordEncoder.encode(user.getPassword()));
        dbUser.setRole(Role.USER);
        dbUser = userRepository.save(dbUser);
        return toResponse(dbUser);
    }

    @Override
    @Transactional
    public UserResponse updateUser(Map<String, Object> payload) {
        if(!payload.containsKey("username")){
            throw new GlobalException("Username is empty");
        }
        User dbUser = userRepository.findByUsername(String.valueOf(payload.get("username")))
                .orElseThrow(() -> new GlobalException("User not found - username: " + payload.get("username")));

        dbUser.setPassword(String.valueOf(payload.get("password")));
        return toResponse(userRepository.save(dbUser));
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        userRepository.deleteById((long) id);
    }
}
