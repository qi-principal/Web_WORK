package com.adserver.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "whitelist")
public class Whitelist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String domain;
    
    private boolean active = true;
} 