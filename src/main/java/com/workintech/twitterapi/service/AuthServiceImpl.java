package com.workintech.twitterapi.service;

import com.workintech.twitterapi.dto.request.LoginRequest;
import com.workintech.twitterapi.dto.request.RegisterRequest;
import com.workintech.twitterapi.dto.response.AuthResponse;
import com.workintech.twitterapi.dto.response.UserResponse;
import com.workintech.twitterapi.entity.User;
import com.workintech.twitterapi.exception.TwitterException;
import com.workintech.twitterapi.repository.UserRepository;
import com.workintech.twitterapi.security.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    /**
     * RegisterRequest
     *       ↓
     * Email daha önce kullanılmış mı?
     *       ↓
     * Username kullanılmış mı?
     *       ↓
     * Password BCrypt ile encode edilir
     *       ↓
     * User kaydedilir
     *       ↓
     * UserResponse döner
     */
    @Override
    public UserResponse register(RegisterRequest request) {

        if (userRepository.existsByEmail(request.email())) {
            throw new TwitterException(
                    "Bu email adresi zaten kullanılıyor.",
                    HttpStatus.BAD_REQUEST
            );
        }

        if (userRepository.existsByUsername(request.username())) {
            throw new TwitterException(
                    "Bu kullanıcı adı zaten kullanılıyor.",
                    HttpStatus.BAD_REQUEST
            );
        }

        User user = new User();

        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(
                passwordEncoder.encode(request.password())
        );

        User savedUser = userRepository.save(user);

        return new UserResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail()
        );
    }

    @Override
    public AuthResponse login(LoginRequest request) {

        //Bu email ve password doğru mu?
        //CustomUserDetailsService->findByEmail->DB'deki hash password->PasswordEncoder karşılatır==>
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() ->
                        new TwitterException(
                                "Kullanıcı bulunamadı.",
                                HttpStatus.NOT_FOUND
                        )
                );

        //=>Doğruysa devam ediyoruz ve JWT üretiyoruz

        String token =
                jwtTokenProvider.generateToken(user.getEmail());

        UserResponse userResponse = new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail()
        );

        return new AuthResponse(
                token,
                "Bearer",
                userResponse
        );
    }
}
