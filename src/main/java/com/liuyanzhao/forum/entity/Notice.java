package com.liuyanzhao.forum.entity;

import com.liuyanzhao.forum.util.DateUtil;


import javax.persistence.*;
import java.sql.Timestamp;

/**
 * 提醒
 *
 * @author 言曌
 * @date 2018/5/29 下午7:05
 */
@Entity(name = "notice")
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 操作提醒的用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id")
    private User fromUser;


    /**
     * 接受提醒的用户
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id")
    private User toUser;

    /**
     * 提醒类型
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notice_type_id")
    private NoticeType noticeType;

    /**
     * 页面url
     */
    @Column(name = "guid")
    private String guid;


    @Transient
    private Integer count;

    /**
     * 内容
     */
    private String content;

    /**
     * 补充
     */
    private String more;

    @org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
    @Column(name = "create_time")
    private Timestamp createTime;


    private Integer status = 0; //0未阅读，1已阅读



    @Transient
    public String easyCreateTime;

    public String getEasyCreateTime() {
        if (getCreateTime() == null) {
            return null;
        }
        return DateUtil.getRelativeDate(getCreateTime());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getFromUser() {
        return fromUser;
    }

    public void setFromUser(User fromUser) {
        this.fromUser = fromUser;
    }

    public User getToUser() {
        return toUser;
    }

    public void setToUser(User toUser) {
        this.toUser = toUser;
    }

    public NoticeType getNoticeType() {
        return noticeType;
    }

    public void setNoticeType(NoticeType noticeType) {
        this.noticeType = noticeType;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMore() {
        return more;
    }

    public void setMore(String more) {
        this.more = more;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public void setEasyCreateTime(String easyCreateTime) {
        this.easyCreateTime = easyCreateTime;
    }
}
