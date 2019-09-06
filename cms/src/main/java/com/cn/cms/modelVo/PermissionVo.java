package com.cn.cms.modelVo;

import java.util.Date;
import java.util.List;

public class PermissionVo   {
    private Integer id;

    private Integer parentId;

    private String perCode;

    private String perDesc;

    private Date createTime;

    private Integer perLev;

    private List<PermissionVo> childPer;

    private int check ;//是否被选中，0-否 1-是

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getPerCode() {
        return perCode;
    }

    public void setPerCode(String perCode) {
        this.perCode = perCode;
    }

    public String getPerDesc() {
        return perDesc;
    }

    public void setPerDesc(String perDesc) {
        this.perDesc = perDesc;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getPerLev() {
        return perLev;
    }

    public void setPerLev(Integer perLev) {
        this.perLev = perLev;
    }

    public List<PermissionVo> getChildPer() {
        return childPer;
    }

    public void setChildPer(List<PermissionVo> childPer) {
        this.childPer = childPer;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }
}