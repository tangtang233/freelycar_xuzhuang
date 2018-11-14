package com.geariot.platform.freelycar.service;

import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geariot.platform.freelycar.dao.ProgramDao;
import com.geariot.platform.freelycar.dao.ProjectDao;
import com.geariot.platform.freelycar.entities.Program;
import com.geariot.platform.freelycar.model.RESCODE;
import com.geariot.platform.freelycar.utils.Constants;
import com.geariot.platform.freelycar.utils.DateJsonValueProcessor;
import com.geariot.platform.freelycar.utils.JsonResFactory;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

@Service
@Transactional
public class ProgramService {

	@Autowired
	private ProgramDao programDao;
	
	@Autowired
	private ProjectDao projectDao;
	
	public String addProgram(Program program){
		Program exist = programDao.findProgramByName(program.getName());
		if(exist != null){
			return JsonResFactory.buildOrg(RESCODE.ACCOUNT_EXIST).toString();
		}
		else {
			program.setCreateDate(new Date());
			programDao.save(program);
			JsonConfig config = JsonResFactory.dateConfig();
			return JsonResFactory.buildNetWithData(RESCODE.SUCCESS,net.sf.json.JSONObject.fromObject(program, config)).toString();
		}
		
	}
	
	public String getProgramList(int page , int number){
		int from = (page - 1) * number;
		List<Program> list = programDao.listPrograms(from, number);
		if(list == null || list.isEmpty()){
			return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
		}
		long realSize = programDao.getCount();
		int size=(int) Math.ceil(realSize/(double)number);
		JsonConfig config = new JsonConfig();
		config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor());
		JSONArray jsonArray = JSONArray.fromObject(list, config);
		net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS, jsonArray);
		obj.put(Constants.RESPONSE_SIZE_KEY, size);
		return obj.toString();
	}
	
	public String deleteProgram(Integer... programIds){
		int count = 0;
		for(int programId : programIds){
			Program exist = programDao.findProgramByProgramId(programId);
			if(exist == null){
				count++;
			}
			else{
				programDao.delete(programId);
				projectDao.deleteByprogramId(programId);	
			}
		}
		if(count !=0){
			String tips = "共"+count+"条未在数据库中存在记录";
			net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.PART_SUCCESS , tips);
			long realSize = programDao.getCount();
			obj.put(Constants.RESPONSE_REAL_SIZE_KEY,realSize);
			return obj.toString();
		}
		else{
			JSONObject obj = JsonResFactory.buildOrg(RESCODE.SUCCESS);
			long realSize = programDao.getCount();
			obj.put(Constants.RESPONSE_REAL_SIZE_KEY,realSize);
			return obj.toString();
		}
	}
	
	public String getProgramList(){
		List<Program> list = programDao.listAll();
		JsonConfig config = new JsonConfig();
		config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor());
		JSONArray jsonArray = JSONArray.fromObject(list, config);
		net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS, jsonArray);
		return obj.toString();
	}
	
}
