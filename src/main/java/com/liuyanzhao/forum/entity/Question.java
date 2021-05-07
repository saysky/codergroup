package com.liuyanzhao.forum.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.liuyanzhao.forum.util.DateUtil;



import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

/**
 * @author 言曌
 * @date 2018/6/3 上午10:57
 */

@Entity(name = "question")
public class Question implements Serializable {

    private static final long serialVersionUID = 5886418188213599434L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @NotEmpty(message = "标题不能为空")
    @Size(max = 200, message = "标题不能超过200个字符")
    @Column(nullable = false, length = 200)
    private String title;

    @Lob  // 大对象，映射 MySQL 的 Long Text 类型
    @Basic(fetch = FetchType.LAZY) // 懒加载
    @NotEmpty(message = "内容不能为空")
    @Size(max = 500000, message = "内容过长，请删减")
    @Column(nullable = false)
    private String content;

    @NotEmpty(message = "摘要不能为空")
    @Size(max = 2000, message = "摘要不能超过2000个字符")
    @Column(name = "summary")
    private String summary = "";

    @Column(name = "answer_size", length = 100)
    private Integer answerSize = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    @Column(name = "tags", length = 100)
    @Size(max = 100, message = "关键词太长了")
    private String tags;

    @Column(name = "guid", length = 100)
    private String guid;

    @org.hibernate.annotations.CreationTimestamp
    @Column(name = "create_time")
    private Timestamp createTime;

    @Column(name = "update_time")
    private Timestamp updateTime;

    @Column(name = "view_size")
    private Integer viewSize = 0;

    /**
     * 状态resolved已解决
     */
    @Column(length = 10)
    @Pattern(regexp = "publish|resolved|deleted")
    private String status = "publish";

    @OneToMany(cascade = {CascadeType.DETACH}, mappedBy = "question", fetch = FetchType.LAZY)
    private List<Answer> answerList;

    @Transient
    public List<String> tagList;

    public List<String> getTagList() {
        if (tags != null) {
            String[] arr = tags.split(",|，", 5);
            List<String> list = Arrays.asList(arr);
            return list;
        } else {
            return null;
        }
    }

    public void setTagList(List<String> tagList) {
        this.tagList = tagList;
    }

    @Transient
    public String easyCreateTime;

    @Transient
    public String easyUpdateTime;

    public String getEasyCreateTime() {
        if (getCreateTime() == null) {
            return null;
        }
        return DateUtil.getRelativeDate(getCreateTime());
    }

    public String getEasyUpdateTime() {
        if (getCreateTime() == null) {
            return null;
        }
        return DateUtil.getRelativeDate(getUpdateTime());
    }

    @Transient
    public String easyViewSize;

    public String getEasyViewSize() {
        Integer views = getViewSize();
        if (views == null) {
            return "1";
        }
        if (views < 1000) {
            return views.toString();
        }
        Integer suffix = views % 1000 / 100;
        if (suffix == 0) {
            return views / 1000 + "k";
        } else {
            return views / 1000 + "." + views % 1000 / 100 + "k";
        }
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Integer getAnswerSize() {
        return answerSize;
    }

    public void setAnswerSize(Integer answerSize) {
        this.answerSize = answerSize;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getViewSize() {
        return viewSize;
    }

    public void setViewSize(Integer viewSize) {
        this.viewSize = viewSize;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Answer> getAnswerList() {
        return answerList;
    }

    public void setAnswerList(List<Answer> answerList) {
        this.answerList = answerList;
    }

    public void setEasyCreateTime(String easyCreateTime) {
        this.easyCreateTime = easyCreateTime;
    }

    public void setEasyUpdateTime(String easyUpdateTime) {
        this.easyUpdateTime = easyUpdateTime;
    }

    public void setEasyViewSize(String easyViewSize) {
        this.easyViewSize = easyViewSize;
    }
}
