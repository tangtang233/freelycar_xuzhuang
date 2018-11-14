package com.geariot.platform.freelycar.dao;

import java.util.List;

import com.geariot.platform.freelycar.entities.Project;
import com.geariot.platform.freelycar.entities.ProjectInfo;

public interface ProjectDao {
	
	Project findProjectByName(String name);
	
	Project findProjectById(int projectId);
	
	void save(Project project);

    void saveOrUpdate(Project project);

    void delete(Project project);
	
	void delete(int projectId);
	
	void deleteByprogramId(int programId);
	
	long getCount();
	
	List<Project> listProjects(int from , int pageSize);
	
	List<Object[]> getProjectName();
	
	List<Project> getConditionQuery(String name, String programId , int from , int pageSize);
	
	long getConditionCount(String name, String programId);
	
	long countInventoryByIds(List<String> inventoryIds);
	
	void deleteInventory(int projectId);
	
	void updateProjectInfo(ProjectInfo projectInfo);
}
