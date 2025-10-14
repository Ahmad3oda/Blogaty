package com.blog.demo.service;

import com.blog.demo.dto.BookmarkResponse;
import org.springframework.stereotype.Service;

@Service
public interface BookmarkService {

    public BookmarkResponse getBookmarksByUserId(int userId);
    public void addBookmark(int userId, int blogId);
    public void deleteBookmark(int userId, int blogId);

}
