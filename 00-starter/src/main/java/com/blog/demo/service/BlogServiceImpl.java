package com.blog.demo.service;

import com.blog.demo.cache.RedisConfig;
import com.blog.demo.dto.BlogRequest;
import com.blog.demo.dto.BlogResponse;
import com.blog.demo.entity.BlogVote;
import com.blog.demo.entity.User;
import com.blog.demo.entity.Vote;
import com.blog.demo.repository.BlogRepository;
import com.blog.demo.entity.Blog;
import com.blog.demo.exception.GlobalException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class BlogServiceImpl implements BlogService {

    private final ObjectMapper objectMapper;
    private final BlogRepository blogRepository;

    protected BlogResponse toResponse(Blog blog) {
        return new BlogResponse(blog);
    }

    protected List<BlogResponse> toResponse(@NonNull List<Blog> blogs) {
        List<BlogResponse> blogResponses = new ArrayList<>();
        blogs.forEach(blog -> blogResponses.add(toResponse(blog)));
        return blogResponses;
    }


    @Override
    public List<BlogResponse> findAll(int page, int size) {
        return toResponse(blogRepository.findAllBlogs(PageRequest.of(page, size)));
    }

    @Override
    public List<BlogResponse> findAllByUserId(int userId) {
        List <Blog> blogs = blogRepository.findAllByUserId(userId);
        return toResponse(blogs);
    }

    public List<BlogResponse> findByContent(String content, int page, int size) {
        return toResponse(blogRepository.findByContent(content, PageRequest.of(page, size)));
    }

    @Override
    @Cacheable(value = "blogs", key = "#blogId")
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
    @CachePut(value = "blogs", key = "#result.blogId")
    public BlogResponse save(int userId, @NonNull BlogRequest blogRequest) {
        Blog blog = new Blog(
                new User(userId),
                blogRequest.getContent(),
                LocalDateTime.now(),
                0, 0
        );
        return toResponse(blogRepository.save(blog));
    }

    @Override
    @Transactional
    @CachePut(value = "blogs", key = "#result.blogId")
    public BlogResponse update(@NonNull Map<String, Object> payload){
        Blog dbBlog = __findByBlogId((int) payload.get("blogId"));

        BlogRequest blogRequest = objectMapper.convertValue(payload, BlogRequest.class);

        ObjectNode payloadNd = objectMapper.convertValue(blogRequest, ObjectNode.class);
        ObjectNode blog = objectMapper.convertValue(dbBlog, ObjectNode.class);

        blog.setAll(payloadNd);
        dbBlog = objectMapper.convertValue(blog, Blog.class);
        return toResponse(blogRepository.save(dbBlog));
    }

    @CachePut(value = "blogs", key = "#result.blogId")
    public BlogResponse incComment(int blogId) {
        Blog blog = blogRepository.findByBlogId(blogId);
        if (blog == null) {
            throw new GlobalException("Blog Not Found - id: " + blogId);
        }
        int comments = blog.getComments() + 1;
        blog.setComments(comments);

        return toResponse(blogRepository.save(blog));
    }

    @CachePut(value = "blogs", key = "#result.blogId")
    public BlogResponse decComment(int blogId) {
        Blog blog = blogRepository.findByBlogId(blogId);
        if (blog == null) {
            throw new GlobalException("Blog Not Found - id: " + blogId);
        }
        int comments = blog.getComments() - 1;
        blog.setComments(comments);
        return toResponse(blogRepository.save(blog));
    }

    @CachePut(value = "blogs", key = "#result.blogId")
    public BlogResponse updateBlogVoteCount (BlogVote blogVote) {
        Blog blog = blogRepository.findByBlogId(blogVote.getId().getBlog().getBlogId());
        if(blogVote.getType() == Vote.up)
            blog.setVotes(blog.getVotes() + 1);
        else
            blog.setVotes(blog.getVotes() - 1);

        return toResponse(blogRepository.save(blog));
    }

    @Override
    @Transactional
    @CacheEvict(value = "blogs", key = "#blogId")
    public void deleteById(int blogId) {
        blogRepository.deleteByBlogId(blogId);
    }
}
