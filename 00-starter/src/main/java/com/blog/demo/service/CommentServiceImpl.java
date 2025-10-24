package com.blog.demo.service;

import com.blog.demo.cache.RedisConfig;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
public class CommentServiceImpl implements CommentService {


    RedisConfig cache;
    ObjectMapper objectMapper;
    private final CommentRepository commentRepository;
    private final BlogRepository blogRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Autowired
    public CommentServiceImpl(RedisConfig cache,
                              ObjectMapper objectMapper,
                              CommentRepository commentRepository,
                              BlogRepository blogRepository, UserRepository userRepository,
                              NotificationService notificationService) {
        this.cache = cache;
        this.commentRepository = commentRepository;
        this.objectMapper = objectMapper;
        this.blogRepository = blogRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

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
        System.out.println(blog);
        System.out.println(notification);

        notificationService.addNotification(notification);
    }

    @Override
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
    public CommentResponse add(int userId, int blogId, CommentRequest comment) {
        Blog blog = blogRepository.findByBlogId(blogId);

        if(blog == null){
            throw new GlobalException("Blog not found - id: " + blogId);
        }

        Comment dbComment = new Comment(
                userRepository.findById((long) userId).get(),
                new Blog(blogId),
                comment.getContent(),
                LocalDateTime.now(), 0
        );

        incComment(blogId);
        sendNotification(dbComment);

        return toResponse(commentRepository.save(dbComment));
//        return toResponse(dbComment);
    }

    private void incComment(int blogId) {
        Blog blog = blogRepository.findByBlogId(blogId);
        if (blog == null) {
            throw new GlobalException("Blog Not Found - id: " + blogId);
        }

        int comments = blog.getComments() + 1;
        blog.setComments(comments);

        blogRepository.save(blog);
    }
    private void decComment(int blogId) {
        Blog blog = blogRepository.findByBlogId(blogId);
        if (blog == null) {
            throw new GlobalException("Blog Not Found - id: " + blogId);
        }

        int comments = blog.getComments() - 1;
        blog.setComments(comments);
        blogRepository.save(blog);
    }

    @Override
    @Transactional
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

    @Override
    @Transactional
    public void deleteByCommentId(int commentId) {
        Comment comment = commentRepository.findById(commentId);
        decComment(comment.getBlog().getBlogId());
        commentRepository.deleteById((long) commentId);
    }
}
