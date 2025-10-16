package com.blog.demo.repository;

import com.blog.demo.dto.BlogVoteResponse;
import com.blog.demo.entity.BlogVote;
import com.blog.demo.entity.BlogVoteID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogVoteRepository extends JpaRepository<BlogVote, Long> {

    @Query("SELECT s from BlogVote s where s.id.blog.blogId = :blogId")
    List<BlogVote> findAllByBlogId(@Param("blogId") Long blogId);

    BlogVote findById(BlogVoteID blogVoteID);

    void deleteById(BlogVoteID vote);
}
