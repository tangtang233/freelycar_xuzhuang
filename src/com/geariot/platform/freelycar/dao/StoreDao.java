/**
 * 
 */
package com.geariot.platform.freelycar.dao;

import java.util.List;

import com.geariot.platform.freelycar.entities.Store;

/**
 * @author mxy940127
 *
 */

public interface StoreDao {
	
	void save(Store store);
	
	void update(Store store);
	
	long getCount();
	
	void delete(int storeId);
	
	List<Store> query(String condition, int from, int pageSize);
	
	long getQueryCount(String andCondition);
	
	Store findStoreById(int storeId);
	
	Store findStoreByName(String name);
}
