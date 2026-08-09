package com.workintech.twitterapi.exception;

import java.time.LocalDateTime;

public record ApiError(
        int status,
        String message,
        LocalDateTime timestamp
) {
}
/*
{
  "status": 404,
  "message": "Tweet bulunamadı.",
  "timestamp": "2026-08-09T10:50:00"
}
 */