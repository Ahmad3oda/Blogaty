package com.blog.demo.repository;

import com.blog.demo.entity.BlogVote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogVoteRepository extends JpaRepository<BlogVote, Long> {
}
