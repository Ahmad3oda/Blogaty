package com.blog.demo.service;

import com.blog.demo.dto.CommentRequest;
import com.blog.demo.dto.CommentResponse;
import com.blog.demo.entity.Comment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface CommentService {
    CommentResponse getByCommentId(int commentId);
    List<CommentResponse> getCommentsByBlogId(int blogId);
    CommentResponse add(int userId, int blogId, CommentRequest comment);
    CommentResponse update(Map<String, Object> payload);
    void deleteByCommentId(int commentId);
}
