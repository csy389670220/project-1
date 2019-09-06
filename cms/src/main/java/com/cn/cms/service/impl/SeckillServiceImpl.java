package com.cn.cms.service.impl;

import com.cn.cms.mapper.ItemStockMapper;
import com.cn.cms.mapper.SeckillMapper;
import com.cn.cms.mapper.SuccessSkillMapper;
import com.cn.cms.model.Seckill;
import com.cn.cms.model.SuccessSkill;
import com.cn.cms.modelVo.ItemSeckillVo;
import com.cn.cms.redis.RedisRunner;
import com.cn.cms.result.BusinessException;
import com.cn.cms.result.EmBusinessCode;
import com.cn.cms.result.ResultMapUtil;
import com.cn.cms.service.SeckillService;
import com.cn.cms.util.CheckUtil;
import com.cn.cms.util.MD5;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SeckillServiceImpl implements SeckillService {

    private static final Logger logger = LoggerFactory.getLogger(SeckillService.class);

    //缓存秒杀活动ID key前缀
    private  static  final String SECKILLIDKEY="SECKILLID_";

    //缓存秒杀活动结束时间key前缀
    private  static  final String SECKILLENDKEY="SECKILLEND_";

    @Autowired
    SeckillMapper seckillMapper;

    @Autowired
    SuccessSkillMapper successSkillMapper;

    @Autowired
    ItemStockMapper itemStockMapper;

    @Autowired
    RedisRunner redisRunner;


    //md5盐值字符串,用于混淆MD5
    private final String SECKILL_SLAT = "afjeghawohgnospurfpeworai134";

    @Override
    public List<ItemSeckillVo> getSeckillList() {
        ItemSeckillVo itemSeckillVo = new ItemSeckillVo();
        itemSeckillVo.setItemStatus(1);//商品状态1-上架有效
        List<ItemSeckillVo> list = seckillMapper.queryAll(itemSeckillVo);
        return list;
    }

    @Override
    public ItemSeckillVo getById(Integer userId, Integer seckillId) {
        ItemSeckillVo itemSeckillVo = seckillMapper.queryById(seckillId);
        SuccessSkill successSkill=new SuccessSkill();
        successSkill.setUserId(userId);
        successSkill.setSkillId(seckillId);
        int num=successSkillMapper.selectNumByUserId(successSkill);
        if(num>0){//已经参加过秒杀
            itemSeckillVo.setParticipate(true);
        }else {
            itemSeckillVo.setParticipate(false);
        }
        return itemSeckillVo;
    }

    @Override
    public String exportSeckillUrl(Integer seckillId) {
        String md5;
        Date now = new Date();

        //tips:缓存key的定义逻辑：前缀+秒杀活动ID

        //先在redie获取seckillId对应的MD5，如果不为空直接返回
        //redis挂掉会导致get操作报错
        try{
            md5= (String) redisRunner.get(SECKILLIDKEY+seckillId);
            if(!CheckUtil.isEmpty(md5)){
                return md5;
            }
        }catch (Exception e){
            logger.error("exportSeckillUrl get redis error:{}",e.getMessage());
        }

        Seckill seckill = seckillMapper.selectByPrimaryKey(seckillId);
        if (now.getTime() < seckill.getStartTime().getTime()) {//未开始
            return null;
        }
        md5 = MD5.encodeByMD5(seckillId + "" + SECKILL_SLAT);
        //将md5,秒杀时间信息存入redis,有效期60分钟
        redisRunner.set(SECKILLIDKEY+seckillId,md5,60*60);
        redisRunner.set(SECKILLENDKEY+seckillId,seckill.getEndTime(),60*60);
        return md5;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> executeSeckill(Integer itemId, Integer seckillId, Integer userId, String md5) throws BusinessException, Exception {
            //Integer+随机数参加秒杀
//            Integer ran= (int) (Math.random()*99999999);

            Date now = new Date();
            Date endTime;//活动结束时间
            //验证参数合法性
            if(CheckUtil.isEmpty(itemId) || CheckUtil.isEmpty(seckillId) || CheckUtil.isEmpty(userId)){
                throw new BusinessException(EmBusinessCode.PARAMETER_VALIDATION_ERROR);
            }

            //验证md5合法性
            if (md5 == null || !md5.equals(MD5.encodeByMD5(seckillId + "" + SECKILL_SLAT))) {
                throw new BusinessException(EmBusinessCode.SECKILL_MD5_ERROR);
            }

            //验证秒杀活动是否结束
            endTime= (Date) redisRunner.get(SECKILLENDKEY+seckillId);
            if(CheckUtil.isEmpty(endTime)){//缓存不存在查询数据库
                Seckill seckill = seckillMapper.selectByPrimaryKey(seckillId);
                endTime=seckill.getEndTime();
            }
            if(now.getTime()>endTime.getTime()){//活动已经结束
                throw new BusinessException(EmBusinessCode.SECKILL_END_ERROR);
            }

            //秒杀记录落地
            SuccessSkill successSkill = new SuccessSkill();
            successSkill.setUserId(userId);
            successSkill.setSkillId(seckillId);
            successSkill.setCreateTime(new Date());
            successSkill.setStatus(1);
            int insertNum=successSkillMapper.insertSelective(successSkill);
            if(insertNum<=0){// 插入失败，重复秒杀
                throw new BusinessException(EmBusinessCode.SECKILL_REPEAT_ERROR);
            }

            //减库存
            int num = itemStockMapper.reduceNumber(itemId);
            if (num <= 0) {//减库存失败
                throw new BusinessException(EmBusinessCode.SECKILL_STOCK_ERROR);
            }

            return ResultMapUtil.successData(EmBusinessCode.SECKILL_EXECUTE_OK.getErrMsg());
    }

    @Override
    public Map<String, Object> executeSeckillProducer(Integer itemId, Integer seckillId, Integer userId, String md5) {
        //Integer+随机数参加秒杀
        //Integer ran= (int) (Math.random()*99999999);

        Date now = new Date();
        Date endTime;//活动结束时间
        //验证参数合法性
        if(CheckUtil.isEmpty(itemId) || CheckUtil.isEmpty(seckillId) || CheckUtil.isEmpty(userId)){
            logger.info("executeSeckillProducer PARAMETER_VALIDATION_ERROR:{}",EmBusinessCode.PARAMETER_VALIDATION_ERROR.getErrMsg());
            return ResultMapUtil.build(EmBusinessCode.PARAMETER_VALIDATION_ERROR);
        }

        //验证md5合法性
        if (md5 == null || !md5.equals(MD5.encodeByMD5(seckillId + "" + SECKILL_SLAT))) {
            logger.info("executeSeckillProducer SECKILL_MD5_ERROR:{}",EmBusinessCode.SECKILL_MD5_ERROR.getErrMsg());
            return ResultMapUtil.build(EmBusinessCode.SECKILL_MD5_ERROR);
        }

        //验证秒杀活动是否结束
        endTime= (Date) redisRunner.get(SECKILLENDKEY+seckillId);
        if(CheckUtil.isEmpty(endTime)){//缓存不存在查询数据库
            Seckill seckill = seckillMapper.selectByPrimaryKey(seckillId);
            endTime=seckill.getEndTime();
        }
        if(now.getTime()>endTime.getTime()){//活动已经结束
            logger.info("executeSeckillProducer SECKILL_END_ERROR:{}",EmBusinessCode.SECKILL_END_ERROR.getErrMsg());
            return ResultMapUtil.build(EmBusinessCode.SECKILL_END_ERROR);
        }

        // 执行整个秒杀流程：成功记录+减库存
        Map<String, Object> map=new HashMap<>();
        map.put("skill_id",seckillId);
        map.put("user_id",userId);
        map.put("item_id",itemId);
        map.put("create_time",now);
        map.put("result",null);
        try {
            seckillMapper.killByProducer(map);
        }catch (Exception e){
            logger.error("executeSeckillProducer error:"+e,e.getMessage());
            return   ResultMapUtil.build(EmBusinessCode.SECKILL_EXECUTE_ERROR);
        }
        int result= MapUtils.getIntValue(map,"result",-3);
        if(1==result){
            //成功
            return   ResultMapUtil.successData(EmBusinessCode.SECKILL_EXECUTE_OK.getErrMsg());
        }else if(-1==result){
            //重复秒杀
            return   ResultMapUtil.build(EmBusinessCode.SECKILL_REPEAT_ERROR);
        } else if(0==result){
            //库存售罄
            return   ResultMapUtil.build(EmBusinessCode.SECKILL_STOCK_ERROR);
        }else {
            //存储过程系统失败
            return   ResultMapUtil.build(EmBusinessCode.SECKILL_EXECUTE_ERROR);
        }
    }

}
