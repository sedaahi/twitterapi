package com.workintech.twitterapi.repository;

import com.workintech.twitterapi.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // Bir tweet'e ait yorumları eski -> yeni sıralı getirir.
    List<Comment> findByTweetIdOrderByCreatedAtAsc(Long tweetId);
    long countByTweetId(Long tweetId);
}