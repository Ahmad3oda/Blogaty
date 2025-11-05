package com.blog.demo.repository;

import com.blog.demo.entity.Blog;
import com.blog.demo.entity.User;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findUsernameById(int id);

    @Query("SELECT u FROM User u WHERE u.username LIKE CONCAT('%', :content, '%')")
    List<User> findByUsername(String content, PageRequest of);
}
