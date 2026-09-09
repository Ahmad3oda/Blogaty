package com.blog.demo.service;

import com.blog.demo.dto.BlogResponse;
import com.blog.demo.dto.CommentRequest;
import com.blog.demo.dto.CommentResponse;
import com.blog.demo.entity.*;
import com.blog.demo.exception.GlobalException;
import com.blog.demo.repository.BlogRepository;
import com.blog.demo.repository.CommentRepository;
import com.blog.demo.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;


@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BlogRepository blogRepository;
    private final BlogService blogService;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final CacheManager cacheManager;

    protected CommentResponse toResponse(@NonNull Comment comment) {
        return new CommentResponse(comment);
    }

    protected List<CommentResponse> toResponse(@NonNull List<Comment> comments) {
        List<CommentResponse> commentResponses = new ArrayList<>();
        comments.forEach(comment -> commentResponses.add(toResponse(comment)));
        return commentResponses;
    }

    private void sendNotification(@NonNull Comment comment){
        Blog blog = blogRepository.findByBlogId(comment.getBlog().getBlogId())
                .orElseThrow(() -> new GlobalException("Blog not found"));
        User actor = userRepository.findById(comment.getUser().getId())
                .orElseThrow(() -> new GlobalException("User not found"));
        User receiver = blog.getUser();
        Notification notification = new Notification(
                null,
                receiver,
                actor,
                NotificationType.COMMENTED,
                blog.getBlogId(),
                TargetType.BLOG,
                actor.getUsername() + " commented on your post: " + blog.getContent(),
                LocalDateTime.now(),
                false
        );
        notificationService.addNotification(notification);
    }

    @Override
    @Cacheable(value = "comments", key = "#commentId")
    public CommentResponse getByCommentId(int commentId) {
        Comment comment = commentRepository.findCommentById((long) commentId)
                .orElseThrow(() -> new GlobalException("Comment Not Found - id: " + commentId));
        System.out.println(comment);
        return toResponse(comment);
    }

    public Comment __getByCommentId(int commentId) {
        return commentRepository.findCommentById((long) commentId)
                .orElseThrow(() -> new GlobalException("Comment Not Found - id: " + commentId));
    }

    @Override
    public List<CommentResponse> getCommentsByBlogId(int blogId) {
        return toResponse(commentRepository.findAllByBlog_BlogId((long) blogId));
    }

    @Override
    @Cacheable(value = "blog_comments", key = "#blogId")
    public List<CommentResponse> getRecentComments(int blogId, int size) {
        return toResponse(commentRepository
                .findTopCommentsByBlogId((long) blogId, PageRequest.of(0, size)));
    }

    private void cacheComment(Comment comment){
        Long blogId = comment.getBlog().getBlogId();
        Cache cache = cacheManager.getCache("blog_comments");

        if (cache != null) {
            List<CommentResponse> cached = cache.get(blogId, List.class);
            if (cached != null) {
                cached.addFirst(toResponse(comment));
                if (cached.size() > 5)
                    cached = new ArrayList<>(cached.subList(0, 5));
                else
                    cached = new ArrayList<>(cached);
                cache.put(blogId, cached);
            }
        }
    }

    @Override
    public List<CommentResponse> getComments(int blogId, int page, int size) {
        return toResponse(commentRepository.findTopCommentsByBlogId((long) blogId, PageRequest.of(page, size)));
    }

    @Override
    @CachePut(value = "comments", key = "#result.id")
    public CommentResponse add(int userId, int blogId, CommentRequest comment) {
        BlogResponse blog = blogService.findByBlogId(blogId);

        if(blog == null){
            throw new GlobalException("Blog not found - id: " + blogId);
        }

        Comment dbComment = new Comment(
                userRepository.findById((long) userId)
                        .orElseThrow(() -> new GlobalException("User not found - id: " + userId)),
                new Blog((long) blogId),
                comment.getContent(),
                LocalDateTime.now(), 0
        );
        // Save first so dbComment has a generated ID before notification/cache
        Comment saved = commentRepository.save(dbComment);
        blogService.incComment(blogId);
        sendNotification(saved);
        cacheComment(saved);
        return toResponse(saved);
    }

    @Override
    @Transactional
    @CachePut(value = "comments", key = "#result.id")
    public CommentResponse update(@NonNull Map<String, Object> payload) {
        Comment dbComment = __getByCommentId((int) payload.get("commentId"));
        if (payload.containsKey("content")) {
            dbComment.setContent(String.valueOf(payload.get("content")));
        }
        dbComment.setDate(LocalDateTime.now());
        return toResponse(commentRepository.save(dbComment));
    }

    @CachePut(value = "comments", key = "#result.id")
    public CommentResponse updateCommentVoteCount (@NonNull CommentVote commentVote) {
        Comment comment = commentRepository.findCommentById(commentVote.getId().getCommentId())
                .orElseThrow(() -> new GlobalException("Comment Not Found"));
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
        Comment comment = commentRepository.findCommentById((long) commentId)
                .orElseThrow(() -> new GlobalException("Comment Not Found - id: " + commentId));
        blogService.decComment(Math.toIntExact(comment.getBlog().getBlogId()));
        commentRepository.deleteById((long) commentId);
    }
}
