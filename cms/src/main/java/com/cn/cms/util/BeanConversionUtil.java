package com.cn.cms.util;

import com.cn.cms.model.Permission;
import com.cn.cms.model.Role;
import com.cn.cms.model.SysUser;
import com.cn.cms.modelVo.PermissionVo;
import com.cn.cms.modelVo.RoleVo;
import com.cn.cms.modelVo.SysUserVo;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author: Farben
 * @description: BeanConversionUtil:系统model跟modelVo相互转换
 * @create: 2019/9/6-11:30
 **/
public class BeanConversionUtil {
    public static SysUser toSysUser(SysUserVo sysUserVo){
        SysUser sysUser=new SysUser();
        BeanUtils.copyProperties(sysUserVo,sysUser);
        return sysUser;
    }

    public static Role toRole(RoleVo roleVo){
        Role role=new Role();
        BeanUtils.copyProperties(roleVo,role);
        return role;
    }

    public static Permission toPermission(PermissionVo permissionVo){
        Permission permission=new Permission();
        BeanUtils.copyProperties(permissionVo,permission);
        return permission;
    }
    public static List<Permission> toPermissions(List<PermissionVo> permissionVos){
        List<Permission> permissions=new ArrayList<>();
        for(PermissionVo p:permissionVos){
            permissions.add(toPermission(p));
        }
        return permissions;
    }

    public static PermissionVo toPermissionVo(Permission permission){
        PermissionVo permissionVo=new PermissionVo();
        BeanUtils.copyProperties(permission,permissionVo);
        return permissionVo;
    }

    public static List<PermissionVo> toPermissionVos(List<Permission> permissions){
        List<PermissionVo> permissionVos=new ArrayList<>();
        for(Permission p:permissions){
            permissionVos.add(toPermissionVo(p));
        }
        return permissionVos;
    }
}
