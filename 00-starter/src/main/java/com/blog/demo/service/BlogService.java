package com.blog.demo.service;

import com.blog.demo.dto.BlogRequest;
import com.blog.demo.dto.BlogResponse;
import com.blog.demo.entity.Blog;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

@Service
public interface BlogService {
    List<BlogResponse> findAll();
    List<BlogResponse> findAllByUserId(int userId);
    BlogResponse findByBlogId(int blogId);
    BlogResponse save(int userId, BlogRequest blogRequest);
    BlogResponse update(Map<String, Object> payload);
    void deleteById(int id);

}
