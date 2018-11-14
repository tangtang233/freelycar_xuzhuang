package com.geariot.platform.freelycar.dao;

import java.util.List;

import com.geariot.platform.freelycar.entities.Service;

public interface ServiceDao {

	Service findServiceById(int serviceId);
	
	void save(Service service);
	
//	void delete(int serviceId);
	
	/*List<Service> listServices(int from , int pageSize);*/
	
	List<Service> queryByName(String name);
	
	long getCount();
	
	List<Service> listServices(String andCondition , int from , int pageSize);
	
	long getConditionCount(String andCondition);
	
	List<Object> listName();
	
	long countProjectByIds(List<Integer> ids);
	
}
