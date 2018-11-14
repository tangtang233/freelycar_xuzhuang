package com.geariot.platform.freelycar.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geariot.platform.freelycar.dao.ConsumOrderDao;
import com.geariot.platform.freelycar.dao.ExpendOrderDao;
import com.geariot.platform.freelycar.dao.IncomeOrderDao;
import com.geariot.platform.freelycar.entities.ExpendOrder;
import com.geariot.platform.freelycar.entities.IncomeOrder;
import com.geariot.platform.freelycar.model.RESCODE;
import com.geariot.platform.freelycar.utils.Constants;
import com.geariot.platform.freelycar.utils.DateJsonValueProcessor;
import com.geariot.platform.freelycar.utils.JsonResFactory;
import com.geariot.platform.freelycar.utils.query.MemberPayStat;
import com.geariot.platform.freelycar.utils.query.MonthStat;
import com.geariot.platform.freelycar.utils.query.PayMethodStat;
import com.geariot.platform.freelycar.utils.query.ProgramPayStat;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

@Service
@Transactional
public class StatService {

	@Autowired
	private ExpendOrderDao expendOrderDao;

	@Autowired
	private IncomeOrderDao incomeOrderDao;
	
	@Autowired
	private ConsumOrderDao consumOrderDao;

	public String getToday(int income, int expend, int page, int number) {
		int from = (page - 1) * number;
		if (income == 0 && expend == 1) {
			List<ExpendOrder> orders = expendOrderDao.listByDate();
			List<ExpendOrder> list = expendOrderDao.listByDate(from, number);
			if (list == null || list.isEmpty()) {
				return JsonResFactory.buildOrg(RESCODE.NO_RECORD).toString();
			}
			float expendStat = 0;
			if (orders == null || orders.isEmpty()) {
				expendStat = 0;
			} else {
				for (ExpendOrder expendOrder : orders) {
					expendStat = expendStat + expendOrder.getAmount();
				}
			}
			long realSize = (long) orders.size();
			JsonConfig config = new JsonConfig();
			config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor());
			JSONArray jsonArray = JSONArray.fromObject(list, config);
			net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS, jsonArray);
			obj.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
			obj.put("expendStat", expendStat);
			return obj.toString();
		} else if (income == 1 && expend == 0) {
			List<IncomeOrder> orders = incomeOrderDao.listByDate();
			List<IncomeOrder> list = incomeOrderDao.listByDate(from, number);
			if (list == null || list.isEmpty()) {
				return JsonResFactory.buildOrg(RESCODE.NO_RECORD).toString();
			}
			float incomeStat = 0;
			if (orders == null || orders.isEmpty()) {
				incomeStat = 0;
			} else {
				for (IncomeOrder incomeOrder : orders) {
					incomeStat = incomeStat + incomeOrder.getAmount();
				}
			}
			long realSize = (long) orders.size();
			JsonConfig config = new JsonConfig();
			config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor());
			JSONArray jsonArray = JSONArray.fromObject(list, config);
			net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS, jsonArray);
			obj.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
			obj.put("incomeStat", incomeStat);
			return obj.toString();
		} else if (income == 1 && expend == 1) {
			List<ExpendOrder> expendList = expendOrderDao.listByDate();
			List<IncomeOrder> incomeList = incomeOrderDao.listByDate();
			float incomeStat = 0;
			float expendStat = 0;
			if (expendList == null || expendList.isEmpty()) {
				expendStat = 0;
			} else {
				for (ExpendOrder expendOrder : expendList) {
					expendStat = expendStat + expendOrder.getAmount();
				}
			}
			if (incomeList == null || incomeList.isEmpty()) {
				incomeStat = 0;
			} else {
				for (IncomeOrder incomeOrder : incomeList) {
					incomeStat = incomeStat + incomeOrder.getAmount();
				}
			}
			JSONObject obj = JsonResFactory.buildOrg(RESCODE.SUCCESS);
			obj.put("incomeStat", incomeStat);
			obj.put("expendStat", expendStat);
			return obj.toString();
		} else {
			return JsonResFactory.buildOrg(RESCODE.WRONG_PARAM).toString();
		}

	}

	public String byMonth(Date month , int income, int expend, int page, int number) {
		if(month == null){
			 month = new Date();
		}
		int from = (page - 1) * number;
		if (income == 0 && expend == 1) {
			List<ExpendOrder> orders = expendOrderDao.listByMonth();
			List<ExpendOrder> list = expendOrderDao.listByMonth(from, number);
			if (list == null || list.isEmpty()) {
				return JsonResFactory.buildOrg(RESCODE.NO_RECORD).toString();
			}
			float expendStat = 0;
			if (orders == null || orders.isEmpty()) {
				expendStat = 0;
			} else {
				for (ExpendOrder expendOrder : orders) {
					expendStat = expendStat + expendOrder.getAmount();
				}
			}
			long realSize = (long) orders.size();
			JsonConfig config = new JsonConfig();
			config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor());
			JSONArray jsonArray = JSONArray.fromObject(list, config);
			net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS, jsonArray);
			obj.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
			obj.put("expendStat", expendStat);
			return obj.toString();
		} else if (income == 1 && expend == 0) {
			List<IncomeOrder> orders = incomeOrderDao.listByMonth();
			List<IncomeOrder> list = incomeOrderDao.listByMonth(from, number);
			if (list == null || list.isEmpty()) {
				return JsonResFactory.buildOrg(RESCODE.NO_RECORD).toString();
			}
			float incomeStat = 0;
			if (orders == null || orders.isEmpty()) {
				incomeStat = 0;
			} else {
				for (IncomeOrder incomeOrder : orders) {
					incomeStat = incomeStat + incomeOrder.getAmount();
				}
			}
			long realSize = (long) orders.size();
			JsonConfig config = new JsonConfig();
			config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor());
			JSONArray jsonArray = JSONArray.fromObject(list, config);
			net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS, jsonArray);
			obj.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
			obj.put("incomeStat", incomeStat);
			return obj.toString();
		} else if (income == 1 && expend == 1) {
			List<ExpendOrder> expendList = expendOrderDao.listByMonth();
			List<IncomeOrder> incomeList = incomeOrderDao.listByMonth();
			float expendStat = 0;
			if (expendList == null || expendList.isEmpty()) {
				expendStat = 0;
			} else {
				for (ExpendOrder expendOrder : expendList) {
					expendStat = expendStat + expendOrder.getAmount();
				}
			}
			float incomeStat = 0;
			if (incomeList == null || incomeList.isEmpty()) {
				incomeStat = 0;
			} else {
				for (IncomeOrder incomeOrder : incomeList) {
					incomeStat = incomeStat + incomeOrder.getAmount();
				}
			}
			JSONObject obj = JsonResFactory.buildOrg(RESCODE.SUCCESS);
			obj.put("incomeStat", incomeStat);
			obj.put("expendStat", expendStat);
			return obj.toString();
		} else {
			return JsonResFactory.buildOrg(RESCODE.WRONG_PARAM).toString();
		}

	}

	public String monthlyByYear(Date selectYear) {
		Calendar start = Calendar.getInstance();
		Calendar end = Calendar.getInstance();
		start.setTime(selectYear);
		end.setTime(selectYear);
		start.set(Calendar.MONTH, 0);
		start.set(Calendar.DAY_OF_MONTH, 1);
		end.set(Calendar.MONTH, 11);
		end.set(Calendar.DAY_OF_MONTH, 31);
		List<Object[]> rss = this.incomeOrderDao.listMonthStat(start.getTime(), end.getTime());
		List<MonthStat> list = new ArrayList<>();
		for (Object[] rs : rss) {
			list.add(new MonthStat(Float.valueOf(String.valueOf(rs[0])), Float.valueOf(String.valueOf(rs[1])),
					String.valueOf(rs[2])));
		}
		return JsonResFactory.buildNetWithData(RESCODE.SUCCESS, net.sf.json.JSONArray.fromObject(list)).toString();
	}

	public String weeklyStatDetail(int income, int expend, int page, int number) {
		int from = (page - 1) * number;
		if (income == 0 && expend == 1) {
			List<ExpendOrder> orders = expendOrderDao.listByWeek();
			List<ExpendOrder> list = expendOrderDao.listByWeek(from, number);
			if (list == null || list.isEmpty()) {
				return JsonResFactory.buildOrg(RESCODE.NO_RECORD).toString();
			}
			float expendStat = 0;
			if (orders == null || orders.isEmpty()) {
				expendStat = 0;
			} else {
				for (ExpendOrder expendOrder : orders) {
					expendStat = expendStat + expendOrder.getAmount();
				}
			}
			long realSize = (long) orders.size();
			JsonConfig config = new JsonConfig();
			config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor());
			JSONArray jsonArray = JSONArray.fromObject(list, config);
			net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS, jsonArray);
			obj.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
			obj.put("expendStat", expendStat);
			return obj.toString();
		} else if (income == 1 && expend == 0) {
			List<IncomeOrder> orders = incomeOrderDao.listByWeek();
			List<IncomeOrder> list = incomeOrderDao.listByWeek(from, number);
			if (list == null || list.isEmpty()) {
				return JsonResFactory.buildOrg(RESCODE.NO_RECORD).toString();
			}
			float incomeStat = 0;
			if (orders == null || orders.isEmpty()) {
				incomeStat = 0;
			} else {
				for (IncomeOrder incomeOrder : orders) {
					incomeStat = incomeStat + incomeOrder.getAmount();
				}
			}
			long realSize = (long) orders.size();
			JsonConfig config = new JsonConfig();
			config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor());
			JSONArray jsonArray = JSONArray.fromObject(list, config);
			net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS, jsonArray);
			obj.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
			obj.put("incomeStat", incomeStat);
			return obj.toString();
		} else {
			return JsonResFactory.buildOrg(RESCODE.WRONG_PARAM).toString();
		}
	}
	
	//日期区间查询
	public String selectDate(Date startTime , Date endTime , int income , int expend , int page , int number){
		int from = (page - 1) * number;
		if (income == 0 && expend == 1) {
			List<ExpendOrder> orders = expendOrderDao.listByDateRange(startTime, endTime);
			List<ExpendOrder> list = expendOrderDao.listByDateRange(startTime, endTime, from, number);
			if (list == null || list.isEmpty()) {
				return JsonResFactory.buildOrg(RESCODE.NO_RECORD).toString();
			}
			float expendStat = 0;
			if (orders == null || orders.isEmpty()) {
				expendStat = 0;
			} else {
				for (ExpendOrder expendOrder : orders) {
					expendStat = expendStat + expendOrder.getAmount();
				}
			}
			long realSize = (long) orders.size();
			JsonConfig config = new JsonConfig();
			config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor());
			JSONArray jsonArray = JSONArray.fromObject(list, config);
			net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS, jsonArray);
			obj.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
			obj.put("expendStat", expendStat);
			return obj.toString();
		} else if (income == 1 && expend == 0) {
			List<IncomeOrder> orders = incomeOrderDao.listByDateRange(startTime, endTime);
			List<IncomeOrder> list = incomeOrderDao.listByDateRange(startTime, endTime, from, number);
			if (list == null || list.isEmpty()) {
				return JsonResFactory.buildOrg(RESCODE.NO_RECORD).toString();
			}
			float incomeStat = 0;
			if (orders == null || orders.isEmpty()) {
				incomeStat = 0;
			} else {
				for (IncomeOrder incomeOrder : orders) {
					incomeStat = incomeStat + incomeOrder.getAmount();
				}
			}
			long realSize = (long) orders.size();
			JsonConfig config = new JsonConfig();
			config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor());
			JSONArray jsonArray = JSONArray.fromObject(list, config);
			net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS, jsonArray);
			obj.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
			obj.put("incomeStat", incomeStat);
			return obj.toString();
		} else if (income == 1 && expend == 1) {
			List<ExpendOrder> expendList = expendOrderDao.listByDateRange(startTime, endTime);
			List<IncomeOrder> incomeList = incomeOrderDao.listByDateRange(startTime, endTime);
			float incomeStat = 0;
			float expendStat = 0;
			if (expendList == null || expendList.isEmpty()) {
				expendStat = 0;
			} else {
				for (ExpendOrder expendOrder : expendList) {
					expendStat = expendStat + expendOrder.getAmount();
				}
			}
			if (incomeList == null || incomeList.isEmpty()) {
				incomeStat = 0;
			} else {
				for (IncomeOrder incomeOrder : incomeList) {
					incomeStat = incomeStat + incomeOrder.getAmount();
				}
			}
			JSONObject obj = JsonResFactory.buildOrg(RESCODE.SUCCESS);
			obj.put("incomeStat", incomeStat);
			obj.put("expendStat", expendStat);
			return obj.toString();
		} else {
			return JsonResFactory.buildOrg(RESCODE.WRONG_PARAM).toString();
		}
	}
	
	public String payMethodToday() {
		List<IncomeOrder> incomeOrders = incomeOrderDao.listByDate();
		if(incomeOrders == null || incomeOrders.isEmpty()){
			return JsonResFactory.buildOrg(RESCODE.NO_INCOME).toString();
		}
		else{
			List<Object[]> payMethods = this.incomeOrderDao.listByPayMethodToday();
			List<PayMethodStat> payMethodDetail = new ArrayList<>();
			for (Object[] payMethod : payMethods) {
				payMethodDetail.add(new PayMethodStat(String.valueOf(payMethod[1]), Float.valueOf(String.valueOf(payMethod[0]))));
			}
			List<ProgramPayStat> programNames = this.consumOrderDao.programNameToday();
			List<Object[]> objects = this.incomeOrderDao.MemberPayToday();
			List<MemberPayStat> memberPayStats = new ArrayList<>();
			for(Object[] object : objects){
				memberPayStats.add(new MemberPayStat(Boolean.valueOf(String.valueOf(object[1])), Float.valueOf(String.valueOf(object[0]))));
			}
		net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS, net.sf.json.JSONArray.fromObject(payMethodDetail));
		obj.put("programPayDetail", programNames);
		obj.put("memberPay", memberPayStats);
		return obj.toString();
		}
	}
	
	public String payMethodMonth() {
		List<IncomeOrder> incomeOrders = incomeOrderDao.listByMonth();
		if(incomeOrders == null || incomeOrders.isEmpty()){
			return JsonResFactory.buildOrg(RESCODE.NO_INCOME).toString();
		}
		else{
			List<Object[]> payMethods = this.incomeOrderDao.listByPayMethodMonth();
			List<PayMethodStat> payMethodDetail = new ArrayList<>();
			for (Object[] payMethod : payMethods) {
				payMethodDetail.add(new PayMethodStat(String.valueOf(payMethod[1]), Float.valueOf(String.valueOf(payMethod[0]))));
			}
			List<ProgramPayStat> programNames = this.consumOrderDao.programNameMonth();
			List<Object[]> objects = this.incomeOrderDao.MemberPayMonth();
			List<MemberPayStat> memberPayStats = new ArrayList<>();
			for(Object[] object : objects){
				memberPayStats.add(new MemberPayStat(Boolean.valueOf(String.valueOf(object[1])), Float.valueOf(String.valueOf(object[0]))));
			}
		net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS, net.sf.json.JSONArray.fromObject(payMethodDetail));
		obj.put("programPayDetail", programNames);
		obj.put("memberPay", memberPayStats);
		return obj.toString();
		}
}
	
	public String payMethodRange(Date startTime , Date endTime) {
		List<IncomeOrder> incomeOrders = incomeOrderDao.listByDateRange(startTime, endTime);
		if(incomeOrders == null || incomeOrders.isEmpty()){
			return JsonResFactory.buildOrg(RESCODE.NO_INCOME).toString();
		}
		else{
			List<Object[]> payMethods = this.incomeOrderDao.listByPayMethodRange(startTime, endTime);
			List<PayMethodStat> payMethodDetail = new ArrayList<>();
			for (Object[] payMethod : payMethods) {
				payMethodDetail.add(new PayMethodStat(String.valueOf(payMethod[1]), Float.valueOf(String.valueOf(payMethod[0]))));
			}
			List<ProgramPayStat> programNames = this.consumOrderDao.programNameRange(startTime, endTime);
			List<Object[]> objects = this.incomeOrderDao.MemberPayRange(startTime, endTime);
			List<MemberPayStat> memberPayStats = new ArrayList<>();
			for(Object[] object : objects){
				memberPayStats.add(new MemberPayStat(Boolean.valueOf(String.valueOf(object[1])), Float.valueOf(String.valueOf(object[0]))));
			}
		net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS, net.sf.json.JSONArray.fromObject(payMethodDetail));
		obj.put("programPayDetail", programNames);
		obj.put("memberPay", memberPayStats);
		return obj.toString();
		}
	}
	
}
