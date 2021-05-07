package com.liuyanzhao.forum.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;


import org.hibernate.validator.constraints.Email;

import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.URL;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author 言曌
 * @date 2018/3/19 下午9:54
 */

@Entity
@Table(name = "user")
public class User implements UserDetails, Serializable {

    private static final long serialVersionUID = 6147345506206285446L;

    @Id // 主键
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Integer id; // 用户的唯一标识

    @NotEmpty(message = "昵称不能为空")
    @Size(min = 2, max = 20, message = "昵称长度必须为2-20个字符")
    @Column(nullable = false, length = 20)
    private String nickname;

    @NotEmpty(message = "邮箱不能为空")
    @Size(max = 50, message = "邮箱长度最多50个字符")
    @Email(message = "邮箱格式不对")
    @Column(length = 50, unique = true)
    @JsonIgnore
    private String email;

    @NotEmpty(message = "用户名不能为空")
    @Size(min = 4, max = 20, message = "用户名长度必须为4-20个字符")
    @Column(nullable = false, length = 20, unique = true)
    private String username; // 用户账号，用户登录时的唯一标识

    @NotEmpty(message = "密码不能为空")
    @Size(max = 100, message = "密码长度最多100个字符")
    @Column(length = 100)
    @JsonIgnore
    private String password; // 登录时密码

    @Column(length = 200)
    @Size(max = 200, message = "头像链接长度最多200个字符")
    private String avatar; // 头像图片地址

    @org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
    @Column(name = "create_time")
    private Timestamp createTime;

    @org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
    @Column(name = "last_login_time")
    private Timestamp lastLoginTime;

    @Column(length = 1,name = "is_verify_email")
    private String isVerifyEmail = "N";//是否验证了Email

    @Column(name = "article_size")
    private Long articleSize = 0L; // 文章数

    @Column(name = "view_size")
    private Long viewSize = 1L; // 访问量

    @Column(name = "follow_size")
    private Long followSize = 0L;//关注数

    @Column(name = "fan_size")
    private Long fanSize = 0L; //粉丝数

    @Column(name = "question_size")
    private Long questionSize = 0L;//提问数

    @Column(name = "answer_size")
    private Long answerSize = 0L; //回答数

    @Column(length = 50)
    @URL(message = "URL不符合规范")
    @Size(max = 50, message = "个人主页链接长度最多50个字符")
    private String homepage;//个人主页

    @Size(max = 100,message = "联系方式最多不要超过100个字符")
    @Column(length = 100)
    private String contact;

    @Column(length = 50)
    @URL(message = "URL不符合规范")
    @Size(max = 50, message = "github链接长度最多50个字符")
    private String github;//github

    @Column(length = 1000)
    @Size(max = 1000, message = "个人资料长度最多1000个字符")
    private String profile;//用户说明

    @ManyToMany(cascade = CascadeType.DETACH, fetch = FetchType.LAZY)
    @JoinTable(name = "user_authority", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"))
    @JsonIgnore
    private List<Authority> authorities;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", referencedColumnName = "id")
    @JsonIgnore
    private Job job;

    @Transient
    private Integer isFriend = 0;//关系，0表示没有关系，1表示我已关注，2表示互相关注

    private Integer reputation = 10;//声望(积分)，刚注册有10积分

    @Pattern(regexp = "normal|banned|deleted")
    private String status = "normal";//状态：正常normal，禁止登陆banned,已删除deleted

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.REMOVE}, mappedBy = "user", fetch = FetchType.LAZY)
    private List<Bookmark> bookmarkList;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.REMOVE}, mappedBy = "user", fetch = FetchType.LAZY)
    private List<Article> articleList;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.REMOVE}, mappedBy = "user", fetch = FetchType.LAZY)
    private List<Comment> commentList;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.REMOVE}, mappedBy = "user", fetch = FetchType.LAZY)
    private List<LoginRecord> loginRecordList;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.REMOVE}, mappedBy = "user", fetch = FetchType.LAZY)
    private List<Zan> zanList;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.REMOVE}, mappedBy = "user", fetch = FetchType.LAZY)
    private List<Cai> caiList;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.REMOVE}, mappedBy = "user", fetch = FetchType.LAZY)
    private List<Category> categoryList;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.REMOVE}, mappedBy = "user", fetch = FetchType.LAZY)
    private List<Message> messageFromList;

    @JsonIgnore
    @OneToMany(cascade = {CascadeType.REMOVE}, mappedBy = "friend", fetch = FetchType.LAZY)
    private List<Message> messageToList;

    public User() { // JPA 的规范要求无参构造函数；设为 protected 防止直接使用
    }

//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        //  需将 List<Authority> 转成 List<SimpleGrantedAuthority>，否则前端拿不到角色列表名称
//        List<SimpleGrantedAuthority> simpleAuthorities = new ArrayList<>();
//        for (GrantedAuthority authority : this.authorities) {
//            simpleAuthorities.add(new SimpleGrantedAuthority(authority.getAuthority()));
//        }
//        return simpleAuthorities;
//    }


    @Override
    public List<Authority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(List<Authority> authorities) {
        this.authorities = authorities;
    }

    public List<Authority> getAuthorityList() {
        return authorities;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setEncodePassword(String password) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodePasswd = encoder.encode(password);
        this.password = encodePasswd;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }//帐号是否未过期

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }//帐号是否未锁定

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }//凭据是否未过期

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", email='" + email + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", avatar='" + avatar + '\'' +
                ", createTime=" + createTime +
                ", lastLoginTime=" + lastLoginTime +
                ", isVerifyEmail='" + isVerifyEmail + '\'' +
                ", articleSize=" + articleSize +
                ", viewSize=" + viewSize +
                ", followSize=" + followSize +
                ", fanSize=" + fanSize +
                ", homepage='" + homepage + '\'' +
                ", github='" + github + '\'' +
                ", profile='" + profile + '\'' +
                '}';
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Timestamp lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getIsVerifyEmail() {
        return isVerifyEmail;
    }

    public void setIsVerifyEmail(String isVerifyEmail) {
        this.isVerifyEmail = isVerifyEmail;
    }

    public Long getArticleSize() {
        return articleSize;
    }

    public void setArticleSize(Long articleSize) {
        this.articleSize = articleSize;
    }

    public Long getViewSize() {
        return viewSize;
    }

    public void setViewSize(Long viewSize) {
        this.viewSize = viewSize;
    }

    public Long getFollowSize() {
        return followSize;
    }

    public void setFollowSize(Long followSize) {
        this.followSize = followSize;
    }

    public Long getFanSize() {
        return fanSize;
    }

    public void setFanSize(Long fanSize) {
        this.fanSize = fanSize;
    }

    public Long getQuestionSize() {
        return questionSize;
    }

    public void setQuestionSize(Long questionSize) {
        this.questionSize = questionSize;
    }

    public Long getAnswerSize() {
        return answerSize;
    }

    public void setAnswerSize(Long answerSize) {
        this.answerSize = answerSize;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Integer getIsFriend() {
        return isFriend;
    }

    public void setIsFriend(Integer isFriend) {
        this.isFriend = isFriend;
    }

    public Integer getReputation() {
        return reputation;
    }

    public void setReputation(Integer reputation) {
        this.reputation = reputation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Bookmark> getBookmarkList() {
        return bookmarkList;
    }

    public void setBookmarkList(List<Bookmark> bookmarkList) {
        this.bookmarkList = bookmarkList;
    }

    public List<Article> getArticleList() {
        return articleList;
    }

    public void setArticleList(List<Article> articleList) {
        this.articleList = articleList;
    }

    public List<Comment> getCommentList() {
        return commentList;
    }

    public void setCommentList(List<Comment> commentList) {
        this.commentList = commentList;
    }

    public List<LoginRecord> getLoginRecordList() {
        return loginRecordList;
    }

    public void setLoginRecordList(List<LoginRecord> loginRecordList) {
        this.loginRecordList = loginRecordList;
    }

    public List<Zan> getZanList() {
        return zanList;
    }

    public void setZanList(List<Zan> zanList) {
        this.zanList = zanList;
    }

    public List<Cai> getCaiList() {
        return caiList;
    }

    public void setCaiList(List<Cai> caiList) {
        this.caiList = caiList;
    }

    public List<Category> getCategoryList() {
        return categoryList;
    }

    public void setCategoryList(List<Category> categoryList) {
        this.categoryList = categoryList;
    }

    public List<Message> getMessageFromList() {
        return messageFromList;
    }

    public void setMessageFromList(List<Message> messageFromList) {
        this.messageFromList = messageFromList;
    }

    public List<Message> getMessageToList() {
        return messageToList;
    }

    public void setMessageToList(List<Message> messageToList) {
        this.messageToList = messageToList;
    }
}