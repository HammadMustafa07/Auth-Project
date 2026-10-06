//package com.example.secureauth.dto;
//
//public record LoginRequest(
//        String email,
//        String password
//) {
//}


//this is our dto after validation

package com.example.secureauth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank
        @Email
        String email,

        @NotBlank
        String password
) {
}