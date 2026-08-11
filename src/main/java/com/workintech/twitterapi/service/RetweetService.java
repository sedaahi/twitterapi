package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.request.RetweetRequest;

public interface RetweetService {

    void retweet(RetweetRequest request, String email);

    void deleteRetweet(Long id, String email);
}