package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.request.LoginRequest;
import com.workintech.twitterapi.dto.request.RegisterRequest;
import com.workintech.twitterapi.dto.response.AuthResponse;
import com.workintech.twitterapi.dto.response.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}