package com.geariot.platform.freelycar.dao;

import java.util.List;

import com.geariot.platform.freelycar.entities.ClearRecord;

public interface ClearRecordDao {
	
	void save(ClearRecord record);
	
	List<ClearRecord> recordsList(int providerId, int from, int pageSize);
	
	int getCount(int providerId);
}
