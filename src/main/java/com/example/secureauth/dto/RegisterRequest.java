//package com.example.secureauth.dto;
//
//public record RegisterRequest(
//        String name,
//        String email,
//        String password
//) {
//}


//this is our dto after validation
package com.example.secureauth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank
        @Size(max = 100)
        String name,

        @NotBlank
        @Email
        @Size(max = 254)
        String email,

        @NotBlank
        @Size(min = 15, max = 64)
        String password
) {
}