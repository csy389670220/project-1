package com.cn.cms.controller;

import com.cn.cms.modelVo.ItemSeckillVo;
import com.cn.cms.result.BusinessException;
import com.cn.cms.result.EmBusinessCode;
import com.cn.cms.result.ResultMapUtil;
import com.cn.cms.service.SeckillService;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 秒杀商品控制类
 */
@Controller
@RequestMapping("seckill")
@ResponseBody
public class SeckillController extends BaseController {
    private static final Logger logger = LoggerFactory.getLogger(SeckillController.class);

    @Autowired
    SeckillService seckillService;

    @ModelAttribute
    public void getSysUserId(){
        sysId= (Integer) SecurityUtils.getSubject().getSession().getAttribute("sysId");
        logger.debug("SeckillController getSysUserId:{}",sysId);
    }

    @RequestMapping(value = "/query")
    public ModelAndView query() {
        ModelAndView modelAndView = new ModelAndView("skill/skill");
        List<ItemSeckillVo> list = seckillService.getSeckillList();
        modelAndView.addObject("list", list);
        return modelAndView;
    }

    @RequestMapping(value = "/{seckillId}/detail",method = RequestMethod.GET)
    public ModelAndView detail(@PathVariable("seckillId") Integer seckillId) {
        ModelAndView modelAndView = new ModelAndView("skill/detail");
        ItemSeckillVo itemSeckillVo = seckillService.getById(sysId,seckillId);
        modelAndView.addObject("itemSeckillVo", itemSeckillVo);
        return modelAndView;
    }

    /**
     * 获取当前系统时间
     * @return
     */
    @RequestMapping(value = "/time/now",method = RequestMethod.GET)
    public long time(){
        Date now = new Date();
        return now.getTime();
    }

    /**
     * 获取MD5
     */
    @RequestMapping(value = "/{seckillId}/exposer",method = RequestMethod.GET)
    public String exposer(@PathVariable("seckillId") Integer secklillId){
       String md5=seckillService.exportSeckillUrl(secklillId);
       return md5;
    }

    /**
     * 执行秒杀
     * @return
     */
    @RequestMapping(value = "/{md5}/execution",method = RequestMethod.POST)
    public Map<String, Object> execute(@PathVariable("md5") String md5,
                                       @RequestParam("itemId") Integer itemId,
                                       @RequestParam("seckillId") Integer seckillId  ){
        try {
            Map<String, Object> result=seckillService.executeSeckill(itemId,seckillId,sysId,md5);
            return result;

        }catch (DuplicateKeyException e){
            logger.info("execution DuplicateKeyException is :"+e.getMessage(),e);
            return ResultMapUtil.build(EmBusinessCode.SECKILL_REPEAT_ERROR);
        } catch(BusinessException e){
            logger.info("execution BusinessException is :"+e.getErrMsg(),e);
            return ResultMapUtil.build(String.valueOf(e.getErrCode()),e.getErrMsg());
        } catch (Exception e) {
            logger.error("execution Exception is :"+e.getMessage(),e);
            return ResultMapUtil.build(EmBusinessCode.SECKILL_EXECUTE_ERROR);
        }

    }

    /**
     * 执行秒杀 by 存储过程
     * @return
     */
    @RequestMapping(value = "/{md5}/executionProducer")
    public Map<String, Object> executeProducer(@PathVariable("md5") String md5,
                                               @RequestParam("itemId") Integer itemId,
                                               @RequestParam("seckillId") Integer seckillId ){

       return seckillService.executeSeckillProducer(itemId,seckillId,sysId,md5);
    }

}
