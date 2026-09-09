package com.blog.demo.service;

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

    private final UserService userService;
    private final UserRepository userRepository;
    private final FollowerRepository followerRepository;
    private final NotificationService notificationService;

    public FollowServiceImpl (UserService userService,
                              UserRepository userRepository,
                              FollowerRepository followerRepository,
                              NotificationService notificationService){
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
        User actor = userRepository.findById(follower.getActor().getId())
                .orElseThrow(() -> new GlobalException("Actor user not found"));
        User receiver = userRepository.findById(follower.getReceiver().getId())
                .orElseThrow(() -> new GlobalException("Receiver user not found"));
        Notification notification = new Notification(
                null,
                receiver,
                actor,
                NotificationType.FOLLOWED,
                receiver.getId(),
                TargetType.USER,
                actor.getUsername() + " started following you.",
                LocalDateTime.now(),
                false
        );

        notificationService.addNotification(notification);
    }

    @Override
    public FollowResponse getFollowersById(int userId) {
        return toResponse(followerRepository.findFollowersIdByUserId((long) userId));
    }

    @Override
    public FollowResponse getFollowingsById(int userId) {
        return toResponse(followerRepository.findFollowingsIdByUserId((long) userId));
    }

    @Override
    public void addFollower(int receiverId, int actorId) {
        Follower follower = followerRepository.findByReceiver_IdAndActor_Id((long) receiverId, (long) actorId);
        if(follower != null){
            throw new GlobalException("Follower relationship exists - receiver id: "
                    + receiverId + ", followers id: " + actorId);
        }
        User actor = new User((long) actorId);
        User receiver = new User((long) receiverId);

        follower = new Follower(receiver, actor);
        sendNotification(follower);

        followerRepository.save(follower);
    }

    @Override
    public Object getSuggestions(int userId) {
        List<UserResponse> list = userService.findAll();
        List<Long> followers = followerRepository.findFollowingsIdByUserId((long) userId);

        followers.add((long) userId);
        return list.stream()
                .filter(user -> !followers.contains(user.getId()))
                .toList();
    }

    @Override
    public void removeFollower(int receiverId, int actorId) {
        User actor = new User((long) actorId);
        User receiver = new User((long) receiverId);
        followerRepository.delete(new Follower(receiver, actor));
    }

    @Override
    public Map<String, Integer> getNumbers(int userId) {
        Map<String, Integer> response = new HashMap<>();
        response.put("followers", followerRepository.countByReceiver_Id((long) userId));
        response.put("following", followerRepository.countByActor_Id((long) userId));
        return response;
    }
}
