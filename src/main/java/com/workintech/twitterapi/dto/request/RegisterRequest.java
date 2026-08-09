package com.workintech.twitterapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "Username boş olamaz.")
        @Size(min = 3, max = 30, message = "Username 3-30 karakter arasında olmalıdır.")
        String username,

        @NotBlank(message = "Email boş olamaz.")
        @Email(message = "Geçerli bir email adresi giriniz.")
        String email,

        @NotBlank(message = "Password boş olamaz.")
        @Size(min = 6, message = "Password en az 6 karakter olmalıdır.")
        String password

) {
}