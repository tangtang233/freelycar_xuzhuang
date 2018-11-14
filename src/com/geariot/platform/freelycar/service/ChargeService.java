package com.geariot.platform.freelycar.service;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geariot.platform.freelycar.dao.ChargeDao;
import com.geariot.platform.freelycar.dao.ExpendOrderDao;
import com.geariot.platform.freelycar.entities.ExpendOrder;
import com.geariot.platform.freelycar.entities.OtherExpendOrder;
import com.geariot.platform.freelycar.entities.OtherExpendType;
import com.geariot.platform.freelycar.model.RESCODE;
import com.geariot.platform.freelycar.utils.DateHandler;
import com.geariot.platform.freelycar.utils.IDGenerator;
import com.geariot.platform.freelycar.utils.JsonResFactory;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

@Service
@Transactional
public class ChargeService {

	@Autowired
	private ChargeDao chargeDao;
	
	@Autowired
	private ExpendOrderDao expendOrderDao;
	
	public String addType(OtherExpendType otherExpendType){
		OtherExpendType exist = chargeDao.findByName(otherExpendType.getName());
		JSONObject obj = null;
		if(exist != null){
			obj = JsonResFactory.buildOrg(RESCODE.NAME_EXIST);
		}
		else{
			chargeDao.save(otherExpendType);
			obj = JsonResFactory.buildOrg(RESCODE.SUCCESS);
		}
		return obj.toString();
	}
	
	public String deleteType(int otherExpendTypeId){
		OtherExpendType exist = chargeDao.findById(otherExpendTypeId);
		JSONObject obj = null;
		if(exist == null){
			obj = JsonResFactory.buildOrg(RESCODE.NOT_FOUND);
		}
		else{
			chargeDao.delete(otherExpendTypeId);
			obj = JsonResFactory.buildOrg(RESCODE.SUCCESS);
		}
		return obj.toString();
	}
	
	public String listType(){
		List<OtherExpendType> list = chargeDao.listAll();
		if(list == null || list.isEmpty()){
			return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
		}
		JSONArray jsonArray = JSONArray.fromObject(list);
		net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS, jsonArray);
		return obj.toString();
	}
	
	public String addCharge(OtherExpendOrder otherExpendOrder){
		otherExpendOrder.setId(IDGenerator.generate(IDGenerator.CHARORDER_ID));
		otherExpendOrder.setCreateDate(new Date());
		otherExpendOrder.setTypeName(chargeDao.findById(otherExpendOrder.getTypeId()).getName());
		chargeDao.save(otherExpendOrder);
		//当有其他支出发生时,取信息存入expendOrder表
		ExpendOrder expendOrder = new ExpendOrder();
		expendOrder.setAmount(otherExpendOrder.getAmount());
		expendOrder.setPayDate(otherExpendOrder.getExpendDate());
		expendOrder.setType("其他支出");
		expendOrder.setReference(otherExpendOrder.getId());
		expendOrderDao.save(expendOrder);
		JsonConfig config = JsonResFactory.dateConfig();
		return JsonResFactory.buildNetWithData(RESCODE.SUCCESS,net.sf.json.JSONObject.fromObject(otherExpendOrder, config)).toString();
		
	}
	
	public String deleteCharge(String[] ids){
		int success = this.chargeDao.delete(Arrays.asList(ids));
		int suc = this.expendOrderDao.delete(Arrays.asList(ids));
		if(success < ids.length || suc < ids.length){
			return JsonResFactory.buildOrg(RESCODE.PART_SUCCESS).toString();
		}
		return JsonResFactory.buildOrg(RESCODE.SUCCESS).toString();
	}
	
	public Map<String,Object> selectCharge(int typeId , Date startTime , Date endTime, int page, int number){
		startTime = DateHandler.setTimeToBeginningOfDay(startTime);
		endTime = DateHandler.setTimeToBeginningOfDay(endTime);
		
		int from = (page - 1) * number;
		List<OtherExpendOrder> list = chargeDao.getConditionQuery(typeId, startTime, endTime, from, number);
		if(list == null || list.isEmpty()){
			return RESCODE.NOT_FOUND.getJSONRES();
		}
		long count = (long) chargeDao.getConditionCount(typeId, startTime, endTime);
		int size=(int) Math.ceil(count/(double)number);
		return RESCODE.SUCCESS.getJSONRES(list,size,count);
	}
}
