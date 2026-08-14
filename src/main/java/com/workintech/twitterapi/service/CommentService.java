package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.request.CommentRequest;
import com.workintech.twitterapi.dto.response.CommentResponse;

import java.util.List;

public interface CommentService {

    CommentResponse createComment(
            CommentRequest request,
            String email
    );

    List<CommentResponse> findByTweetId(Long tweetId);

    List<CommentResponse> findByUserId(Long userId);

    CommentResponse updateComment(
            Long id,
            CommentRequest request,
            String email
    );

    void deleteComment(Long id, String email);
}