package com.maosong.component.net;

/**
 * Created by tianweiping on 2017/12/11.
 */

public class TokenException extends RuntimeException {
    private int code;

    public TokenException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
