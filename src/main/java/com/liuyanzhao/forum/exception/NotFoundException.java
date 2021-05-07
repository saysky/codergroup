package com.liuyanzhao.forum.exception;

/**
 * 自定义404异常
 *
 * @author 言曌
 * @date 2018/3/20 下午12:50
 */

public class NotFoundException extends RuntimeException {

    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 6417641452178955756L;

    public NotFoundException() {
        super();
    }

    public NotFoundException(String message) {
        super(message);
    }




    public NotFoundException(Throwable cause) {
        super(cause);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}