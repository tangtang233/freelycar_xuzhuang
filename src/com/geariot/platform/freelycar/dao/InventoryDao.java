package com.geariot.platform.freelycar.dao;

import java.util.Date;
import java.util.List;

import com.geariot.platform.freelycar.entities.Inventory;

public interface InventoryDao {
	void add(Inventory inventory);
	
	void update(Inventory inventory);

	int delete(List<String> inventoryIds);
	
	Inventory findById(String id);

	List<Inventory> list(String name, int providerId, String typeId, int from, int number);
	
	boolean checkUnique(String name, String standard, String property);
	
	List<Inventory> listTest(Date createTime);

	long getCount(String name, int providerId, String typeId);

	List<Inventory> findByProviderId(int providerId);
	
	List<Object[]> getInventoryName();
	
	List<String> findByTypeName(String typeName);
}
