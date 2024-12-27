package com.adserver.repository;

import com.adserver.model.Whitelist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface WhitelistRepository extends JpaRepository<Whitelist, Long> {
    
    // 查找指定域名且状态为激活的白名单记录
    Optional<Whitelist> findByDomainAndActiveTrue(String domain);
    
    // 查找所有激活状态的白名单记录
    List<Whitelist> findByActiveTrue();
    
    // 查找指定域名的白名单记录（不考虑状态）
    Optional<Whitelist> findByDomain(String domain);
    
    // 检查域名是否存在
    boolean existsByDomain(String domain);
    
    // 根据域名删除白名单记录
    void deleteByDomain(String domain);
} 