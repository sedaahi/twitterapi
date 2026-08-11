package com.workintech.twitterapi.repository;

import com.workintech.twitterapi.entity.Tweet;
import com.workintech.twitterapi.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
class TweetRepositoryTest {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    private User testUser;

    @Autowired
    public TweetRepositoryTest(
            TweetRepository tweetRepository,
            UserRepository userRepository
    ) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    // Her testten önce kullanıcı ve ona ait bir tweet oluşturuyoruz.
    @BeforeEach
    void setUp() {

        testUser = new User();
        testUser.setUsername("tweettestuser");
        testUser.setEmail("tweettest@example.com");
        testUser.setPassword("123456");

        testUser = userRepository.save(testUser);

        Tweet tweet = new Tweet();
        tweet.setContent("Repository test tweeti");
        tweet.setUser(testUser);

        tweetRepository.save(tweet);
    }

    // Test verilerini temizliyoruz.
    @AfterEach
    void tearDown() {
        tweetRepository.deleteAll(
                tweetRepository.findByUserId(testUser.getId())
        );

        userRepository.delete(testUser);
    }

    @DisplayName("Can find tweets by user id")
    @Test
    void findByUserId() {

        List<Tweet> tweets =
                tweetRepository.findByUserId(testUser.getId());

        // Kullanıcıya ait tweet bulundu mu?
        assertFalse(tweets.isEmpty());

        // Gelen tweet beklediğimiz içerikte mi?
        assertEquals(
                "Repository test tweeti",
                tweets.get(0).getContent()
        );
    }
}