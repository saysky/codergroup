package com.liuyanzhao.forum.vo;

/**
 * @author 言曌
 * @date 2018/3/18 上午9:04
 */

import lombok.Data;

import java.io.Serializable;


@Data
public class ResultVO implements Serializable {


    private static final long serialVersionUID = 1841171214259634581L;


    // 响应业务状态
    private Integer code;// code=0 表示正常

    // 响应消息
    private String msg;

    // 响应中的数据
    private Object data;

    public ResultVO(Integer code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public ResultVO() {
    }

    public static ResultVO ok() {
        return new ResultVO(0, "success", null);
    }

    public static ResultVO ok(String message) {
        return new ResultVO(0, message, null);
    }

    public static ResultVO error() {
        return new ResultVO(1, "error", null);
    }

    public static ResultVO error(String message) {
        return new ResultVO(1, message, null);
    }


}
