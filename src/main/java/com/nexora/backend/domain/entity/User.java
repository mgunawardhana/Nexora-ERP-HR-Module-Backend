package com.nexora.backend.domain.entity;

import com.nexora.backend.domain.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public class User implements UserDetails {

    @Id
    @Column(name = "id")
    private String id; // Changed from Integer to String, removed @GeneratedValue

    @NotBlank(message = "First name is mandatory")
    private String firstName;

    @NotBlank(message = "Last name is mandatory")
    private String lastName;

    @NotBlank(message = "Email is mandatory")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password is mandatory")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotNull(message = "Role is mandatory")
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "user_profile_pic")
    private String userProfilePic;

    // Static counter to ensure sequential IDs within application session
    private static int idCounter = 0;

    @PrePersist
    protected void onCreate() {
        // Generate custom ID if not already set
        if (id == null) {
            generateCustomId();
        }
    }

    private void generateCustomId() {
        synchronized (User.class) {
            // Initialize counter from database on first use
            if (idCounter == 0) {
                initializeCounter();
            }

            // Increment and generate ID
            idCounter++;
            this.id = "EMPID-" + String.format("%03d", idCounter);
        }
    }

    private void initializeCounter() {
        // Try to get the maximum existing ID number from database
        try {
            // We'll start from a reasonable number if we can't access DB
            // In practice, you might want to initialize this differently
            idCounter = getLastIdNumberFromDatabase();
        } catch (Exception e) {
            // Fallback: start from 0, will become 1 after increment
            idCounter = 0;
        }
    }

    private int getLastIdNumberFromDatabase() {
        // Since we can't easily access EntityManager in @PrePersist,
        // we'll use a simple approach:
        // Return 0 so counter starts from 1
        // You could enhance this by reading from a properties file or cache
        return 0;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}