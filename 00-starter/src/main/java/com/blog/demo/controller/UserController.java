package com.blog.demo.controller;

import com.blog.demo.CacheService;
import com.blog.demo.dto.UserRequest;
import com.blog.demo.dto.UserResponse;
import com.blog.demo.entity.User;
import com.blog.demo.service.UserService;
import io.swagger.v3.oas.models.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {

    UserService userService;

    @Autowired
    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping
    public List<UserResponse> getAllUsers(){
        return userService.findAll();
    }

    @GetMapping("/{userId}")
    public UserResponse getUser(@PathVariable int userId){
        return userService.findById(userId);
    }

    @PostMapping("/register")
    public UserResponse register(@RequestBody UserRequest user){
        return userService.register(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequest user){
        String token = userService.login(user);

        Map<String, String> response = new HashMap<>();
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    @PatchMapping
    public UserResponse addUser(@RequestBody Map<String, Object> payload){
        return userService.updateUser(payload);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable int userId){
        userService.deleteById(userId);
    }
}
