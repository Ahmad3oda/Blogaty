package com.blog.demo.service;

import com.blog.demo.dto.BlogRequest;
import com.blog.demo.dto.BlogResponse;
import com.blog.demo.entity.Blog;
import com.blog.demo.entity.BlogVote;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Service
public interface BlogService {
    List<BlogResponse> findAll(int page, int size);
    List<BlogResponse> findAllByUserId(int userId);
    BlogResponse findByBlogId(int blogId);
    BlogResponse save(int userId, BlogRequest blogRequest);
    BlogResponse update(Map<String, Object> payload);
    void deleteById(int id);

    BlogResponse decComment(int blogId);
    BlogResponse incComment(int blogId);
    BlogResponse updateBlogVoteCount(BlogVote blogVote);

    List<BlogResponse> findByContent(String searchTerm, int page, int size);
}
