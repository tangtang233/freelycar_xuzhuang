package com.geariot.platform.freelycar.dao;

import java.util.Date;
import java.util.List;

import com.geariot.platform.freelycar.entities.OtherExpendOrder;
import com.geariot.platform.freelycar.entities.OtherExpendType;

public interface ChargeDao {
	
	void save(OtherExpendType otherExpendType);
	
	void delete(int otherExpendTypeId);
	
	OtherExpendType findByName(String name);
	
	List<OtherExpendOrder> listAll(int from , int pageSize);

	long getCount();
	
	OtherExpendType findById(int id);
	
	List<OtherExpendType> listAll();
	
	void save(OtherExpendOrder otherExpendOrder);
	
	OtherExpendOrder findById(String id);
	
	void delete(String id);
	
	int delete(List<String> ids);
	
//	List<OtherExpendOrder> getSelectList(int otherExpendTypeId , Date startTime , Date endTime);
	
	List<OtherExpendOrder> getConditionQuery(int typeId , Date startTime , Date endTime, int from, int pageSize);
	
	long getConditionCount(int typeId , Date startTime , Date endTime);
	
}
