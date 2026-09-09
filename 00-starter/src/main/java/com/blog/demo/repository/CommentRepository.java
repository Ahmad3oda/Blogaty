package com.blog.demo.repository;

import com.blog.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Optional<Comment> findCommentById(Long commentId);
    List<Comment> findAllByBlog_BlogId(Long blogId);

    @Query("SELECT c FROM Comment c WHERE c.blog.blogId = :blogId ORDER BY c.date DESC")
    List<Comment> findTopCommentsByBlogId(@Param("blogId") Long blogId, Pageable pageable);

}
