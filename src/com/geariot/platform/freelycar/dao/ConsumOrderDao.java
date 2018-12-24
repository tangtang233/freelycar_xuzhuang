package com.geariot.platform.freelycar.dao;

import com.geariot.platform.freelycar.entities.ConsumOrder;
import com.geariot.platform.freelycar.model.OrderSummary;
import com.geariot.platform.freelycar.utils.query.ConsumOrderQueryCondition;
import com.geariot.platform.freelycar.utils.query.ProgramPayStat;

import java.util.Date;
import java.util.List;

public interface ConsumOrderDao {
	
	void save(ConsumOrder consumOrder);
	
	void update(ConsumOrder consumOrder);

	List<ConsumOrder> list(int from, int pageSize);

	long getCount();

	ConsumOrder findById(String consumOrderId);
	
	//自定义修改order表
	ConsumOrder updateOrderConsutom(ConsumOrder consumOrder);
	
	//更新order
	void updateOrder(ConsumOrder consumOrder);

	List<ConsumOrder> query(ConsumOrderQueryCondition andCondition, int from, int pageSize);
	
	long getQueryCount(ConsumOrderQueryCondition andCondition);
	
	List<String> getConsumOrderIdsByStaffId(int staffId);
	
	List<ConsumOrder> findWithClientId(int clientId);
	
	List<ConsumOrder> findByMakerAccount(String account);
	
	List<ConsumOrder> findByPickCarStaffId(int staffId);
	
	List<ConsumOrder> findByStoreId(int storeId, int from, int pageSize);
	
	long countByStoreId(int storeId);
	
	long countInventoryInfoByIds(List<String> inventoryIds);
	
	void removeStaffInConsumOrderStaffs(int staffId);
	
	List<ProgramPayStat> programNameToday();
	
	List<ProgramPayStat> programNameMonth();
	
	List<ProgramPayStat> programNameRange(Date startTime , Date endTime);
	
	//long getQueryClientCount(String andCondition, int clientId);
	
	List<ConsumOrder> queryByClientIdToday(int clientId, int from, int pageSize);
	
	List<ConsumOrder> queryByClientIdMonth(int clientId, int from, int pageSize);
	
	List<ConsumOrder> queryByClientIdAll(int clientId);
	
	List<ConsumOrder> queryByClientIdDate(int clientId, int from, int pageSize, Date startTime, Date endTime);
	
	float storeAverage(int storeId);
	
	List<ConsumOrder> bigScreenOrder(int from, int pageSize);
	
	List<ConsumOrder> bigScreenOrder();
	
	long getBigScreenOrderCount();

	List<OrderSummary> listAllPaidOrders(String startTime, String endTime, int from, int pageSize);

	long getAllPaidOrdersCount(String startTime, String endTime);

	List<OrderSummary> listAllPaidOrders(String startTime, String endTime);
}
