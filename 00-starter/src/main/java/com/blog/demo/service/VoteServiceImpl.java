package com.blog.demo.service;

import com.blog.demo.CacheService;
import com.blog.demo.dto.BlogVoteRequest;
import com.blog.demo.dto.BlogVoteResponse;
import com.blog.demo.entity.BlogVoteID;
import com.blog.demo.repository.BlogVoteRepository;
import com.blog.demo.repository.CommentVoteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoteServiceImpl implements VoteService {

    CacheService cache;
    ObjectMapper objectMapper;
    BlogVoteRepository blogVoteRepository;
    CommentVoteRepository commentVoteRepository;


    @Override
    public List<BlogVoteResponse> findAllBlogVotesByBlogId(Long blogId) {
        return List.of();
    }

    @Override
    public BlogVoteResponse addBlogVote(BlogVoteRequest blogVoteRequest) {
        return null;
    }

    @Override
    public BlogVoteResponse updateBlogVote(BlogVoteRequest blogVoteRequest) {
        return null;
    }

    @Override
    public void deleteBlogVote(BlogVoteID vote) {

    }
}
