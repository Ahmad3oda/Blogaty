package com.blog.demo.controller;

import com.blog.demo.dto.FollowResponse;
import com.blog.demo.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

    @GetMapping("/by/{userId}")
    public FollowResponse getFollowings(@PathVariable int userId){
        return followService.getFollowingsById(userId);
    }

    @GetMapping("/numbers/{userId}")
    public ResponseEntity<?> getFollowersNumbers(@PathVariable int userId){
        Map<String, Integer> response = followService.getNumbers(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/suggest/{userId}")
    public ResponseEntity<?> getFollowingSuggestions(@PathVariable int userId){
        return ResponseEntity.ok(followService.getSuggestions(userId));
    }

    @PostMapping("/{receiverId}/{actorId}")
    public void addFollower(@PathVariable int receiverId, @PathVariable int actorId){
        followService.addFollower(receiverId, actorId);
    }

    @DeleteMapping("/{receiverId}/{actorId}")
    public void deleteFollower(@PathVariable int receiverId, @PathVariable int actorId){
        followService.removeFollower(receiverId, actorId);
    }
}
