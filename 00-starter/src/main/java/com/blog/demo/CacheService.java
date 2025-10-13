package com.blog.demo;

import com.blog.demo.entity.User;
import com.blog.demo.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class CacheService {
    private final UserRepository userRepository;
    private final Map<Integer, String> usersCache = new ConcurrentHashMap<>();

    @PostConstruct
    public void loadUsers() {
        userRepository.findAll().forEach(user ->
                usersCache.put(user.getId(), user.getUsername()));
    }

    public void putUser(User user) {
        usersCache.put(user.getId(), user.getUsername());
    }

    public void removeUser(int userId) {
        usersCache.remove(userId);
    }

    public String getUsername(int userId) {
        return usersCache.get(userId);
    }

    public Map<Integer, String> getAllUsers() {
        return usersCache;
    }
}
