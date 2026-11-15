package com.cfs.BookMyShow.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

  @NotBlank(message = "email is required")
  @Email(message = "email must be a valid email address")
  private String email;

  // Deliberately NO @Size here, unlike UserRequest. A login form must not tell an
  // attacker "passwords are at least 6 characters" - that is free information.
  // We just need to know something was typed.
  @NotBlank(message = "password is required")
  private String password;

}
