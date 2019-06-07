package com.liuyanzhao.forum.entity;

import com.liuyanzhao.forum.util.DateUtil;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.NotEmpty;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

/**
 * @author 言曌
 * @date 2018/6/3 上午11:12
 */
@Entity(name = "answer")
@Getter
@Setter
public class Answer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Lob  // 大对象，映射 MySQL 的 Long Text 类型
    @Basic(fetch = FetchType.LAZY) // 懒加载
    @NotEmpty(message = "内容不能为空")
    @Size(max = 500000, message = "内容过长，请删减")
    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id")
    private Question question;

    @org.hibernate.annotations.CreationTimestamp
    @Column(name = "create_time")
    private Timestamp createTime;

    @Column(name = "pid")
    private Long pid;//父分类

    //这里要把数据库的外键删除
    @OneToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "pid", referencedColumnName = "id")
    @OrderBy("id")
    private List<Answer> replyList;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "status")
    @Pattern(regexp = "publish|deleted")
    private String status = "publish";

    @Column(name = "comment_size")
    private Integer commentSize = 0;

    @Column(name = "is_accepted")
    private Integer isAccepted = 0;//是否采纳，未采纳0，已采纳1

    @JoinColumn(name = "reply_user_id")
    @OneToOne(fetch = FetchType.LAZY)
    private User replyUser;

    //赞的数量
    @Column(name = "zan_size")
    private Integer zanSize = 0;

    //踩的数量
    @Column(name = "cai_size")
    private Integer caiSize = 0;

    //这里要把数据库的外键删除
    @OneToMany(cascade = {CascadeType.DETACH}, fetch = FetchType.LAZY)
    @JoinColumn(name = "pid", referencedColumnName = "id")
    @OrderBy("id")
    private List<Answer> answerList;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "answer_zan", joinColumns = @JoinColumn(name = "answer_id", referencedColumnName = "id"),
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
                //如果点过赞了，就取消
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
    @JoinTable(name = "answer_cai", joinColumns = @JoinColumn(name = "answer_id", referencedColumnName = "id"),
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
                //如果已存在，取消点踩
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
    private Integer integral;//分数

    public Integer getIntegral() {
        return zanSize - caiSize;
    }

    @Transient
    public String easyCreateTime;


    public String getEasyCreateTime() {
        if (getCreateTime() == null) {
            return null;
        }
        return DateUtil.getRelativeDate(getCreateTime());
    }

    public Answer() {
    }

    public Answer(User user, Question question, Long pid, String content) {
        this.user = user;
        this.question = question;
        this.pid = pid;
        this.content = content;
    }

}
