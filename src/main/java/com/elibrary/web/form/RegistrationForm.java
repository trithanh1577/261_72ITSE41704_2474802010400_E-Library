package com.elibrary.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.nio.charset.StandardCharsets;

public class RegistrationForm {

    @NotBlank
    @Size(min = 3, max = 80)
    @Pattern(regexp = "[A-Za-z0-9._-]+", message = "Use letters, numbers, dots, underscores or hyphens only.")
    private String username;

    @NotBlank
    @Email
    @Size(max = 254)
    private String email;

    @NotBlank
    @Size(max = 120)
    private String displayName;

    @NotBlank
    @Size(max = 72, message = "Password must not exceed BCrypt's 72-byte input limit.")
    private String password;

    @NotBlank
    private String confirmPassword;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    @AssertTrue(message = "Password must not exceed BCrypt's 72-byte UTF-8 input limit.")
    public boolean isPasswordWithinBcryptLimit() {
        return password == null || password.getBytes(StandardCharsets.UTF_8).length <= 72;
    }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
