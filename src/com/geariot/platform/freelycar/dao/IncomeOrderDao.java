package com.geariot.platform.freelycar.dao;

import com.geariot.platform.freelycar.entities.IncomeOrder;

import java.util.Date;
import java.util.List;

public interface IncomeOrderDao {
	List<IncomeOrder> findByClientId(int clientId);
	
	void save(IncomeOrder incomeOrder);
	
	List<IncomeOrder> listByDate(int from , int pageSize);
	
	List<IncomeOrder> listByMonth(int from , int pageSize);

	List<Object[]> listMonthStat(Date start, Date end);
	
	List<IncomeOrder> listByWeek(int from , int pageSize);
	
	List<IncomeOrder> listByDate();
	
	List<IncomeOrder> listByMonth();
	
	List<IncomeOrder> listByWeek();
	
	List<IncomeOrder> listByDateRange(Date startTime , Date endTime , int from , int pageSize);
	
	List<IncomeOrder> listByDateRange(Date startTime , Date endTime);
	
	List<Object[]> listByPayMethodToday();
	
	List<Object[]> listByPayMethodMonth();
	
	List<Object[]> listByPayMethodRange(Date startTime , Date endTime);
	
	List<Object[]> MemberPayToday();
	
	List<Object[]> MemberPayMonth();
	
	List<Object[]> MemberPayRange(Date startTime , Date endTime);
	
	List<IncomeOrder> listByClientIdToday(int clientId, int from, int pageSize);
	
	List<IncomeOrder> listByClientIdMonth(int clientId, int from, int pageSize);
	
	List<IncomeOrder> listByClientIdAll(int clientId);
	
	List<IncomeOrder> listByClientIdDate(int clientId, int from, int pageSize, Date startTime, Date endTime);
	
	//long getQueryCondition(String condition, int clientId);
	
	//float countAmountByClientId(String condition, int clientId);

    List<Object[]> statInfoByMonth(String dateString);
}
