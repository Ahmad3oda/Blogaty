package com.blog.demo.service;
import com.blog.demo.dto.BlogRequest;
import com.blog.demo.dto.BlogResponse;
import com.blog.demo.entity.BlogVote;
import com.blog.demo.entity.User;
import com.blog.demo.entity.Vote;
import com.blog.demo.repository.BlogRepository;
import com.blog.demo.repository.BlogVoteRepository;
import com.blog.demo.entity.Blog;
import com.blog.demo.exception.GlobalException;
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

    private final BlogRepository blogRepository;
    private final BlogVoteRepository blogVoteRepository;

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
        List <Blog> blogs = blogRepository.findAllByUser_Id((long) userId);
        return toResponse(blogs);
    }

    public List<BlogResponse> findByContent(String content, int page, int size) {
        return toResponse(blogRepository.findByContent(content, PageRequest.of(page, size)));
    }

    @Override
    @Cacheable(value = "blogs", key = "#blogId")
    public BlogResponse findByBlogId(int blogId) {
        Blog blog = blogRepository.findByBlogId((long) blogId)
                .orElseThrow(() -> new GlobalException("Blog not found - id: " + blogId));
        return toResponse(blog);
    }

    // returns blog class !blog response
    protected Blog __findByBlogId(int blogId) {
        return blogRepository.findByBlogId((long) blogId)
                .orElseThrow(() -> new GlobalException("Blog not found - id: " + blogId));
    }

    @Override
    @Transactional
    @CachePut(value = "blogs", key = "#result.blogId")
    public BlogResponse save(int userId, @NonNull BlogRequest blogRequest) {
        Blog blog = new Blog(
                new User((long) userId),
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

        // Direct field updates to avoid ObjectMapper losing the JPA proxy
        if (payload.containsKey("content")) {
            dbBlog.setContent(String.valueOf(payload.get("content")));
        }
        dbBlog.setDate(LocalDateTime.now());
        return toResponse(blogRepository.save(dbBlog));
    }

    @Transactional
    @CachePut(value = "blogs", key = "#result.blogId")
    public BlogResponse incComment(int blogId) {
        Blog blog = blogRepository.findByBlogId((long) blogId)
                .orElseThrow(() -> new GlobalException("Blog Not Found - id: " + blogId));
        blog.setComments(blog.getComments() + 1);
        return toResponse(blogRepository.save(blog));
    }

    @Transactional
    @CachePut(value = "blogs", key = "#result.blogId")
    public BlogResponse decComment(int blogId) {
        Blog blog = blogRepository.findByBlogId((long) blogId)
                .orElseThrow(() -> new GlobalException("Blog Not Found - id: " + blogId));
        int comments = blog.getComments() - 1;
        blog.setComments(comments);
        return toResponse(blogRepository.save(blog));
    }

    @Override
    @Transactional
    @CachePut(value = "blogs", key = "#result.blogId")
    public BlogResponse recalculateVotes(int blogId) {
        Blog blog = blogRepository.findByBlogId((long) blogId)
                .orElseThrow(() -> new GlobalException("Blog Not Found - id: " + blogId));
        List<BlogVote> allVotes = blogVoteRepository.findAllByBlogId((long) blogId);
        int total = allVotes.stream().mapToInt(v -> v.getType() == Vote.up ? 1 : (v.getType() == Vote.down ? -1 : 0)).sum();
        blog.setVotes(total);
        return toResponse(blogRepository.save(blog));
    }

    @Override
    @Transactional
    @CachePut(value = "blogs", key = "#result.blogId")
    public BlogResponse updateBlogVoteCount (BlogVote blogVote) {
        return recalculateVotes(Math.toIntExact(blogVote.getId().getBlogId()));
    }

    @Override
    @Transactional
    @CacheEvict(value = "blogs", key = "#blogId")
    public void deleteById(int blogId) {
        blogRepository.deleteByBlogId((long) blogId);
    }
}
