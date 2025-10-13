package com.blog.demo.controller;

import com.blog.demo.dto.BlogResponse;
import com.blog.demo.entity.Blog;
import com.blog.demo.service.BlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/blogs")
public class BlogController {

    BlogService blogService;

    @Autowired
    public BlogController(BlogService blogService) {
        this.blogService = blogService;
    }

    @GetMapping
    public List<BlogResponse> getAllBlogs() {
        return blogService.findAll();
    }

    @GetMapping("/user/{userId}")
    public List<BlogResponse> getBlogsByUserId(@PathVariable int userId) {
        return blogService.findAllByUserId(userId);
    }

    @GetMapping("/{blogId}")
    public BlogResponse getBlogById(@PathVariable int blogId) {
        return blogService.findByBlogId(blogId);
    }

    @PostMapping("user/{userId}")
    public BlogResponse createBlog(@PathVariable int userId, @RequestBody Blog blog) {
        blog.setUserId(userId);
        blog.setDate(LocalDateTime.now());
        return blogService.save(blog);
    }

    @PatchMapping("/{blogId}")
    public BlogResponse updateBlog(@PathVariable int blogId, @RequestBody Map<String, Object> payload) {
        payload.put("blogId", blogId);
        payload.put("date", LocalDateTime.now());
        return blogService.update(payload);
    }

    @DeleteMapping("/{blogId}")
    public void deleteBlog(@PathVariable int blogId) {
        blogService.deleteById(blogId);
    }
}
