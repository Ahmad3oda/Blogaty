package com.blog.demo.service;

import com.blog.demo.dto.BlogVoteRequest;
import com.blog.demo.dto.BlogVoteResponse;
import com.blog.demo.dto.CommentVoteRequest;
import com.blog.demo.dto.CommentVoteResponse;
import com.blog.demo.dto.UserResponse;
import com.blog.demo.entity.*;
import com.blog.demo.exception.GlobalException;
import com.blog.demo.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final UserRepository userRepository;
    private final BlogVoteRepository blogVoteRepository;
    private final BlogRepository blogRepository;
    private final BlogService blogService;
    private final CommentVoteRepository commentVoteRepository;
    private final CommentRepository commentRepository;
    private final CommentService commentService;
    private final NotificationService notificationService;

    // -------- Blog Vote Section --------
    private void sendNotification(BlogVote blogVote){
        Blog blog = blogRepository.findByBlogId(blogVote.getId().getBlogId())
                .orElseThrow(() -> new GlobalException("Blog not found"));

        User actor = userRepository.findById(blogVote.getId().getUserId())
                .orElseThrow(() -> new GlobalException("User not found"));
        User receiver = blog.getUser();
        Notification notification = new Notification(
                null,
                receiver,
                actor,
                NotificationType.BLOG_VOTED,
                blog.getBlogId(),
                TargetType.BLOG,
                actor.getUsername() + " voted on your blog: " + blog.getContent(),
                LocalDateTime.now(),
                false
        );

        notificationService.addNotification(notification);
    }

    protected BlogVoteResponse toResponse(BlogVote blogVote) {
        return new BlogVoteResponse(blogVote);
    }

    protected List<BlogVoteResponse> toResponse(List<BlogVote> blogVoteList) {
        List<BlogVoteResponse> blogVoteResponseList = new ArrayList<>();
        blogVoteList.forEach(blogVote -> blogVoteResponseList.add(toResponse(blogVote)));
        return blogVoteResponseList;
    }

    @Override
    public List<BlogVoteResponse> findAllBlogVotesByBlogId(Long blogId) {
        List<BlogVote> blogVotes = blogVoteRepository.findAllByBlogId(blogId);
        return toResponse(blogVotes);
    }

    @Override
    public BlogVoteResponse findSingleVoteStatus(Long userId, Long blogId) {
        BlogVote blogVote = blogVoteRepository.findById(new BlogVoteID(userId, blogId))
                .orElseThrow(() -> new GlobalException("Blog vote not found"));
        return toResponse(blogVote);
    }

    @Override
    @Transactional
    public BlogVoteResponse addBlogVote(int userId, int blogId, BlogVoteRequest blogVoteRequest) {
        BlogVoteID blogVoteID = new BlogVoteID((long) userId, (long) blogId);

        Optional<BlogVote> existing = blogVoteRepository.findById(blogVoteID);
        if (existing.isPresent()) {
            return updateBlogVote(userId, blogId, blogVoteRequest);
        }
        if (blogVoteRequest.getVote() == null || blogVoteRequest.getVote() == Vote.none) {
            throw new GlobalException("Vote is not found.");
        }

        User user = userRepository.findById((long) userId)
                .orElseThrow(() -> new GlobalException("User not found - id: " + userId));
        Blog blog = blogRepository.findByBlogId((long) blogId)
                .orElseThrow(() -> new GlobalException("Blog not found - id: " + blogId));

        BlogVote blogVote = new BlogVote(
                blogVoteID,
                user,
                blog,
                blogVoteRequest.getVote()
        );
        blogVoteRepository.save(blogVote);

        blogService.recalculateVotes(blogId);
        sendNotification(blogVote);
        return toResponse(blogVote);
    }

    @Override
    @Transactional
    public BlogVoteResponse updateBlogVote(int userId, int blogId, BlogVoteRequest blogVoteRequest) {
        BlogVoteID blogVoteID = new BlogVoteID((long) userId, (long) blogId);

        BlogVote blogVote = blogVoteRepository.findById(blogVoteID)
                .orElseThrow(() -> new GlobalException("Vote not found - blog id: " + blogId + ", user id: " + userId));

        if (blogVoteRequest.getVote() == null || blogVoteRequest.getVote() == Vote.none) {
            blogVoteRepository.delete(blogVote);
            blogService.recalculateVotes(blogId);
            return new BlogVoteResponse(new UserResponse(blogVote.getUser()), Vote.none);
        }

        blogVote.setType(blogVoteRequest.getVote());
        blogVoteRepository.save(blogVote);

        blogService.recalculateVotes(blogId);
        return toResponse(blogVote);
    }

    @Override
    public void deleteBlogVote(BlogVoteID vote) {
        blogVoteRepository.deleteById(vote);
    }

    // -------- Comment Vote Section --------

    private void sendNotification(CommentVote commentVote){
        Comment comment = commentRepository.findCommentById(commentVote.getId().getCommentId())
                .orElseThrow(() -> new GlobalException("Comment not found"));

        User actor = userRepository.findById(commentVote.getId().getUserId())
                .orElseThrow(() -> new GlobalException("User not found"));
        User receiver = comment.getUser();
        Notification notification = new Notification(
                null,
                receiver,
                actor,
                NotificationType.COMMENT_VOTED,
                comment.getId(),
                TargetType.COMMENT,
                actor.getUsername() + " voted on your comment: " + comment.getContent(),
                LocalDateTime.now(),
                false
        );

        notificationService.addNotification(notification);
    }

    protected CommentVoteResponse toCommentResponse(CommentVote commentVote) {
        return new CommentVoteResponse(commentVote);
    }

    protected List<CommentVoteResponse> toCommentResponse(List<CommentVote> commentVoteList) {
        List<CommentVoteResponse> commentVoteResponses = new ArrayList<>();
        commentVoteList.forEach(commentVote -> commentVoteResponses.add(toCommentResponse(commentVote)));
        return commentVoteResponses;
    }

    @Override
    public List<CommentVoteResponse> findAllCommentVotesByCommentId(Long commentId) {
        List<CommentVote> commentVotes = commentVoteRepository.findAllByCommentId(commentId);
        return toCommentResponse(commentVotes);
    }

    @Override
    public CommentVoteResponse findSingleCommentVoteStatus(Long userId, Long commentId) {
        CommentVote commentVote = commentVoteRepository.findById(new CommentVoteID(userId, commentId))
                .orElseThrow(() -> new GlobalException("Comment vote not found"));
        return toCommentResponse(commentVote);
    }

    @Override
    @Transactional
    public CommentVoteResponse addCommentVote(int userId, int commentId, CommentVoteRequest commentVoteRequest) {
        CommentVoteID commentVoteID = new CommentVoteID((long) userId, (long) commentId);

        Optional<CommentVote> existing = commentVoteRepository.findById(commentVoteID);
        if (existing.isPresent()) {
            return updateCommentVote(userId, commentId, commentVoteRequest);
        }
        if (commentVoteRequest.getVote() == null || commentVoteRequest.getVote() == Vote.none) {
            throw new GlobalException("Vote is not found.");
        }

        User user = userRepository.findById((long) userId)
                .orElseThrow(() -> new GlobalException("User not found - id: " + userId));
        Comment comment = commentRepository.findCommentById((long) commentId)
                .orElseThrow(() -> new GlobalException("Comment not found - id: " + commentId));

        CommentVote commentVote = new CommentVote(
                commentVoteID,
                user,
                comment,
                commentVoteRequest.getVote()
        );
        commentVoteRepository.save(commentVote);

        commentService.recalculateVotes(commentId);
        sendNotification(commentVote);
        return toCommentResponse(commentVote);
    }

    @Override
    @Transactional
    public CommentVoteResponse updateCommentVote(int userId, int commentId, CommentVoteRequest commentVoteRequest) {
        CommentVoteID commentVoteID = new CommentVoteID((long) userId, (long) commentId);

        CommentVote commentVote = commentVoteRepository.findById(commentVoteID)
                .orElseThrow(() -> new GlobalException("Vote not found - commentId: " + commentId + ", user id: " + userId));

        if (commentVoteRequest.getVote() == null || commentVoteRequest.getVote() == Vote.none) {
            commentVoteRepository.delete(commentVote);
            commentService.recalculateVotes(commentId);
            return new CommentVoteResponse(new UserResponse(commentVote.getUser()), Vote.none);
        }

        commentVote.setType(commentVoteRequest.getVote());
        commentVoteRepository.save(commentVote);

        commentService.recalculateVotes(commentId);
        return toCommentResponse(commentVote);
    }

}
