package com.liuyanzhao.forum.vo;

import lombok.Data;

/**
 * @author 言曌
 * @date 2018/5/28 下午11:38
 */

@Data
public class HotArticleVO {

    private Long id;

    private String title;

    private String guid;

    private Integer commentSize;
}
