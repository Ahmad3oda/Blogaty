package com.blog.demo.repository;

import com.blog.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment findById(int commentId);
    List<Comment> findAllByBlog_BlogId(int blogId);
    Integer findBlogIdById(int commentId);
}
