package com.blog.demo.service;

import com.blog.demo.RedisConfig;
import com.blog.demo.dto.FollowResponse;
import com.blog.demo.dto.UserResponse;
import com.blog.demo.entity.*;
import com.blog.demo.exception.GlobalException;
import com.blog.demo.repository.FollowerRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FollowServiceImpl implements FollowService{

    RedisConfig cache;
    UserService userService;
    FollowerRepository followerRepository;
    NotificationService notificationService;

    FollowServiceImpl (RedisConfig cache,
                       UserService userService,
                       FollowerRepository followerRepository,
                       NotificationService notificationService){
        this.cache = cache;
        this.userService = userService;
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
        User receiver = follower.getReceiver();
        Notification notification = new Notification(
                null,
                receiver,
                actor,
                NotificationType.FOLLOWED,
                (long) receiver.getId(),
                TargetType.USER,
                actor.getUsername() + " followed you.",
                LocalDateTime.now(),
                false
        );

        notificationService.addNotification(notification);
    }

    @Override
    public FollowResponse getFollowersById(int userId) {
        List<Long> followersIds = followerRepository.findFollowersIdByUserId(userId);
        return toResponse(followersIds);
    }

    @Override
    public void addFollower(int followingId, int followerId) {
        Follower follower = followerRepository.findByReceiver_IdAndActor_Id(followingId, followerId);
        if(follower != null){
            throw new GlobalException("Follower relationship exists - following id: "
                    + followingId + ", followers id: " + followerId);
        }
        User actor = new User(); actor.setId(Math.toIntExact(followingId));
        User receiver = new User(); receiver.setId(Math.toIntExact(followerId));

        follower = new Follower(receiver, actor);
        sendNotification(follower);

        followerRepository.save(follower);
    }

    @Override
    public void removeFollower(int followingId, int followerId) {
        User actor = new User(); actor.setId(Math.toIntExact(followingId));
        User receiver = new User(); receiver.setId(Math.toIntExact(followerId));
        followerRepository.delete(new Follower(receiver, actor));
    }
}
