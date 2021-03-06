package com.geariot.platform.freelycar.dao;

import com.geariot.platform.freelycar.entities.ProjectInfo;
import com.geariot.platform.freelycar.entities.Staff;

import java.util.List;

public interface StaffDao {
	
	Staff findStaffByStaffId(int staffId);
	
	Staff findStaffByPhone(String phone);
	
	void saveStaff(Staff staff);
	
	void deleteStaff(int staffId);
	
	void deleteStaff(Staff staff);
	
	void deleteStaff(String staffName);
	
	List<Staff> listStaffs(int from , int pageSize);
	
	//List<Staff> queryByNameAndId(int staffId , String staffName);
	
	List<Staff> getConditionQuery(String staffId , String staffName , int from , int pageSize);
	
	long getConditionCount(String staffId , String staffName);
	
	List<ProjectInfo> staffServiceDetails(int staffId , int from , int pageSize);
	
	long getCount();

	Staff verifyTheRepeat(int staffId, String staffAccount);
}
