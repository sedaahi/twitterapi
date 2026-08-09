package com.workintech.twitterapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username boş olamaz.")
    @Size(min = 3, max = 30, message = "Username 3-30 karakter arasında olmalıdır.")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Email boş olamaz.")
    @Email(message = "Geçerli bir email adresi giriniz.")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Password boş olamaz.")
    @Size(min = 6, message = "Password en az 6 karakter olmalıdır.")
    @Column(nullable = false)
    private String password;
}



/*

                    ┌─────────────┐
                    │    USER     │
                    │-------------│
                    │ id          │
                    │ username    │
                    │ email       │
                    │ password    │
                    └──────┬──────┘
                           │
           ┌───────────────┼──────────────┐
           │               │              │
           ▼               ▼              ▼
        TWEET           COMMENT          LIKE
           │               │              │
           │               │              │
           ├───────────────┘              │
           │                              │
           ├──────────────────────────────┘
           │
           ▼
        RETWEET

 */