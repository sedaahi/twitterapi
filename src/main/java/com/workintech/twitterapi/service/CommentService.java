package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.request.CommentRequest;
import com.workintech.twitterapi.dto.response.CommentResponse;

public interface CommentService {

    CommentResponse createComment(CommentRequest request, String email);

    CommentResponse updateComment(
            Long id,
            CommentRequest request,
            String email
    );

    void deleteComment(Long id, String email);
}