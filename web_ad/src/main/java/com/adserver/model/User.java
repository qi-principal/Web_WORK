package com.adserver.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    
    private String username;
    
    private String password;
    
    @Enumerated(EnumType.STRING)
    private AccountType accountType;
    
    public enum AccountType {
        STANDARD, ADMIN
    }
} 