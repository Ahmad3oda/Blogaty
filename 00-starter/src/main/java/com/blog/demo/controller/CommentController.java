package com.blog.demo.controller;

import com.blog.demo.dto.CommentRequest;
import com.blog.demo.dto.CommentResponse;
import com.blog.demo.entity.Comment;
import com.blog.demo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comments")
public class CommentController {

    CommentService commentService;
    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/{commentId}")
    public CommentResponse getComment(@PathVariable int commentId) {
        return commentService.getByCommentId(commentId);
    }

    @GetMapping("/blog/{blogId}")
    public List<CommentResponse> getCommentByBlog(@PathVariable int blogId) {
        return commentService.getCommentsByBlogId(blogId);
    }

    @PostMapping("/{userId}/{blogId}")
    public CommentResponse addComment(@PathVariable int userId,
                                      @PathVariable int blogId,
                                      @RequestBody CommentRequest comment) {
        return commentService.add(userId, blogId, comment);
    }

    @PatchMapping("/{commentId}")
    public CommentResponse updateComment(@PathVariable int commentId,
                                         @RequestBody Map<String, Object> payload) {
        payload.put("commentId", commentId);
        return commentService.update(payload);
    }

    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable int commentId) {
        commentService.deleteByCommentId(commentId);
    }
}
