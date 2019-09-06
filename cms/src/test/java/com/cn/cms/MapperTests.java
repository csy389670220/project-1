package com.cn.cms;

import com.cn.cms.mapper.RoleMapper;
import com.cn.cms.model.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MapperTests {
	@Autowired
	RoleMapper roleMapper;

	@Test
	public void roleMapperSelectByPrimaryKey() {
		Role role=roleMapper.selectByPrimaryKey(21);
		System.out.println(role.getRoleDesc()+"---"+role.getRoleName());
	}

}
