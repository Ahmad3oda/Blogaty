package com.blog.demo.service;

import com.blog.demo.CacheService;
import com.blog.demo.dto.FollowResponse;
import com.blog.demo.dto.UserResponse;
import com.blog.demo.entity.Follower;
import com.blog.demo.entity.FollowerID;
import com.blog.demo.exception.GlobalException;
import com.blog.demo.repository.FollowerRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FollowServiceImpl implements FollowService{

    CacheService cache;
    UserService userService;
    FollowerRepository followerRepository;

    FollowServiceImpl (CacheService cache,
                       UserService userService,
                       FollowerRepository followerRepository){
        this.cache = cache;
        this.userService = userService;
        this.followerRepository = followerRepository;
    }

    private FollowResponse toResponse(List<Long> followersIds){
        List <UserResponse> followers = new ArrayList<>();
        followersIds.forEach(followerId -> followers.add(userService.findById(Math.toIntExact(followerId))));

        return new FollowResponse(followers.size(), followers);
    }

    @Override
    public FollowResponse getFollowersById(int userId) {
        List<Long> followersIds = followerRepository.findFollowersIdByUserId(userId);
        return toResponse(followersIds);
    }

    @Override
    public void addFollower(int followingId, int followerId) {
        Follower follower = followerRepository.findById_UserIdAndId_FollowerId(followingId, followerId);
        if(follower != null){
            throw new GlobalException("Follower relationship exists - following id: "
                    + followingId + ", followers id: " + followerId);
        }
        followerRepository.save(new Follower(new FollowerID(followingId, followerId)));
    }

    @Override
    public void removeFollower(int followingId, int followerId) {
        followerRepository.delete(new Follower(new FollowerID(followingId, followerId)));
    }
}
