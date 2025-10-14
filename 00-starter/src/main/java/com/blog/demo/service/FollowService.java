package com.blog.demo.service;

import com.blog.demo.dto.FollowResponse;
import com.blog.demo.dto.UserResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface FollowService {
    FollowResponse getFollowersById(int userId);
    void addFollower(int followingId, int followerId);
    void removeFollower(int followingId, int followerId);
}
