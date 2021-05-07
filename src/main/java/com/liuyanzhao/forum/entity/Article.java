package com.liuyanzhao.forum.entity;

import com.liuyanzhao.forum.util.DateUtil;


import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/3/19 下午9:54
 */
@Entity(name = "article")
public class Article implements Serializable {


    private static final long serialVersionUID = -5086173193716866676L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    @Column(name = "id", nullable = false)
    private Long id;

    @NotEmpty(message = "标题不能为空")
    @Size(max = 200, message = "标题不能超过200个字符")
    @Column(nullable = false, length = 200)
    private String title;

    @NotEmpty(message = "摘要不能为空")
    @Size(max = 2000, message = "摘要不能超过2000个字符")
    @Column(nullable = false)
    private String summary = "";

    @Lob  // 大对象，映射 MySQL 的 Long Text 类型
    @Basic(fetch = FetchType.LAZY) // 懒加载
    @NotEmpty(message = "内容不能为空")
    @Size(max = 50000000, message = "内容过长，请删减")
    @Column(nullable = false)
    private String content;//文章全文内容

    @org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
    @Column(name = "create_time")
    private Timestamp createTime;

    @Column(name = "update_time")
    @org.hibernate.annotations.UpdateTimestamp  // 由数据库自动创建时间
    private Timestamp updateTime;

    @Column(name = "is_allow_comment")
    private Integer isAllowComment = 1;//允许1，不允许0

    @Column(name = "is_sticky", length = 1)
    private Integer isSticky = 0;//置顶1，不置顶0

    @Column(length = 10)
    @Pattern(regexp = "publish|draft|private|deleted")
    private String status = "publish";//状态（已发布publish，草稿draft）

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "tags", length = 100)
    @Size(max = 100, message = "关键词太长了")
    private String tags;  // 标签

    @Column(name = "guid", length = 100)
    private String guid;//固定链接

    @OneToMany(cascade = {CascadeType.MERGE, CascadeType.REFRESH}, fetch = FetchType.LAZY)
    @JoinTable(name = "article_zan", joinColumns = @JoinColumn(name = "article_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "zan_id", referencedColumnName = "id"))
    private List<Zan> zanList;

    @OneToMany(cascade = {CascadeType.REMOVE}, mappedBy = "article", fetch = FetchType.LAZY)
    private List<Bookmark> bookmarkList;

    @Column(name = "view_size")
    private Integer viewSize = 0; // 访问量、阅读量

    @Column(name = "comment_size")
    private Integer commentSize = 0;  // 评论量

    @Column(name = "zan_size")
    private Integer zanSize = 0;//赞的数量

    @Column(name = "bookmark_size")
    private Integer bookmarkSize = 0;

    public Article() {
    }

    public Article(String title, String summary, String content) {
        this.title = title;
        this.summary = summary;
        this.content = content;
    }


    /**
     * 点赞
     *
     * @param Zan
     * @return
     */
    public boolean addZan(Zan Zan) {
        boolean isExist = false;
        // 判断重复
        for (int index = 0; index < this.zanList.size(); index++) {
            if (Objects.equals(this.zanList.get(index).getUser().getId(), Zan.getUser().getId())) {
                isExist = true;
                break;
            }
        }

        if (!isExist) {
            this.zanList.add(Zan);
            this.zanSize = this.zanList.size();
        }

        return isExist;
    }

    /**
     * 取消点赞
     *
     * @param ZanId
     */
    public void removeZan(Long ZanId) {
        for (int index = 0; index < this.zanList.size(); index++) {
            if (Objects.equals(this.zanList.get(index).getId(), ZanId)) {
                this.zanList.remove(index);
                break;
            }
        }

        this.zanSize = this.zanList.size();
    }


    @Transient
    public String easyCreateTime;


    public String getEasyCreateTime() {
        if (getCreateTime() == null) {
            return null;
        }
        return DateUtil.getRelativeDate(getCreateTime());
    }

    @Transient
    public List<String> getTagList() {
        if (tags != null) {
            String[] arr = tags.split(",|，", 5);
            List<String> list = Arrays.asList(arr);
            return list;
        } else {
            return null;
        }
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", summary='" + summary + '\'' +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", isAllowComment=" + isAllowComment +
                ", isSticky=" + isSticky +
                ", status='" + status + '\'' +
                ", tags='" + tags + '\'' +
                ", guid='" + guid + '\'' +
                ", viewSize=" + viewSize +
                ", zanSize=" + zanSize +
                '}';
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

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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

    public Integer getIsAllowComment() {
        return isAllowComment;
    }

    public void setIsAllowComment(Integer isAllowComment) {
        this.isAllowComment = isAllowComment;
    }

    public Integer getIsSticky() {
        return isSticky;
    }

    public void setIsSticky(Integer isSticky) {
        this.isSticky = isSticky;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public List<Zan> getZanList() {
        return zanList;
    }

    public void setZanList(List<Zan> zanList) {
        this.zanList = zanList;
    }

    public List<Bookmark> getBookmarkList() {
        return bookmarkList;
    }

    public void setBookmarkList(List<Bookmark> bookmarkList) {
        this.bookmarkList = bookmarkList;
    }

    public Integer getViewSize() {
        return viewSize;
    }

    public void setViewSize(Integer viewSize) {
        this.viewSize = viewSize;
    }

    public Integer getCommentSize() {
        return commentSize;
    }

    public void setCommentSize(Integer commentSize) {
        this.commentSize = commentSize;
    }

    public Integer getZanSize() {
        return zanSize;
    }

    public void setZanSize(Integer zanSize) {
        this.zanSize = zanSize;
    }

    public Integer getBookmarkSize() {
        return bookmarkSize;
    }

    public void setBookmarkSize(Integer bookmarkSize) {
        this.bookmarkSize = bookmarkSize;
    }

    public void setEasyCreateTime(String easyCreateTime) {
        this.easyCreateTime = easyCreateTime;
    }
}
