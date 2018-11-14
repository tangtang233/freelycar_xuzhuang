package com.geariot.platform.freelycar.dao;

import java.util.List;

import com.geariot.platform.freelycar.entities.InventoryBrand;
import com.geariot.platform.freelycar.entities.InventoryType;

public interface InventoryBrandDao {
	
	long getCount();
	
	InventoryBrand findById(int inventoryBrandId);
	
	InventoryBrand findByName(String name);
	
	int add(InventoryBrand inventoryBrand);
	
	int delete(List<Integer> brandIds);
	
	/*List<InventoryBrand> list(int from, int pageSize);
	
	List<InventoryBrand> query(String name);*/
	
	List<InventoryBrand> getConditionQuery(String andCondition , int from , int pageSize);
	
	long getConditionCount(String andCondition);
	
	/**
	 * 有数据就不插入 没数据 就插入
	 */
	InventoryBrand insertIfExist(int id,String typeName);
	
}
