package com.cfs.BookMyShow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    // @NotBlank = must be present AND not just spaces.
    // (@NotNull would let "   " through; @NotEmpty would let " " through. NotBlank is
    //  the right one for text a human types.)
    // The message is what the client sees, so write it for a human, not a developer.
    @NotBlank(message = "name is required")
    @Size(min = 2, max = 50, message = "name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "email is required")
    @Email(message = "email must be a valid email address")
    private String email;

    @NotBlank(message = "password is required")
    @Size(min = 6, message = "password must be at least 6 characters")
    private String password;

    // Optional field, so NO @NotBlank - but IF something is sent it must look like a
    // phone number. A regex of exactly 10 digits.
    @Pattern(regexp = "^$|^[0-9]{10}$", message = "phone must be 10 digits")
    private String phone;
}
