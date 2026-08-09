package com.workintech.twitterapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


/**Burada özellikle userId yok. Tweet sahibi JWT'den bulunacak. */
public record TweetCreateRequest(

        @NotBlank(message = "Tweet içeriği boş olamaz.")
        @Size(max = 280, message = "Tweet en fazla 280 karakter olabilir.")
        String content

) {
}