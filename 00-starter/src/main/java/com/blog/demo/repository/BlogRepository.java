package com.blog.demo.repository;

import com.blog.demo.entity.Blog;
import lombok.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    @Query("SELECT b FROM Blog b ORDER BY b.votes DESC")
    List<Blog> findAllBlogs(Pageable pageable);
    List<Blog> findAllByUserId(int userId);
    Blog findByBlogId(int blogId);
    void deleteByBlogId(int id);

    @Query("SELECT b FROM Blog b WHERE b.content LIKE CONCAT('%', :content, '%')")
    List<Blog> findByContent(String content, PageRequest of);
}
