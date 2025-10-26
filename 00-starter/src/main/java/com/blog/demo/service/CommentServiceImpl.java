package com.blog.demo.service;

import com.blog.demo.cache.RedisConfig;
import com.blog.demo.dto.BlogResponse;
import com.blog.demo.dto.CommentRequest;
import com.blog.demo.dto.CommentResponse;
import com.blog.demo.entity.*;
import com.blog.demo.exception.GlobalException;
import com.blog.demo.repository.BlogRepository;
import com.blog.demo.repository.CommentRepository;
import com.blog.demo.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {


    private final ObjectMapper objectMapper;
    private final CommentRepository commentRepository;
    private final BlogRepository blogRepository;
    private final BlogService blogService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    protected CommentResponse toResponse(Comment comment) {
        return new CommentResponse(comment);
    }

    protected List<CommentResponse> toResponse(List<Comment> comments) {
        List<CommentResponse> commentResponses = new ArrayList<>();
        comments.forEach(comment -> commentResponses.add(toResponse(comment)));
        return commentResponses;
    }

    private void sendNotification(Comment comment){
        Blog blog = blogRepository.findByBlogId(comment.getBlog().getBlogId());
        Optional<User> opActor = userRepository.findById((long) comment.getUser().getId());
        if(opActor.isEmpty()){
            throw new GlobalException("User not found");
        }
        User actor = opActor.get();
        User receiver = blog.getUser();
        Notification notification = new Notification(
                null,
                receiver,
                actor,
                NotificationType.COMMENTED,
                (long) receiver.getId(),
                TargetType.USER,
                actor.getUsername() + " commented on your post: " + comment.getBlog().getContent(),
                LocalDateTime.now(),
                false
        );
        notificationService.addNotification(notification);
    }

    @Override
    @Cacheable(value = "comments", key = "#commentId")
    public CommentResponse getByCommentId(int commentId) {
        Comment comment = commentRepository.findById(commentId);
        if (comment == null) {
            throw new GlobalException("Comment Not Found - id: " + commentId);
        }
        return toResponse(comment);
    }

    public Comment __getByCommentId(int commentId) {
        Comment comment = commentRepository.findById(commentId);
        if (comment == null) {
            throw new GlobalException("Comment Not Found - id: " + commentId);
        }
        return comment;
    }

    @Override
    public List<CommentResponse> getCommentsByBlogId(int blogId) {
        List<Comment> comments = commentRepository.findAllByBlog_BlogId(blogId);
        return toResponse(comments);
    }

    @Override
    @CachePut(value = "comments", key = "#result.id")
    public CommentResponse add(int userId, int blogId, CommentRequest comment) {
        BlogResponse blog = blogService.findByBlogId(blogId);

        if(blog == null){
            throw new GlobalException("Blog not found - id: " + blogId);
        }

        Comment dbComment = new Comment(
                userRepository.findById((long) userId).get(),
                new Blog(blogId),
                comment.getContent(),
                LocalDateTime.now(), 0
        );
        blogService.incComment(blogId);
        sendNotification(dbComment);
        return toResponse(commentRepository.save(dbComment));
    }

    @Override
    @Transactional
    @CachePut(value = "comments", key = "#result.id")
    public CommentResponse update(Map<String, Object> payload) {
        Comment dbComment = __getByCommentId((int) payload.get("commentId"));

        CommentRequest commentRequest = objectMapper.convertValue(payload, CommentRequest.class);
        ObjectNode requestNode = objectMapper.convertValue(commentRequest, ObjectNode.class);
        ObjectNode responseNode = objectMapper.convertValue(dbComment, ObjectNode.class);

        responseNode.setAll(requestNode);
        dbComment = objectMapper.convertValue(responseNode, Comment.class);
        dbComment.setDate(LocalDateTime.now());
        return toResponse(commentRepository.save(dbComment));
    }

    @CachePut(value = "comments", key = "#result.id")
    public CommentResponse updateCommentVoteCount (CommentVote commentVote) {
        Comment comment = commentRepository.findById(commentVote.getId().getComment().getId());
        System.out.println(comment);
        if(commentVote.getType() == Vote.up)
            comment.setVotes(comment.getVotes() + 1);
        else
            comment.setVotes(comment.getVotes() - 1);
        return toResponse(commentRepository.save(comment));
    }

    @Override
    @Transactional
    @CacheEvict(value = "comments", key = "#commentId")
    public void deleteByCommentId(int commentId) {
        Comment comment = commentRepository.findById(commentId);
        blogService.decComment(comment.getBlog().getBlogId());
        commentRepository.deleteById((long) commentId);
    }
}
