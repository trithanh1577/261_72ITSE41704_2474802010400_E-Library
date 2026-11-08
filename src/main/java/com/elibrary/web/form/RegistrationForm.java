package com.elibrary.web.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.nio.charset.StandardCharsets;

public class RegistrationForm {

    @NotBlank(message = "{validation.required}")
    @Size(min = 3, max = 80, message = "{validation.username.size}")
    @Pattern(regexp = "[A-Za-z0-9._-]+", message = "{validation.username.pattern}")
    private String username;

    @NotBlank(message = "{validation.required}")
    @Email(message = "{validation.email.invalid}")
    @Size(max = 254, message = "{validation.email.max}")
    private String email;

    @NotBlank(message = "{validation.required}")
    @Size(max = 120, message = "{validation.displayName.max}")
    private String displayName;

    @NotBlank(message = "{validation.required}")
    @Size(max = 72, message = "{validation.password.bcryptLimit}")
    private String password;

    @NotBlank(message = "{validation.required}")
    private String confirmPassword;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    @AssertTrue(message = "{validation.password.bcryptLimit}")
    public boolean isPasswordWithinBcryptLimit() {
        return password == null || password.getBytes(StandardCharsets.UTF_8).length <= 72;
    }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
