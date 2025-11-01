package com.blog.demo.repository;

import com.blog.demo.entity.Comment;
import com.blog.demo.entity.CommentVote;
import com.blog.demo.entity.CommentVoteID;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    Comment findById(int commentId);
    List<Comment> findAllByBlog_BlogId(int blogId);

    @Query("SELECT c FROM Comment c WHERE c.blog.blogId = :blogId ORDER BY c.date DESC")
    List<Comment> findTopCommentsByBlogId(@Param("blogId") int blogId, Pageable pageable);

}
