package com.blog.demo.controller;

import com.blog.demo.dto.BookmarkResponse;
import com.blog.demo.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bookmarks")
public class BookmarkController {

    BookmarkService bookmarkService;

    @Autowired
    BookmarkController(BookmarkService bookmarkService) {
        this.bookmarkService = bookmarkService;
    }

    @GetMapping("/user/{userId}")
    public BookmarkResponse getUserBookmarks(@PathVariable int userId) {
        return bookmarkService.getBookmarksByUserId(userId);
    }

    @PostMapping("/{userId}/{blogId}")
    public void addBookmark(@PathVariable int userId, @PathVariable int blogId) {
        bookmarkService.addBookmark(userId, blogId);
    }

    @DeleteMapping("/{userId}/{blogId}")
    public void deleteBookmark(@PathVariable int userId, @PathVariable int blogId) {
        bookmarkService.deleteBookmark(userId, blogId);
    }
}
