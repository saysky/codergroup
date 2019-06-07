package com.liuyanzhao.forum.dto;

import lombok.Data;

/**
 * @author 言曌
 * @date 2018/5/24 下午3:03
 */

@Data
public class UserArticleRankDTO {

    private Integer id;

    private String username;

    private String nickname;

    private String avatar;

    private String articleSize;

    public UserArticleRankDTO(Integer id, String username, String nickname, String avatar, String articleSize) {
        this.id = id;
        this.username = username;
        this.nickname = nickname;
        this.avatar = avatar;
        this.articleSize = articleSize;
    }

    public UserArticleRankDTO() {
    }
}
