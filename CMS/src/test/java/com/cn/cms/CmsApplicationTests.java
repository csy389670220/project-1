package com.cn.cms;

import com.cn.cms.mapper.SysUserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CmsApplicationTests {
	@Autowired
	SysUserMapper sysUserMapper;

	@Test
	public void contextLoads() {
		sysUserMapper.selectByLoginName("admin");
	}

}
