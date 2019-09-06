package com.cn.cms.modelVo;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.Date;

public class SysUserVo implements Serializable {
    private Integer id;

    @NotBlank(message = " 用户名不能为空")
    private String userName;

    @NotBlank(message = "登录名不能为空")
    @Pattern(regexp="^[a-zA-Z]{1}([a-zA-Z0-9]|[._-]){3,15}$",message="登录名格式错误，4到16位（字母，数字，下滑线，减号）")
    private String loginName;

    @NotBlank(message = "密码不能为空")
    @Length(min = 6, max = 18, message = "密码长度必须为6-18位")
    private String password;

    @Email
    private String email;

    @NotNull
    @Pattern(regexp="^[1][3,4,5,7,8][0-9]{9}$",message="手机号码格式不符")
    private String phone;

    @NotBlank(message = "用户状态不能为空")
    private String userStatus;

    private Date createTime;

    private Integer createUser;

    private Date updateTime;

    private Integer updateUser;

    private int pageNum; //页码

    private int pageSize;//页面条目数量

    private Integer sysId; //操作ID

    private int[] roles;//角色集合

    
    public Integer getId() {
        return id;
    }

    
    public void setId(Integer id) {
        this.id = id;
    }

    
    public String getUserName() {
        return userName;
    }

    
    public void setUserName(String userName) {
        this.userName = userName;
    }

    
    public String getLoginName() {
        return loginName;
    }

    
    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    
    public String getPassword() {
        return password;
    }

    
    public void setPassword(String password) {
        this.password = password;
    }

    
    public String getEmail() {
        return email;
    }

    
    public void setEmail(String email) {
        this.email = email;
    }

    
    public String getPhone() {
        return phone;
    }

    
    public void setPhone(String phone) {
        this.phone = phone;
    }

    
    public String getUserStatus() {
        return userStatus;
    }

    
    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
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

    public Integer getSysId() {
        return sysId;
    }

    public void setSysId(Integer sysId) {
        this.sysId = sysId;
    }

    public int[] getRoles() {
        return roles;
    }

    public void setRoles(int[] roles) {
        this.roles = roles;
    }
}