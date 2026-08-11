package com.workintech.twitterapi.repository;

import com.workintech.twitterapi.entity.User;
import com.workintech.twitterapi.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class UserRepositoryTest {

    private UserRepository userRepository;

    private User testUser;

    @Autowired
    public UserRepositoryTest(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Her testten önce test kullanıcısı oluşturulur.
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("123456");

        testUser = userRepository.save(testUser);
    }

    // Test bitince oluşturduğumuz veriyi temizliyoruz.
    @AfterEach
    void tearDown() {
        userRepository.delete(testUser);
    }

    @DisplayName("Can find user by email")
    @Test
    void findByEmail() {

        Optional<User> foundUser =
                userRepository.findByEmail("test@example.com");

        assertTrue(foundUser.isPresent());
        assertEquals(
                "testuser",
                foundUser.get().getUsername()
        );
    }

    @DisplayName("Can check if email exists")
    @Test
    void existsByEmail() {

        boolean exists =
                userRepository.existsByEmail("test@example.com");

        assertTrue(exists);
    }
}