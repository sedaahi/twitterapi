package com.workintech.twitterapi.security;

import com.workintech.twitterapi.entity.User;
import com.workintech.twitterapi.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Spring Security, kullanıcıyı login sırasında bir UserDetailsService üzerinden bulur;
 * biz de bizim UserRepository ile bunu bağlayacağız.
 *
 * email
 *  ↓
 * CustomUserDetailsService
 *  ↓
 * UserRepository.findByEmail(email)
 *  ↓
 * bizim User entity
 *  ↓
 * Spring Security UserDetails
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Kullanıcı bulunamadı.")
                );

        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .authorities("USER")
                .build();
    }
}