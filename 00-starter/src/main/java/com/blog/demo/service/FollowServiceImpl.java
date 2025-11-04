package com.blog.demo.service;

import com.blog.demo.cache.RedisConfig;
import com.blog.demo.dto.FollowResponse;
import com.blog.demo.dto.UserResponse;
import com.blog.demo.entity.*;
import com.blog.demo.exception.GlobalException;
import com.blog.demo.repository.FollowerRepository;
import com.blog.demo.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class FollowServiceImpl implements FollowService{

    RedisConfig cache;
    UserService userService;
    UserRepository userRepository;
    FollowerRepository followerRepository;
    NotificationService notificationService;

    FollowServiceImpl (RedisConfig cache,
                       UserService userService,
                       UserRepository userRepository,
                       FollowerRepository followerRepository,
                       NotificationService notificationService){
        this.cache = cache;
        this.userService = userService;
        this.userRepository = userRepository;
        this.followerRepository = followerRepository;
        this.notificationService = notificationService;
    }

    private FollowResponse toResponse(List<Long> followersIds){
        List <UserResponse> followers = new ArrayList<>();
        followersIds.forEach(followerId -> followers.add(userService.findById(Math.toIntExact(followerId))));

        return new FollowResponse(followers.size(), followers);
    }

    private void sendNotification(Follower follower){

        User actor = follower.getActor();
        User receiver = userRepository.findById(Long.valueOf(follower.getReceiver().getId())).get();
        Notification notification = new Notification(
                null,
                receiver,
                actor,
                NotificationType.FOLLOWED,
                (long) receiver.getId(),
                TargetType.USER,
                actor.getUsername() + " started following you.",
                LocalDateTime.now(),
                false
        );

        notificationService.addNotification(notification);
    }

    @Override
    public FollowResponse getFollowersById(int userId) {
        return toResponse(followerRepository.findFollowingsIdByUserId(userId));
    }

    @Override
    public FollowResponse getFollowingsById(int userId) {
        return toResponse(followerRepository.findFollowersIdByUserId(userId));
    }

    @Override
    public void addFollower(int followingId, int followerId) {
        Follower follower = followerRepository.findByReceiver_IdAndActor_Id(followingId, followerId);
        if(follower != null){
            throw new GlobalException("Follower relationship exists - following id: "
                    + followingId + ", followers id: " + followerId);
        }
        User actor = new User(); actor.setId(Math.toIntExact(followerId));
        User receiver = new User(); receiver.setId(Math.toIntExact(followingId));

        follower = new Follower(receiver, actor);
        sendNotification(follower);

        followerRepository.save(follower);
    }

    @Override
    public Object getSuggestions(int userId) {
        List<UserResponse> list = userService.findAll();
        List<Long> followers = followerRepository.findFollowersIdByUserId(userId);

        followers.add((long) userId);
        return list.stream()
                .filter(user -> !followers.contains((long) user.getId()))
                .toList();
    }


    @Override
    public void removeFollower(int followingId, int followerId) {
        User actor = new User(); actor.setId(Math.toIntExact(followingId));
        User receiver = new User(); receiver.setId(Math.toIntExact(followerId));
        followerRepository.delete(new Follower(receiver, actor));
    }

    @Override
    public Map<String, Integer> getNumbers(int userId) {

        Map<String, Integer> response = new HashMap<>();
        response.put("followers", followerRepository.countByReceiver_Id(userId));
        response.put("following", followerRepository.countByActor_Id(userId));
        return response;
    }
}
