package com.workintech.twitterapi.repository;

import com.workintech.twitterapi.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}