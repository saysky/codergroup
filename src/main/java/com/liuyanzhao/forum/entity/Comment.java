package com.liuyanzhao.forum.entity;

import com.liuyanzhao.forum.util.DateUtil;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;


import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/3/19 下午9:54
 */

@Entity(name = "comment")
@Data
public class Comment implements Serializable {

    private static final long serialVersionUID = -4502134548520740266L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 自增长策略
    private Long id;

    @Column(name = "pid")
    private Long pid;//父分类

    @NotEmpty(message = "评论内容不能为空")
    @Size(max = 10000, message = "评论内容不能多于500个字符")
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private Integer floor;//楼层

    @Column(name = "is_sticky")
    private Integer isSticky = 0;//置顶状态，0表示不置顶，1置顶

    @org.hibernate.annotations.CreationTimestamp  // 由数据库自动创建时间
    @Column(name = "create_time")
    private Timestamp createTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id")
    private Article article;//所属文章

    //这里要把数据库的外键删除
    @OneToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "pid", referencedColumnName = "id")
    @OrderBy("id")
    private List<Comment> commentList;

    @JoinColumn(name = "reply_user_id")
    @OneToOne(fetch = FetchType.LAZY)
    private User replyUser;

    //赞的数量
    @Column(name = "zan_size")
    private Integer zanSize = 0;

    //踩的数量
    @Column(name = "cai_size")
    private Integer caiSize = 0;

    @Pattern(regexp = "publish|draft|private|deleted")
    @Column(name = "status")
    private String status = "publish";



    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "comment_zan", joinColumns = @JoinColumn(name = "comment_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "zan_id", referencedColumnName = "id"))
    private List<Zan> zanList;


    public List<Zan> getZanList() {
        return zanList;
    }

    public void setZanList(List<Zan> zanList) {
        this.zanList = zanList;
        this.zanSize = this.zanList.size();

    }


    /**
     * 点赞
     *
     * @param zan
     * @return
     */
    public boolean addZan(Zan zan) {
        boolean isExist = false;
        // 判断重复
        for (int index = 0; index < this.zanList.size(); index++) {
            if (Objects.equals(this.zanList.get(index).getUser().getId(), zan.getUser().getId())) {
                isExist = true;
                this.zanList.remove(index);
                break;
            }
        }

        if (!isExist) {
            this.zanList.add(zan);
        }
        this.zanSize = this.zanList.size();
        return isExist;
    }

    /**
     * 取消点赞
     *
     * @param zanId
     */
    public void removeZan(Long zanId) {
        for (int index = 0; index < this.zanList.size(); index++) {
            if (Objects.equals(this.zanList.get(index).getId(), zanId)) {
                this.zanList.remove(index);
                break;
            }
        }

        this.zanSize = this.zanList.size();
    }


    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "comment_cai", joinColumns = @JoinColumn(name = "comment_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "cai_id", referencedColumnName = "id"))
    private List<Cai> caiList;


    public List<Cai> getCaiList() {
        return caiList;
    }

    public void setCaiList(List<Cai> caiList) {
        this.caiList = caiList;
        this.caiSize = this.caiList.size();

    }

    /**
     * 点踩
     *
     * @param cai
     * @return
     */
    public boolean addCai(Cai cai) {
        boolean isExist = false;
        // 判断重复
        for (int index = 0; index < this.caiList.size(); index++) {
            if (Objects.equals(this.caiList.get(index).getUser().getId(), cai.getUser().getId())) {
                isExist = true;
                this.caiList.remove(index);
                break;
            }
        }
        if (!isExist) {
            this.caiList.add(cai);
        }
        this.caiSize = this.caiList.size();
        return isExist;
    }

    /**
     * 取消点踩
     *
     * @param zanId
     */
    public void removeCai(Long zanId) {
        for (int index = 0; index < this.caiList.size(); index++) {
            if (Objects.equals(this.caiList.get(index).getId(), zanId)) {
                this.caiList.remove(index);
                break;
            }
        }

        this.caiSize = this.caiList.size();
    }

    @Transient
    public String easyCreateTime;


    public String getEasyCreateTime() {
        if (getCreateTime() == null) {
            return null;
        }
        return DateUtil.getRelativeDate(getCreateTime());
    }


    public Comment() {
    }

    public Comment(User user, Article article, String content) {
        this.article = article;
        this.content = content;
        this.user = user;
    }


    public Comment(User user, Article article, Long pid, String content) {
        this.user = user;
        this.article = article;
        this.pid = pid;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", pid=" + pid +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                '}';
    }
}



