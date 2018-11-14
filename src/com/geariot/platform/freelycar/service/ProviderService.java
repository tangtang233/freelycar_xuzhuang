package com.geariot.platform.freelycar.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geariot.platform.freelycar.dao.AdminDao;
import com.geariot.platform.freelycar.dao.ClearRecordDao;
import com.geariot.platform.freelycar.dao.ExpendOrderDao;
import com.geariot.platform.freelycar.dao.InventoryDao;
import com.geariot.platform.freelycar.dao.InventoryOrderDao;
import com.geariot.platform.freelycar.dao.ProviderDao;
import com.geariot.platform.freelycar.entities.ClearRecord;
import com.geariot.platform.freelycar.entities.ExpendOrder;
import com.geariot.platform.freelycar.entities.Inventory;
import com.geariot.platform.freelycar.entities.InventoryOrder;
import com.geariot.platform.freelycar.entities.InventoryOrderInfo;
import com.geariot.platform.freelycar.entities.Provider;
import com.geariot.platform.freelycar.model.RESCODE;
import com.geariot.platform.freelycar.utils.Constants;
import com.geariot.platform.freelycar.utils.JsonResFactory;

import net.sf.json.JSONArray;

@Service
@Transactional
public class ProviderService {

	@Autowired
	private ProviderDao providerDao;
	
	@Autowired
	private InventoryDao inventoryDao;
	
	@Autowired
	private InventoryOrderDao inventoryOrderDao;
	
	@Autowired
	private ClearRecordDao clearRecordDao;
	
	@Autowired
	private AdminDao adminDao;
	
	@Autowired
	private ExpendOrderDao expendOrderDao;
	
	public Map<String,Object> addProvider(Provider provider){
		provider.setCreateDate(new Date());
		providerDao.save(provider);
		long realSize = providerDao.getCount();
		Map<String, Object> jsonres = RESCODE.SUCCESS.getJSONRES();
		jsonres.put(Constants.RESPONSE_DATA_KEY, provider);
		jsonres.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
		return jsonres;
	}
	
	public Map<String,Object> modifyProvider(Provider provider){
		providerDao.modify(provider);
		return RESCODE.SUCCESS.getJSONRES(provider);
	}
	
	public String deleteProvider(Integer[] providerIds){
		int count = 0;
		for(int providerId : providerIds){
			if(providerDao.findProviderById(providerId) == null){
				count++;
			}
			else{
				//删除供应商需要把Inventory和InventoryOrderInfo中相应的Provider字段设置为空。
				for(Inventory inv : this.inventoryDao.findByProviderId(providerId)){
					inv.setProvider(null);
				}
				for(InventoryOrderInfo invOrder : this.inventoryOrderDao.findInfoByProviderId(providerId)){
					invOrder.setProvider(null);
				}
				providerDao.delete(providerId);
			}
		}
		if(count !=0){
			String tips = "共"+count+"条未在数据库中存在记录";
			net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.PART_SUCCESS , tips);
			long realSize = providerDao.getCount();
			obj.put(Constants.RESPONSE_REAL_SIZE_KEY,realSize);
			return obj.toString();
		}
		else{
			org.json.JSONObject obj = JsonResFactory.buildOrg(RESCODE.SUCCESS);
			long realSize = providerDao.getCount();
			obj.put(Constants.RESPONSE_REAL_SIZE_KEY,realSize);
			return obj.toString();
		}
	}
	
	public Map<String,Object> getProviderList(int page , int number){
		int from = (page - 1) * number;
		List<Provider> list = providerDao.listProviders(from, number);
		if(list == null || list.isEmpty()){
			return RESCODE.NOT_FOUND.getJSONRES();
		}
		
		long realSize = providerDao.getCount();
		int size=(int) Math.ceil(realSize/(float)number);
		return RESCODE.SUCCESS.getJSONRES(list, size, realSize);
	}
	
	public Map<String,Object> getSelectProvider(String name , int page , int number){
		int from = (page - 1) * number;
		List<Provider> list = providerDao.getConditionQuery(name, from, number);
		if(list == null || list.isEmpty()){
			return RESCODE.NOT_FOUND.getJSONRES();
		}
		
		long count = (long) providerDao.getConditionCount(name);
		int size=(int) Math.ceil(count/(double)number);
		return RESCODE.SUCCESS.getJSONRES(list,size,count);
	}
	
	public String getProviderName(){
		List<String> list = providerDao.listName();
		JSONArray jsonArray = JSONArray.fromObject(list);
		net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS, jsonArray);
		return obj.toString();
	}
	
	public Map<String,Object> getProviderById(int providerId){
		 Provider provider = providerDao.findProviderById(providerId);
		 return provider==null?RESCODE.NOT_FOUND.getJSONRES():RESCODE.SUCCESS.getJSONRES(provider);
	}
	
	public Map<String,Object> getInventroyOrder(int providerId,Date startTime, Date endTime, int page, int number){
		int from = (page - 1) * number;
		List<InventoryOrder> orders = inventoryOrderDao.queryConditions(providerId, startTime, endTime, from, number);
		if(orders == null){
			return RESCODE.NO_RECORD.getJSONRES();
		}else{
			long realSize = inventoryOrderDao.queryConditionsCount(providerId, startTime, endTime);
			int size=(int) Math.ceil(realSize/(double)number);
			return RESCODE.SUCCESS.getJSONRES(orders, size, realSize);
		}
	}
	
	
	public Map<String,Object> clearInventoryOrder(float clearAmount, String inStockDate, int providerId, int adminId, String... inventoryOrderIds){
		List<InventoryOrder> orders = inventoryOrderDao.findByIds(inventoryOrderIds);
		float price = 0f;
		float totalPrice = 0f;
		float tempClearAmount = clearAmount;
		for(InventoryOrder invOrder : orders){
			if(invOrder.getPayState() ==  0 ){
				price += invOrder.getNeedPayAmount();
				totalPrice = totalPrice + invOrder.getTotalPrice();
				continue;
			}else{
				if(invOrder.getNeedPayAmount() <= clearAmount){
					float tempAmount = invOrder.getNeedPayAmount();
					price += invOrder.getNeedPayAmount();
					totalPrice = totalPrice + invOrder.getTotalPrice();
					invOrder.setPayState(0);
					invOrder.setNeedPayAmount(0);
					invOrder.setPayDate(new Date());
					clearAmount = clearAmount - tempAmount;
				}else{
					float tempAmount = invOrder.getNeedPayAmount();
					price += invOrder.getNeedPayAmount();
					totalPrice = totalPrice + invOrder.getTotalPrice();
					invOrder.setPayDate(new Date());
					invOrder.setNeedPayAmount(tempAmount - clearAmount);
					invOrder.setPayState(1);
					clearAmount = 0;
					break;
				}
			}
		}
		//完成清算后 向数据库添加一条清算记录
		ClearRecord record = new ClearRecord();
		record.setClearPrice(tempClearAmount);
		if(tempClearAmount < price){
			record.setPayState(1);			
		}else{
			record.setPayState(0);			
		}
		record.setClearDate(new Date());
		record.setInStockDate(inStockDate);
		record.setProviderId(providerId);
		record.setTotalPrice(totalPrice);
		record.setOrderMaker(adminDao.findAdminById(adminId));
		clearRecordDao.save(record);
		
		//完成清算后,记录支出
		ExpendOrder expendOrder = new ExpendOrder();
		expendOrder.setAmount(tempClearAmount);
		expendOrder.setPayDate(new Date());
		expendOrder.setType("采购入库");
		expendOrderDao.save(expendOrder);
		return RESCODE.SUCCESS.getJSONRES();
	}
	
	public Map<String,Object> getClearRecord(int providerId, int page, int number){
		int from = (page - 1 ) * number;
		List<ClearRecord> clearRecords = clearRecordDao.recordsList(providerId, from, number);
		if(clearRecords == null || clearRecords.isEmpty()){
			return RESCODE.NOT_FOUND.getJSONRES();
		}else{  
			int realSize = clearRecordDao.getCount(providerId);
			int size=(int) Math.ceil(realSize/(double)number);
			return RESCODE.SUCCESS.getJSONRES(clearRecords, size, realSize);
		}
	}
}
