package com.dami.expensetracker.models;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {
    @NotEmpty
    @Email
    private String email;

    @NotEmpty
    private String username;

    @Size(min = 8, max = 20, message = "Password must be between 8 and 20 characters")
    private String password;

    private String confirmPassword;
}
