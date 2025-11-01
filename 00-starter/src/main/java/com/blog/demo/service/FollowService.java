package com.blog.demo.service;

import com.blog.demo.dto.FollowResponse;
import com.blog.demo.dto.UserResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface FollowService {
    FollowResponse getFollowersById(int userId);
    FollowResponse getFollowingsById(int userId);
    void addFollower(int followingId, int followerId);
    Object getSuggestions(int userId);
    void removeFollower(int followingId, int followerId);

    Map<String, Integer> getNumbers(int userId);
}
