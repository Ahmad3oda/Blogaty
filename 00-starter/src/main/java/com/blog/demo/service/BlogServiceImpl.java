package com.blog.demo.service;

import com.blog.demo.CacheService;
import com.blog.demo.dto.BlogRequest;
import com.blog.demo.dto.BlogResponse;
import com.blog.demo.repository.BlogRepository;
import com.blog.demo.entity.Blog;
import com.blog.demo.exception.GlobalException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BlogServiceImpl implements BlogService {

    CacheService cache;
    ObjectMapper objectMapper;
    private final BlogRepository blogRepository;

    @Autowired
    public BlogServiceImpl(CacheService cache, BlogRepository blogRepository, ObjectMapper objectMapper) {
        this.cache = cache;
        this.blogRepository = blogRepository;
        this.objectMapper = objectMapper;
    }

    protected BlogResponse toResponse(Blog blog) {
        BlogResponse blogResponse = objectMapper.convertValue(blog, BlogResponse.class);
        blogResponse.setUsername(cache.getUsername(blogResponse.getUserId()));
        return blogResponse;
    }

    protected List<BlogResponse> toResponse(List<Blog> blogs) {
        List<BlogResponse> blogResponses = new ArrayList<>();
        blogs.forEach(blog -> blogResponses.add(toResponse(blog)));
        return blogResponses;
    }


    @Override
    public List<BlogResponse> findAll() {
        List <Blog> blogs = blogRepository.findAll();
        return toResponse(blogs);
    }

    @Override
    public List<BlogResponse> findAllByUserId(int userId) {
        List <Blog> blogs = blogRepository.findAllByUserId(userId);
        return toResponse(blogs);
    }

    @Override
    public BlogResponse findByBlogId(int blogId) {
        Blog blog = blogRepository.findByBlogId(blogId);
        if (blog == null) {
            throw new GlobalException("Blog not found - id: " + blogId);
        }
        return toResponse(blog);
    }

    // returns blog class !blog response
    protected Blog __findByBlogId(int blogId) {
        Blog blog = blogRepository.findByBlogId(blogId);
        if (blog == null) {
            throw new GlobalException("Blog not found - id: " + blogId);
        }
        return blog;
    }

    @Override
    @Transactional
    public BlogResponse save(Blog blog) {
        return toResponse(blogRepository.save(blog));
    }

    @Override
    @Transactional
    public BlogResponse update(Map<String, Object> payload){
        Blog dbBlog = __findByBlogId((int) payload.get("blogId"));

        // match payload to blogRequest, removing unwanted elements.
        BlogRequest blogRequest = objectMapper.convertValue(payload, BlogRequest.class);

        // blogId & content only remains to map
        ObjectNode payloadNd = objectMapper.convertValue(blogRequest, ObjectNode.class);
        ObjectNode blog = objectMapper.convertValue(dbBlog, ObjectNode.class);

        blog.setAll(payloadNd);
        dbBlog = objectMapper.convertValue(blog, Blog.class);
        return toResponse(blogRepository.save(dbBlog));
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        blogRepository.deleteByBlogId(id);
    }
}
