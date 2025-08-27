package com.nexora.backend.domain.entity;

import com.nexora.backend.domain.enums.TokenType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity // This was a good addition from the last step!
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Be explicit here!
    public Integer id;

    @Column(unique = true)
    public String token;

    @Enumerated(EnumType.STRING)
    public TokenType tokenType = TokenType.BEARER;

    public boolean revoked;

    public boolean expired;

    // This defines the many-to-one relationship with the User entity
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // This links to the 'id' in the 'users' table
    public User user;
}