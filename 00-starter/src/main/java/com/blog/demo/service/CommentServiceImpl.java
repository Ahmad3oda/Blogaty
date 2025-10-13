package com.blog.demo.service;

import com.blog.demo.CacheService;
import com.blog.demo.dto.BlogResponse;
import com.blog.demo.dto.CommentRequest;
import com.blog.demo.dto.CommentResponse;
import com.blog.demo.entity.Blog;
import com.blog.demo.entity.Comment;
import com.blog.demo.exception.GlobalException;
import com.blog.demo.repository.BlogRepository;
import com.blog.demo.repository.CommentRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class CommentServiceImpl implements CommentService {


    CacheService cache;
    ObjectMapper objectMapper;
    private final CommentRepository commentRepository;
    private final BlogRepository blogRepository;

    @Autowired
    public CommentServiceImpl(CacheService cache,
                              ObjectMapper objectMapper,
                              CommentRepository commentRepository,
                              BlogRepository blogRepository) {
        this.cache = cache;
        this.commentRepository = commentRepository;
        this.objectMapper = objectMapper;
        this.blogRepository = blogRepository;
    }

    protected CommentResponse toResponse(Comment comment) {
        CommentResponse commentResponse = objectMapper.convertValue(comment, CommentResponse.class);
        commentResponse.setUsername(cache.getUsername(commentResponse.getUserId()));
        return commentResponse;
    }

    protected List<CommentResponse> toResponse(List<Comment> comments) {
        List<CommentResponse> commentResponses = new ArrayList<>();
        comments.forEach(comment -> commentResponses.add(toResponse(comment)));
        return commentResponses;
    }


    @Override
    public CommentResponse getByCommentId(int commentId) {
        Comment comment = commentRepository.findById(commentId);
        if (comment == null) {
            throw new GlobalException("Comment Not Found - id: " + commentId);
        }
        return toResponse(comment);
    }

    public Comment __getByCommentId(int commentId) {
        Comment comment = commentRepository.findById(commentId);
        if (comment == null) {
            throw new GlobalException("Comment Not Found - id: " + commentId);
        }
        return comment;
    }

    @Override
    public List<CommentResponse> getCommentsByBlogId(int blogId) {
        List<Comment> comments = commentRepository.findAllByBlogId(blogId);
        return toResponse(comments);
    }

    @Override
    public CommentResponse add(int userId, int blogId, CommentRequest comment) {
        Comment dbComment = objectMapper.convertValue(comment, Comment.class);
        dbComment.setUserId(userId);
        dbComment.setBlogId(blogId);
        dbComment.setDate(LocalDateTime.now());
        incComment(blogId);
        return toResponse(commentRepository.save(dbComment));
    }

    private void incComment(int blogId) {
        Blog blog = blogRepository.findByBlogId(blogId);
        if (blog == null) {
            throw new GlobalException("Blog Not Found - id: " + blogId);
        }

        int comments = blog.getComments() + 1;
        blog.setComments(comments);

        blogRepository.save(blog);
    }
    private void decComment(int blogId) {
        Blog blog = blogRepository.findByBlogId(blogId);
        if (blog == null) {
            throw new GlobalException("Blog Not Found - id: " + blogId);
        }

        int comments = blog.getComments() - 1;
        blog.setComments(comments);
        blogRepository.save(blog);
    }

    @Override
    @Transactional
    public CommentResponse update(Map<String, Object> payload) {
        Comment dbComment = __getByCommentId((int) payload.get("commentId"));

        CommentRequest commentRequest = objectMapper.convertValue(payload, CommentRequest.class);
        ObjectNode requestNode = objectMapper.convertValue(commentRequest, ObjectNode.class);
        ObjectNode responseNode = objectMapper.convertValue(dbComment, ObjectNode.class);

        responseNode.setAll(requestNode);
        dbComment = objectMapper.convertValue(responseNode, Comment.class);
        dbComment.setDate(LocalDateTime.now());
        return toResponse(commentRepository.save(dbComment));
    }

    @Override
    @Transactional
    public void deleteByCommentId(int commentId) {
        Comment comment = commentRepository.findById(commentId);
        decComment(comment.getBlogId());
        commentRepository.deleteById((long) commentId);
    }
}
