package com.blog.demo.service;

import com.blog.demo.CacheService;
import com.blog.demo.dto.BlogVoteRequest;
import com.blog.demo.dto.BlogVoteResponse;
import com.blog.demo.dto.CommentVoteRequest;
import com.blog.demo.dto.CommentVoteResponse;
import com.blog.demo.entity.*;
import com.blog.demo.exception.GlobalException;
import com.blog.demo.repository.BlogRepository;
import com.blog.demo.repository.BlogVoteRepository;
import com.blog.demo.repository.CommentRepository;
import com.blog.demo.repository.CommentVoteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VoteServiceImpl implements VoteService {

    CacheService cache;
    ObjectMapper objectMapper;
    BlogVoteRepository blogVoteRepository;
    BlogRepository blogRepository;
    CommentVoteRepository commentVoteRepository;
    CommentRepository commentRepository;

    @Autowired
    public VoteServiceImpl(CacheService cache,
                           ObjectMapper objectMapper,
                           BlogVoteRepository blogVoteRepository,
                           BlogRepository blogRepository,
                           CommentVoteRepository commentVoteRepository,
                           CommentRepository commentRepository) {
        this.cache = cache;
        this.objectMapper = objectMapper;
        this.blogVoteRepository = blogVoteRepository;
        this.blogRepository = blogRepository;
        this.commentVoteRepository = commentVoteRepository;
        this.commentRepository = commentRepository;
    }

    // -------- Blog Vote Section --------

    protected BlogVoteResponse toResponse(BlogVote blogVote) {
        BlogVoteResponse blogVoteResponse = new BlogVoteResponse();

        blogVoteResponse.setVote(blogVote.getType());
        blogVoteResponse.setUsername(cache.getUsername(blogVote.getId().getUserId()));
        return blogVoteResponse;
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

    protected void updateVoteCount (BlogVote blogVote) {
        Blog blog = blogRepository.findByBlogId(blogVote.getId().getBlogId());
        if(blogVote.getType() == Vote.up)
            blog.setVotes(blog.getVotes() + 1);
        else
            blog.setVotes(blog.getVotes() - 1);
        blogVoteRepository.save(blogVote);
    }

    @Override
    @Transactional
    public BlogVoteResponse addBlogVote(int userId, int blogId, BlogVoteRequest blogVoteRequest) {
        BlogVoteID blogVoteID = new BlogVoteID();
        blogVoteID.setUserId(userId);
        blogVoteID.setBlogId(blogId);

        BlogVote blogVote = blogVoteRepository.findById(blogVoteID);
        if(blogVote != null) {
            throw new GlobalException("Vote already exists - blog id: " + blogId + ", user id: " + userId);
        }

        blogVote = new BlogVote();
        blogVote.setId(blogVoteID);
        blogVote.setType(blogVoteRequest.vote);
        blogVoteRepository.save(blogVote);

        updateVoteCount(blogVote);
        return toResponse(blogVote);
    }

    @Override
    @Transactional
    public BlogVoteResponse updateBlogVote(int userId, int blogId, BlogVoteRequest blogVoteRequest) {
        BlogVoteID blogVoteID = new BlogVoteID();
        blogVoteID.setUserId(userId);
        blogVoteID.setBlogId(blogId);

        BlogVote blogVote = blogVoteRepository.findById(blogVoteID);
        if(blogVote == null) {
            throw new GlobalException("Vote not found - blog id: " + blogId + ", user id: " + userId);
        }
        else if(blogVote.getType() == blogVoteRequest.vote) {
            throw new GlobalException("Vote already exists - blog id: " + blogId + ", user id: " + userId);
        }

        blogVote.setType(blogVoteRequest.vote);
        blogVoteRepository.save(blogVote);

        updateVoteCount(blogVote);
        updateVoteCount(blogVote);
        return toResponse(blogVote);
    }

    @Override
    public void deleteBlogVote(BlogVoteID vote) {
        blogVoteRepository.deleteById(vote);
    }



    // -------- Comment Vote Section --------

    protected CommentVoteResponse toCommentResponse(CommentVote commentVote) {
        CommentVoteResponse commentVoteResponse = new CommentVoteResponse();

        commentVoteResponse.setVote(commentVote.getType());
        commentVoteResponse.setUsername(cache.getUsername(commentVote.getId().getUserId()));
        return commentVoteResponse;
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
        Comment comment = commentRepository.findById(commentVote.getId().getCommentId());
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
        CommentVoteID commentVoteID = new CommentVoteID();
        commentVoteID.setUserId(userId); commentVoteID.setCommentId(commentId);

        CommentVote commentVote = commentVoteRepository.findById(commentVoteID);
        if(commentVote != null) {
            throw new GlobalException("Vote already exists - commentId: " + commentId + ", user id: " + userId);
        }

        commentVote = new CommentVote();
        commentVote.setId(commentVoteID);
        commentVote.setType(commentVoteRequest.vote);
        commentVoteRepository.save(commentVote);

        updateCommentVoteCount(commentVote);
        return toCommentResponse(commentVote);
    }

    @Override
    @Transactional
    public CommentVoteResponse updateCommentVote(int userId, int commentId, CommentVoteRequest commentVoteRequest) {
        CommentVoteID commentVoteID = new CommentVoteID();
        commentVoteID.setUserId(userId); commentVoteID.setCommentId(commentId);

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
