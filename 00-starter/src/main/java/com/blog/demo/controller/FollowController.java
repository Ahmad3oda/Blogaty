package com.blog.demo.controller;

import com.blog.demo.dto.FollowResponse;
import com.blog.demo.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/followers")
public class FollowController {

    FollowService followService;

    @Autowired
    FollowController (FollowService followService){
        this.followService = followService;
    }

    @GetMapping("/{userId}")
    public FollowResponse getFollowers(@PathVariable int userId){
        return followService.getFollowersById(userId);
    }

    @PostMapping("/{followingId}/{followerId}")
    public void addFollower(@PathVariable int followingId, @PathVariable int followerId){
        followService.addFollower(followingId, followerId);
    }

    @DeleteMapping("/{followingId}/{followerId}")
    public void deleteFollower(@PathVariable int followingId, @PathVariable int followerId){
        followService.removeFollower(followingId, followerId);
    }
}
