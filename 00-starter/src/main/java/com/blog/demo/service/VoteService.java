package com.blog.demo.service;

import com.blog.demo.dto.BlogVoteRequest;
import com.blog.demo.dto.BlogVoteResponse;
import com.blog.demo.dto.CommentVoteRequest;
import com.blog.demo.dto.CommentVoteResponse;
import com.blog.demo.entity.BlogVoteID;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VoteService {
    List<BlogVoteResponse> findAllBlogVotesByBlogId(Long blogId);
    BlogVoteResponse addBlogVote(int userId, int blogId, BlogVoteRequest blogVoteRequest);
    BlogVoteResponse updateBlogVote(int userId, int blogId, BlogVoteRequest blogVoteRequest);
    void deleteBlogVote(BlogVoteID vote);


    List<CommentVoteResponse> findAllCommentVotesByCommentId(Long blogId);
    CommentVoteResponse addCommentVote(int userId, int commentId, CommentVoteRequest commentVoteRequest);
    CommentVoteResponse updateCommentVote(int userId, int commentId, CommentVoteRequest commentVoteRequest);
//    void deleteBlogVote(BlogVoteID vote);
}
