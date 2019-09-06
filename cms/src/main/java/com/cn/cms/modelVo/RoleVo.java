package com.cn.cms.modelVo;

import org.hibernate.validator.constraints.NotBlank;

import java.util.Date;

public class RoleVo {
    private Integer id;

    private String roleName;

    private String roleDesc;

    private String roleStatus;

    private Date createTime;

    private Integer createUser;

    private Date updateTime;

    private Integer updateUser;

    private int pageNum; // 当前页码

    private int pageSize;//页面条目数量

    private int pages;//总页数

    private Integer sysId; //操作ID

    private int[] permissions;//资源权限集合

    private int check ;//是否被选中，0-否 1-是

    public Integer getId() {
        return id;
    }

    
    public void setId(Integer id) {
        this.id = id;
    }

    
    public String getRoleName() {
        return roleName;
    }

    
    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    
    public String getRoleDesc() {
        return roleDesc;
    }

    
    public void setRoleDesc(String roleDesc) {
        this.roleDesc = roleDesc;
    }

    
    public String getRoleStatus() {
        return roleStatus;
    }

    
    public void setRoleStatus(String roleStatus) {
        this.roleStatus = roleStatus;
    }

    
    public Date getCreateTime() {
        return createTime;
    }

    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    
    public Integer getCreateUser() {
        return createUser;
    }

    
    public void setCreateUser(Integer createUser) {
        this.createUser = createUser;
    }

    
    public Date getUpdateTime() {
        return updateTime;
    }

    
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    
    public Integer getUpdateUser() {
        return updateUser;
    }

    
    public void setUpdateUser(Integer updateUser) {
        this.updateUser = updateUser;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public Integer getSysId() {
        return sysId;
    }

    public void setSysId(Integer sysId) {
        this.sysId = sysId;
    }

    public int[] getPermissions() {
        return permissions;
    }

    public void setPermissions(int[] permissions) {
        this.permissions = permissions;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }
}