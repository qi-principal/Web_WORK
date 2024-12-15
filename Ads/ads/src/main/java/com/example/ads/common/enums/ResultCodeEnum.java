package com.example.ads.common.enums;

public enum ResultCodeEnum {
    SUCCESS("200", "操作成功，一切顺利！"),

    PARAM_ERROR("400", "参数有点调皮，请检查一下哦~"),
    TOKEN_INVALID_ERROR("401", "无效的token，好像通行证过期了呢！"),
    TOKEN_CHECK_ERROR("401", "token验证失败，请重新登录，再试一次吧！"),
    PARAM_LOST_ERROR("4001", "参数失踪了，快找找看少了什么！"),

    SYSTEM_ERROR("500", "系统开小差了，稍后再试吧！"),
    USER_EXIST_ERROR("5001", "用户名已存在，换个名字试试？"),
    USER_NOT_LOGIN("5002", "用户未登录，快去登录吧！"),
    USER_ACCOUNT_ERROR("5003", "账号或密码好像不太对劲，再检查一下吧！"),
    USER_NOT_EXIST_ERROR("5004", "用户不存在，是不是记错了？"),
    PARAM_PASSWORD_ERROR("5005", "原密码输入错误，再想想看吧！"),
    COLLECT_ALREADY_ERROR("5006", "您已经收藏过该商品啦，不用重复收藏哦！"),
    GOODS_STOCK_ERROR("5007", "库存告急，快去催商家给您补货啦！"),
    GOODS_NOT_ENOUGH_ERROR("5008", "老板，快去补货吧！不然顾客要等不及了！");


    public String code;
    public String msg;

    ResultCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
