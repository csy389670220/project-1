package com.cn.cms.shiro;

import com.cn.cms.mapper.SysUserMapper;
import com.cn.cms.model.SysUser;
import org.apache.log4j.Logger;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author: Farben
 * @description: RetryLimitHashedCredentialsMatcher-密码错误登陆次数限制
 * @create: 2019/8/29-11:19
 **/
public class RetryLimitHashedCredentialsMatcher extends HashedCredentialsMatcher {

    private static final Logger logger = Logger.getLogger(RetryLimitHashedCredentialsMatcher.class);

    public static final String DEFAULT_RETRYLIMIT_CACHE_KEY_PREFIX = "shiro:cache:retrylimit:";
    private static final long DEFAULT_RETRYLIMIT_CACHE_SESSION_TIMEOUT = 30*60L;
    private long retryLimitSessionTimeout = DEFAULT_RETRYLIMIT_CACHE_SESSION_TIMEOUT;
    private String keyPrefix = DEFAULT_RETRYLIMIT_CACHE_KEY_PREFIX;
    @Autowired
    private SysUserMapper sysUserMapper;
    private RedisManager redisManager;

    public void setRedisManager(RedisManager redisManager) {
        this.redisManager = redisManager;
    }

    private String getRedisKickoutKey(String username) {
        return this.keyPrefix + username;
    }

    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {

        //获取用户登录名
        String loginName = (String)token.getPrincipal();
        //获取用户登录次数
        AtomicInteger retryCount = (AtomicInteger)redisManager.get(getRedisKickoutKey(loginName));
        if (retryCount == null) {
            //如果用户没有登陆过,登陆次数加1 并放入缓存
            retryCount = new AtomicInteger(0);
        }
        if (retryCount.incrementAndGet() > 10) {
            //如果用户登陆失败次数大于5次 抛出锁定用户异常  并修改数据库字段
            SysUser user = sysUserMapper.selectByLoginName(loginName);
            if (user != null && "1".equals(user.getUserStatus())){
                //数据库字段 默认为1  就是正常状态 所以 要改为0
                //修改数据库的状态字段为锁定
                user.setUserStatus("0");
                sysUserMapper.updateByPrimaryKeySelective(user);
            }
            logger.info("锁定用户" + user.getLoginName());
            //抛出用户锁定异常
            throw new LockedAccountException();
        }
        //判断用户账号和密码是否正确
        boolean matches = super.doCredentialsMatch(token, info);
        if (matches) {
            //如果正确,从缓存中将用户登录计数 清除
            redisManager.del(getRedisKickoutKey(loginName));
        }else{
            redisManager.set(getRedisKickoutKey(loginName), retryCount,retryLimitSessionTimeout);
        }
        return matches;
    }

    /**
     * 根据用户登录名 解锁用户
     * @param loginName
     * @return
     */
    public void unlockAccount(String loginName){
        SysUser user = sysUserMapper.selectByLoginName(loginName);
        if (user != null){
            //修改数据库的状态字段为正常
            user.setUserStatus("1");
            sysUserMapper.updateByPrimaryKey(user);
            redisManager.del(getRedisKickoutKey(loginName));
        }
    }

}