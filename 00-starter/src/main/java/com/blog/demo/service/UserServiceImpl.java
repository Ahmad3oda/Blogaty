package com.blog.demo.service;

import com.blog.demo.CacheService;
import com.blog.demo.dto.BlogResponse;
import com.blog.demo.dto.UserRequest;
import com.blog.demo.dto.UserResponse;
import com.blog.demo.entity.Blog;
import com.blog.demo.repository.UserRepository;
import com.blog.demo.entity.User;
import com.blog.demo.exception.GlobalException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService{

    CacheService cache;
    ObjectMapper objectMapper;
    UserRepository userRepository;

    public UserServiceImpl(CacheService cache, ObjectMapper objectMapper, UserRepository userRepository) {
        this.cache = cache;
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
    }


    public UserResponse toResponse(User user) {
        UserResponse userResponse = objectMapper.convertValue(user, UserResponse.class);
        userResponse.setUsername(cache.getUsername(userResponse.getId()));
        return userResponse;
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
        Optional<User> user = userRepository.findById((long) id);

        if(user.isEmpty()){
            throw new GlobalException("User not found - id: " + id);
        }
        return toResponse(user.get());
    }


    @Override
    public UserResponse findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);

        if(user.isEmpty()){
            throw new GlobalException("User not found - username: " + username);
        }
        return toResponse(user.get());
    }

    @Override
    @Transactional
    public UserResponse addUser(User user) {
        User dbUser = userRepository.save(user);
        cache.putUser(dbUser);
        return toResponse(dbUser);
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        cache.removeUser(id);
        userRepository.deleteById((long) id);
    }
}
