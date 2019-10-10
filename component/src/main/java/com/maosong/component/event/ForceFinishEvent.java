package com.maosong.component.event;

/**
 * @author zhouhao
 * @since 2019/04/12
 */
public class ForceFinishEvent extends MessageEvent {

    private String className;

    public ForceFinishEvent(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }
}
