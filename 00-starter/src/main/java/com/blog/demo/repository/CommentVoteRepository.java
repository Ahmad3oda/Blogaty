package com.blog.demo.repository;

import com.blog.demo.entity.CommentVote;
import com.blog.demo.entity.CommentVoteID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentVoteRepository extends JpaRepository<CommentVote, Long> {

    @Query("SELECT s from CommentVote s where s.id.commentId = :commentId")
    List<CommentVote> findAllByCommentId(@Param("commentId") Long commentId);

    CommentVote findById(CommentVoteID commentVoteID);
}
