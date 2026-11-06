package com.elibrary.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Entity
@Table(name = "user_accounts")
public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 80)
    private String username;

    @Column(name = "username_key", unique = true, length = 120)
    private String usernameKey;

    @Email
    @Column(unique = true, length = 254)
    private String email;

    @Column(nullable = false)
    private String password;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "membership_type", nullable = false, length = 20,
            columnDefinition = "varchar(20) default 'STANDARD'")
    private MembershipType membershipType = MembershipType.STANDARD;

    @Column(nullable = false)
    private boolean enabled = true;

    protected UserAccount() {
    }

    public UserAccount(String username, String password, String displayName, Role role) {
        this(username, null, password, displayName, role);
    }

    public UserAccount(String username, String email, String password, String displayName, Role role) {
        this.username = username;
        this.usernameKey = usernameKeyFor(username);
        this.email = email;
        this.password = password;
        this.displayName = displayName;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public void setUsername(String username) {
        this.username = username;
        this.usernameKey = usernameKeyFor(username);
    }
    public String getUsernameKey() { return usernameKey; }
    public void setUsernameKey(String usernameKey) { this.usernameKey = usernameKey; }
    public static String usernameKeyFor(String username) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(username.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable.", exception);
        }
    }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public MembershipType getMembershipType() { return membershipType; }
    public void setMembershipType(MembershipType membershipType) { this.membershipType = membershipType; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}
