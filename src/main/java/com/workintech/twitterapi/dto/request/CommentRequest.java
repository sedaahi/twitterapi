package com.workintech.twitterapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Yorum oluşturmak için hem hangi tweet'e yazıldığını hem de içeriği bilmemiz gerekiyor:
 * Yorum yapan kullanıcının userId'sini yine istemiyoruz. JWT'den gelecek.*/
public record CommentRequest(

        @NotNull(message = "Tweet id boş olamaz.")
        Long tweetId,

        @NotBlank(message = "Yorum boş olamaz.")
        @Size(max = 280, message = "Yorum en fazla 280 karakter olabilir.")
        String content

) {
}