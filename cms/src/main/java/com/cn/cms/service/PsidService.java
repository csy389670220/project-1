package com.cn.cms.service;

import com.cn.cms.service.impl.PsidServiceHystric;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
/**
 * @author: Farben
 * @description: PsidService:psid服务对接接口
 * @create: 2019/9/5-15:27
 *  定义一个feign接口，
 *  通过@ FeignClient（“服务名”）
 *  @RequestMapping(value = "/接口名")，来指定调用哪个服务。比如在代码中调用了service-hi服务的“/hi”接口；
 *  接口参数需要@RequestParam
 **/
@FeignClient(value = "service-psid",fallback = PsidServiceHystric.class)
public interface PsidService {
    /**
     * 测试接口
     * @param name
     * @return
     */
    @RequestMapping(value = "/hi")
    String sayHiFromClientOne(@RequestParam(value = "name") String name);

    /**
     * 身份证验证接口
     * @param cardName 姓名
     * @param cardID   身份证号码
     * @param startDate  开始时间
     * @param endDate    结束时间
     * @return
     */
    @RequestMapping(value = "/verification")
    String verification(@RequestParam(value = "cardName") String cardName, @RequestParam(value = "cardID") String cardID,
                        @RequestParam(value = "startDate") String startDate, @RequestParam(value = "endDate") String endDate);
}
