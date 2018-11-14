package com.geariot.platform.freelycar.controller;

import com.geariot.platform.freelycar.entities.Staff;
import com.geariot.platform.freelycar.service.StaffService;
import com.geariot.platform.freelycar.shiro.PermissionRequire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/staff")
public class StaffController {

	@Autowired
	private StaffService staffService;
	
	@RequestMapping(value = "/add" , method = RequestMethod.POST)
	@PermissionRequire("staff:add")
	public String addStaff(Staff staff){
		return staffService.addStaff(staff);
	}
	
	@RequestMapping(value = "/modify" , method = RequestMethod.POST)
	@PermissionRequire("staff:modify")
	public String modifyStaff(Staff staff){
		return staffService.modifyStaff(staff);
	}
	
	@RequestMapping(value = "/delete" , method = RequestMethod.POST)
	@PermissionRequire("staff:delete")
	public String deleteStaff(Integer... staffIds ){
		return staffService.deleteStaff(staffIds);
	}
	
	@RequestMapping(value = "/list" , method = RequestMethod.GET)
	@PermissionRequire("staff:query")
	public String getStaffList(int page , int number){
		return staffService.getStaffList(page, number);
	}
	
	@RequestMapping(value = "/query" , method = RequestMethod.GET)
	@PermissionRequire("staff:query")
	public Map<String,Object> getSelectStaff(String staffId , String staffName , int page , int number){
		return staffService.getSelectStaff(staffId, staffName, page, number);
	}
	
	@RequestMapping(value = "/detail" , method = RequestMethod.GET)
	@PermissionRequire("staff:detail")
	public String staffServiceDetail(int staffId , int page , int number){
		return staffService.staffServiceDetail(staffId, page, number);
	}

	@RequestMapping(value="/openOrCloseStaffLoginAccount", method=RequestMethod.POST)
	public String openOrCloseStaffLoginAccount(Staff staff){
		return staffService.openOrCloseStaffLoginAccount(staff);
	}
}
