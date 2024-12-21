package com.adplatform.module.website.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 广告位测试控制器
 */
@Controller
@RequestMapping("/test")
public class AdSpaceTestController {
    
    /**
     * 访问广告位测试页面
     */
    @GetMapping("/adspace")
    public String adSpaceTest() {
        return "adSpaceTest";
    }
} 