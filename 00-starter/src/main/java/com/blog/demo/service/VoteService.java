package com.blog.demo.service;

import com.blog.demo.dto.BlogVoteRequest;
import com.blog.demo.dto.BlogVoteResponse;
import com.blog.demo.entity.BlogVoteID;
import com.blog.demo.entity.Vote;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface VoteService {
    List<BlogVoteResponse> findAllBlogVotesByBlogId(Long blogId);
    BlogVoteResponse addBlogVote(BlogVoteRequest blogVoteRequest);
    BlogVoteResponse updateBlogVote(BlogVoteRequest blogVoteRequest);
    void deleteBlogVote(BlogVoteID vote);

}
