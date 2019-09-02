package com.cn.cms.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import com.cn.cms.util.AESUtil;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
@EnableTransactionManagement
public class MyBatisConfig {
	public static final String AESKEY="iisuser_db_key";//  数据库连接池加密解密私钥

	@Autowired
	private Environment env;


	/**
	 * 配置Druid数据源
	 * @return
	 * @throws SQLException
	 */
	@Bean(name="dataSource",destroyMethod="close",initMethod ="init")
	public DataSource dataSource() throws SQLException{
		/**
		 * 账号密码解密
		 */
		byte[] usernamedecryptFrom = AESUtil.parseHexStr2Byte(env.getProperty("jdbc.username"));
		byte[] usernamedecryptResult = AESUtil.decrypt(usernamedecryptFrom, AESKEY);
		byte[] passworddecryptFrom = AESUtil.parseHexStr2Byte(env.getProperty("jdbc.password"));
		byte[] passworddecryptResult = AESUtil.decrypt(passworddecryptFrom, AESKEY);

		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
		dataSource.setUrl(env.getProperty("jdbc.url"));
		dataSource.setUsername(new String(usernamedecryptResult));
		dataSource.setPassword(new String(passworddecryptResult));
		//配置最大连接
		dataSource.setMaxActive(200);
		//配置初始连接
		dataSource.setInitialSize(20);
		//配置最小连接
		dataSource.setMinIdle(20);
		//连接等待超时时间
		dataSource.setMaxWait(60000);
		//间隔多久进行检测,关闭空闲连接
		dataSource.setTimeBetweenEvictionRunsMillis(60000);
		//一个连接最小生存时间
		dataSource.setMinEvictableIdleTimeMillis(300000);
		//连接等待超时时间 单位为毫秒 缺省启用公平锁，
		//并发效率会有所下降， 如果需要可以通过配置useUnfairLock属性为true使用非公平锁
		dataSource.setUseUnfairLock(true);
		//用来检测是否有效的sql
		dataSource.setValidationQuery("select 'x'");
		dataSource.setTestWhileIdle(true);
		//申请连接时执行validationQuery检测连接是否有效，配置为true会降低性能
		dataSource.setTestOnBorrow(false);
		//归还连接时执行validationQuery检测连接是否有效，配置为true会降低性能
		dataSource.setTestOnReturn(false);
		return dataSource;
	}

	@Bean(name = "sqlSessionFactory")
	public SqlSessionFactory sqlSessionFactory() throws Exception {

		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(dataSource());

		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		sqlSessionFactoryBean.setMapperLocations(resolver.getResources(env.getProperty("mybatis.mapperLocations")));
		sqlSessionFactoryBean.setTypeAliasesPackage(env.getProperty("mybatis.typeAliasesPackage"));

		return sqlSessionFactoryBean.getObject();
	}



}
