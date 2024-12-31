package com.adplatform.module.user.dto;

import lombok.Data;
import lombok.Builder;

@Data
@Builder
public class UserSecuritySettingDTO {
    private Long userId;
    private Boolean twoFactorEnabled;
    private String twoFactorType;  // SMS, EMAIL, AUTHENTICATOR
    private Boolean loginNotificationEnabled;
    private Boolean passwordChangeNotificationEnabled;
    private Integer passwordExpiryDays;
    private Boolean ipRestrictionEnabled;
    private String allowedIpAddresses;
} 