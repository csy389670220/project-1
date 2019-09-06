package com.cn.cms.service.impl;

import com.cn.cms.service.ConfigClientService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

/**
 * @author: Farben
 * @description: ConfigClientService：配置中心客户端接口
 * @create: 2019/9/5-15:29
 * RefreshScope-刷新功能
 **/
@Service
@RefreshScope
public class ConfigClientServiceImpl implements ConfigClientService {
    private String label;

    @Override
    public String getLabel() {
        return label;
    }
}
