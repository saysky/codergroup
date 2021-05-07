package com.liuyanzhao.forum.exception;

/**
 * 自定义异常
 *
 * @author 言曌
 * @date 2018/3/20 下午12:50
 */

public class CustomException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 6417641452178955756L;

    public CustomException() {
        super();
    }

    public CustomException(String message) {
        super(message);
    }




    public CustomException(Throwable cause) {
        super(cause);
    }

    public CustomException(String message, Throwable cause) {
        super(message, cause);
    }
}