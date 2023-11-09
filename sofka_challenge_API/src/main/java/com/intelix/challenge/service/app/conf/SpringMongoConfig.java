package com.intelix.challenge.service.app.conf;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

@Configuration
public class SpringMongoConfig implements InitializingBean {

	@Autowired
	private MappingMongoConverter mappingMongoConverter;

	@Override
	public void afterPropertiesSet() throws Exception {
		mappingMongoConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
	}
}
