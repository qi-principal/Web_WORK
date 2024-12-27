package com.adserver.service;

import com.adserver.model.Whitelist;
import com.adserver.repository.WhitelistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WhitelistService {
    @Autowired
    private WhitelistRepository whitelistRepository;
    
    public boolean isDomainAllowed(String domain) {
        return whitelistRepository.findByDomainAndActiveTrue(domain).isPresent();
    }
    
    public Whitelist addDomain(String domain) {
        Whitelist whitelist = new Whitelist();
        whitelist.setDomain(domain);
        return whitelistRepository.save(whitelist);
    }
} 