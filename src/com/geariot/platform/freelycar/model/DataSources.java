package com.geariot.platform.freelycar.model;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

public class DataSources extends AbstractRoutingDataSource{
	
	@Override
    protected Object determineCurrentLookupKey() {
		 DBType key = DataSourceSwitch.getDbType();//获得当前数据源标识符
	        //logger.info("当前数据源 :" + key);
	     return key;
    }
	
}
