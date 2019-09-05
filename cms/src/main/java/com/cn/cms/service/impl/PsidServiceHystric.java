package com.cn.cms.service.impl;

import com.cn.cms.service.PsidService;
import org.springframework.stereotype.Component;

/**
 * @author: Farben
 * @description: PsidServiceHystric:PsidService接口的熔断器
 * @create: 2019/9/5-15:28
 **/
@Component
public class PsidServiceHystric implements PsidService {
    @Override
    public String sayHiFromClientOne(String name) {
        return "sorry "+name;
    }

    @Override
    public String verification(String cardName, String cardID, String startDate, String endDate) {
        return "sorry,verification error ";
    }

}
