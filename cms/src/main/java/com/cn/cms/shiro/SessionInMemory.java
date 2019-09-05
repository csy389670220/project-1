package com.cn.cms.shiro;

import org.apache.shiro.session.Session;

import java.util.Date;

/**
 * @author: Farben
 * @description: SessionInMemory
 * Use ThreadLocal as a temporary storage of Session, so that shiro wouldn't keep read redis several times while a request coming.
 * @create: 2019/8/29-13:48
 **/
public class SessionInMemory {
    private Session session;
    private Date createTime;

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
