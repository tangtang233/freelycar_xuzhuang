/**
 * 
 */
package com.geariot.platform.freelycar.dao;

import java.util.List;

import com.geariot.platform.freelycar.entities.InsuranceOrder;

/**
 * @author mxy940127
 *
 */
public interface InsuranceOrderDao {
	
	List<InsuranceOrder> getInsuranceOrder(int from, int pageSize);
	
	long getCount();
	
	void save(InsuranceOrder insuranceOrder);
}
