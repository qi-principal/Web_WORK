package com.adserver.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "ads")
public class Ad {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long adId;
    
    private String adName;
    
    @Column(columnDefinition = "TEXT")
    private String adDescribe;
    
    private String adUrl;
    
    private String picUrl;
} 