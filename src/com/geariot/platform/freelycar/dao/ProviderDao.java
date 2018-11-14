package com.geariot.platform.freelycar.dao;

import java.util.List;


import com.geariot.platform.freelycar.entities.Provider;

public interface ProviderDao {
	
	void save(Provider provider);
	
	void modify(Provider provider);
	
	void delete(int providerId);
	
	List<Provider> listProviders(int from , int pageSize);
	
	long getCount();
	
	//List<Provider> queryByName(String name);
	
	List<Provider> getConditionQuery(String andCondition , int from , int pageSize);
	
	Provider findProviderById(int providerId);
	
	long getConditionCount(String andCondition);
	
	List<String> listName();
	
	Provider findProviderByName(String name);
	
}
