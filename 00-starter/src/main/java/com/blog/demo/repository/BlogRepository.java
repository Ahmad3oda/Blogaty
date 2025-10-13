package com.blog.demo.repository;

import com.blog.demo.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {
    List<Blog> findAllByUserId(int userId);
    Blog findByBlogId(int blogId);
    void deleteByBlogId(int id);
}
