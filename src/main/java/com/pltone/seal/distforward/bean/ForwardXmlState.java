package com.pltone.seal.distforward.bean;

import java.io.Serializable;

/**
 * 转发报文状态信息
 *
 * @author chenlong
 * @version 1.0 2019-01-08
 */
public class ForwardXmlState implements Serializable {
    private static final long serialVersionUID = -8977164629660192764L;
    /** 记录ID */
    private long id;
    /** 转发状态（0 未转发（默认），1 转发成功，2 转发失败） */
    private int state;
    /** 物流报文 */
    private String xml;

    /**
     * 是否转发成功
     */
    public boolean isDone() {
        return state == 1;
    }

    /**
     * 是否转发失败
     */
    public boolean isFail() {
        return state == 2;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }
}
