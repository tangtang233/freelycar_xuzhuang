package com.geariot.platform.freelycar.dao;

import java.util.Date;
import java.util.List;

import com.geariot.platform.freelycar.entities.InventoryOrder;
import com.geariot.platform.freelycar.entities.InventoryOrderInfo;

public interface InventoryOrderDao {
	
	void save(InventoryOrder inventoryOrder);

	List<InventoryOrder> list(int from, int number);

	long getCount();
	
	//String inventoryOrderId, String adminId, String type,  int providerId,int state, Date startTime, Date endTime,
	List<InventoryOrder> query(InventoryOrder order,Integer types[], Date startTime,Date endTime,int from, int pageSize);
	
	long getQueryCount(InventoryOrder order, Integer types[],Date startTime,Date endTime);

	InventoryOrder findById(String inventoryOrderId);

	void deleteOrder(String orderId);
	
	List<InventoryOrder> findByMakerAccount(String account);
	
	List<InventoryOrderInfo> findInfoByProviderId(int providerId);
	
	void setByOrderId(String orderId, String id);
	
	List<InventoryOrder> findByIds(String... inventoryOrderIds);
	
	List<InventoryOrder> queryConditions(int providerId, Date startTime, Date endTime,int from, int pageSize);
	
	int queryConditionsCount(int providerId, Date startTime, Date endTime);
}
