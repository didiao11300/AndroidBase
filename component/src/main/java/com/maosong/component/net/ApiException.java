package com.maosong.component.net;

/**
 * Created by tianweiping on 2018/1/30.
 */

public class ApiException extends RuntimeException {

    private int code;
    private String msg;

    public ApiException(int code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
