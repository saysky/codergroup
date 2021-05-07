package com.liuyanzhao.forum.enums;

/**
 * @author 言曌
 * @date 2018/5/7 下午2:25
 */

public enum SiteTitleEnum {


    HOME_INDEX("首页", "CoderGroup - 开发者技术交流社区，简单而方便"),

    ARTICLE_HOME("文章首页", "最新文章 - CoderGroup"),
    QUESTION_HOME("问题首页", "最新问题 - CoderGroup"),
    bulletin_HOME("话题首页", "最新话题 - CoderGroup"),
    BLOG_DETAIL_PAGE("博客详情页", " - CoderGroup博客"),
    QUESTION_DETAIL_PAGE("求助详情页", " - CoderGroup问答频道"),
    DISCUSS_DETAIL_PAGE("讨论详情页", " - CoderGroup讨论区"),

    USER_HOME_PAGE("用户主页", "的主页 - CoderGroup"),


    LOGIN_PAGE("登录页面", "用户登录 - CoderGroup"),
    REGISTER_PAGE("注册页面", "用户注册 - CoderGroup"),
    REGISTER_SUCCESS_PAGE("注册成功页面", "注册成功，欢迎加入我们 - CoderGroup"),
    FORGET_PAGE("忘记密码页面", "找回密码 - CoderGroup"),
    RESET_PASSWORD_PAGE("重置密码页面", "重置密码 - CoderGroup"),
    RESET_PASSWORD_VERIFY_FAIL("重置密码邮件验证失败", "重置密码邮件验证失败 - CoderGroup"),

    //用户中心
    USER_SPACE("用户个人中心", "个人中心 - CoderGroup"),
    USER_SPACE_AVATAR("用户头像", "个人头像 - CoderGroup"),
    USER_SPACE_PROFILE("用户资料", "个人基本资料 - CoderGroup"),
    USER_SPACE_SECURITY("账号安全", "账号安全 - CoderGroup"),
    USER_SPACE_LOGIN_LOG("登录日志", "登录日志 - CoderGroup"),
    USER_SPACE_BINGINGS("第三方绑定", "第三方绑定 - CoderGroup"),

    RELATIONSHIP_MANAGE("关系管理", "关系管理 - CoderGroup"),
    MESSAGE_MANAGE("私信管理", "私信管理 - CoderGroup"),
    NOTICE_MANAGE("通知管理", "通知管理 - CoderGroup"),
    REPLY_MANAGE("回复管理", "回复管理 - CoderGroup"),
    USER_SETTINGS("用户设置", "用户设置 - CoderGroup"),
    COMMENT_MANAGE("评论管理", "评论管理 - CoderGroup"),

    EMAIL_VERIFY_SUCCESS("邮箱验证成功", "邮箱验证成功 - CoderGroup"),
    EMAIL_VERIFY_FAIL("邮箱验证失败", "邮箱验证失败 - CoderGroup"),

    //文章管理
    BLOG_MANAGE("文章管理", "文章管理 - CoderGroup"),
    BLOG_EDIT("修改文章", "文章编辑 - CoderGroup"),
    BLOG_CREATE("写文章", "文章撰写 - CoderGroup"),
    CATEGORY_MANAGE("分类管理", "分类管理 - CoderGroup"),
    BLOG_REPLY_MANAGE("评论管理", "文章评论管理 - CoderGroup"),


    //收藏管理
    BOOKMARK_MANAGE("收藏管理", "收藏管理 - CoderGroup"),

    //问答管理
    QUESTION_CREATE("提出问题", "提出问题 - CoderGroup"),
    QUESTION_EDIT("修改问题", "修改问题 - CoderGroup"),
    MY_ANSWER_MANAGE("我的回答管理", "我的回答管理 - CoderGroup"),
    MY_QUESTION_MANAGE("我的问题管理", "我的问题管理 - CoderGroup"),
    QUESTION_MANAGE("问答管理", "问答管理 - CoderGroup"),
    QUESTION_REPLY_MANAGE("问答管理", "问答回复管理 - CoderGroup"),

    //讨论区管理
    bulletin_MANAGE("话题管理", "话题管理 - CoderGroup"),
    bulletin_CREATE("话题添加", "添加新话题 - CoderGroup"),
    bulletin_EDIT("话题修改", "修改话题 - CoderGroup"),
    bulletin_DETAIL("话题详情页", " - CoderGroup讨论区"),


    //绑定第三方
    BIND_PAGE("绑定第三方账号", "绑定第三方账号 - CoderGroup"),


    //    后台
    ARTICLE_ADMIN("文章管理", "文章管理 - CoderGroup后台管理系统"),
    COMMENT_ADMIN("话题管理", "话题管理 - CoderGroup后台管理系统"),
    QUESTION_ADMIN("问题管理", "问题管理 - CoderGroup后台管理系统"),
    ANSWER_ADMIN("回答管理", "回答管理 - CoderGroup后台管理系统"),
    bulletin_ADMIN("话题管理", "话题管理 - CoderGroup后台管理系统"),
    TAG_ADMIN("标签管理", "标签管理 - CoderGroup后台管理系统"),
    USER_ADMIN("用户管理", "标签管理 - CoderGroup后台管理系统"),
    USER_LOGINLOG_ADMIN("用户登录日志管理", "标签管理 - CoderGroup后台管理系统"),
    NOTICE_ADMIN("公告栏管理", "标签管理 - CoderGroup后台管理系统"),

    ;


    private String description;
    private String title;

    SiteTitleEnum(String description, String title) {
        this.description = description;
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
