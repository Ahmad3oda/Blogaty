package com.blog.demo.service;

import com.blog.demo.cache.RedisConfig;
import com.blog.demo.dto.BlogVoteRequest;
import com.blog.demo.dto.BlogVoteResponse;
import com.blog.demo.dto.CommentVoteRequest;
import com.blog.demo.dto.CommentVoteResponse;
import com.blog.demo.entity.*;
import com.blog.demo.exception.GlobalException;
import com.blog.demo.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {

    private final UserRepository userRepository;
    private final BlogVoteRepository blogVoteRepository;
    private final BlogRepository blogRepository;
    private final BlogService blogService;
    private final CommentVoteRepository commentVoteRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;

    // -------- Blog Vote Section --------
    private void sendNotification(BlogVote blogVote){

        Blog blog = blogRepository.findByBlogId(blogVote.getId().getBlog().getBlogId());

        User actor = userRepository.findById((long) blogVote.getId().getUser().getId()).get();
        User receiver = userRepository.findById((long) blog.getUser().getId()).get();
        Notification notification = new Notification(
                null,
                receiver,
                actor,
                NotificationType.BLOG_VOTED,
                (long) receiver.getId(),
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
    @Transactional
    public BlogVoteResponse addBlogVote(int userId, int blogId, BlogVoteRequest blogVoteRequest) {
        User user = userRepository.findById((long) userId).get();
        BlogVoteID blogVoteID = new BlogVoteID(user, new Blog(blogId));

        BlogVote blogVote = blogVoteRepository.findById(blogVoteID);
        if(blogVote != null) {
            throw new GlobalException("Vote already exists - blog id: " + blogId + ", user id: " + userId);
        }
        if(blogVoteRequest.getVote() == null){
            throw new GlobalException("Vote is not found.");
        }

        blogVote = new BlogVote(
                blogVoteID,
                blogVoteRequest.vote
        );
        blogVoteRepository.save(blogVote);

        blogService.updateBlogVoteCount(blogVote);
        sendNotification(blogVote);
        return toResponse(blogVote);
    }

    @Override
    @Transactional
    public BlogVoteResponse updateBlogVote(int userId, int blogId, BlogVoteRequest blogVoteRequest) {
        BlogVoteID blogVoteID = new BlogVoteID(new User(userId), new Blog(blogId));

        BlogVote blogVote = blogVoteRepository.findById(blogVoteID);
        if(blogVote == null) {
            throw new GlobalException("Vote not found - blog id: " + blogId + ", user id: " + userId);
        }
        else if(blogVote.getType() == blogVoteRequest.vote) {
            throw new GlobalException("Vote already exists - blog id: " + blogId + ", user id: " + userId);
        }

        blogVote.setType(blogVoteRequest.vote);
        blogVoteRepository.save(blogVote);

        blogService.updateBlogVoteCount(blogVote);
        blogService.updateBlogVoteCount(blogVote);
        return toResponse(blogVote);
    }

    @Override
    public void deleteBlogVote(BlogVoteID vote) {
        blogVoteRepository.deleteById(vote);
    }



    // -------- Comment Vote Section --------

    private void sendNotification(CommentVote commentVote){

        Comment comment = commentRepository.findById(commentVote.getId().getComment().getId());

        User actor = userRepository.findById((long) commentVote.getId().getUser().getId()).get();
        User receiver = userRepository.findById((long) comment.getUser().getId()).get();
        Notification notification = new Notification(
                null,
                receiver,
                actor,
                NotificationType.COMMENT_VOTED,
                (long) receiver.getId(),
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

    protected void updateCommentVoteCount (CommentVote commentVote) {
        Comment comment = commentRepository.findById(commentVote.getId().getComment().getId());
        System.out.println(comment);
        if(commentVote.getType() == Vote.up)
            comment.setVotes(comment.getVotes() + 1);
        else
            comment.setVotes(comment.getVotes() - 1);
        commentRepository.save(comment);
    }

    @Override
    @Transactional
    public CommentVoteResponse addCommentVote(int userId, int commentId, CommentVoteRequest commentVoteRequest) {
        User user = userRepository.findById((long) userId).get();
        CommentVoteID commentVoteID = new CommentVoteID(user, new Comment(commentId));

        CommentVote commentVote = commentVoteRepository.findById(commentVoteID);
        if(commentVote != null) {
            throw new GlobalException("Vote already exists - commentId: " + commentId + ", user id: " + userId);
        }

        commentVote = new CommentVote();
        commentVote.setId(commentVoteID);
        commentVote.setType(commentVoteRequest.vote);
        commentVoteRepository.save(commentVote);

        updateCommentVoteCount(commentVote);
        sendNotification(commentVote);
        return toCommentResponse(commentVote);
    }

    @Override
    @Transactional
    public CommentVoteResponse updateCommentVote(int userId, int commentId, CommentVoteRequest commentVoteRequest) {
        User user = userRepository.findById((long) userId).get();
        CommentVoteID commentVoteID = new CommentVoteID(user, new Comment(commentId));

        CommentVote commentVote = commentVoteRepository.findById(commentVoteID);
        if(commentVote == null) {
            throw new GlobalException("Vote not found - commentId: " + commentId + ", user id: " + userId);
        }
        else if(commentVote.getType() == commentVoteRequest.vote) {
            throw new GlobalException("Vote already exists - commentId: " + commentId + ", user id: " + userId);
        }

        commentVote.setType(commentVoteRequest.vote);
        commentVoteRepository.save(commentVote);

        updateCommentVoteCount(commentVote);
        updateCommentVoteCount(commentVote);
        return toCommentResponse(commentVote);
    }
}
