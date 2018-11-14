package com.geariot.platform.freelycar.dao;

import java.util.List;

import com.geariot.platform.freelycar.entities.Admin;
import com.geariot.platform.freelycar.entities.Role;

public interface AdminDao {
	
	Admin findAdminByAccount(String account);
	
	Admin findAdminById(int id);

	void save(Admin admin);

	void delete(Admin admin);
	
	void delete(String account);
	
	boolean delete(int adminId);
	
	List<Admin> listAdmins(int from, int pageSize);
	
	long getCount();

	List<Admin> queryByNameAndAccount(String account, String name, int from, int pageSize);
	
	long getQueryCount(String account, String name);
	
	void save(Role role);
	
	void clearRoles();
	
	void deleteByStaffId(int staffId);
}
