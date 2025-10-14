package com.blog.demo.repository;

import com.blog.demo.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    @Query("select b.id.blogId from Bookmark b where b.id.userId = :userId")
    List<Long> findBlogIdByUserId(@Param("userId") int userId);

    Bookmark findById_UserIdAndId_BlogId(int userId, int blogId);
}
