package com.geariot.platform.freelycar.service;

import com.geariot.platform.freelycar.dao.*;
import com.geariot.platform.freelycar.entities.*;
import com.geariot.platform.freelycar.model.ConsumHist;
import com.geariot.platform.freelycar.model.RESCODE;
import com.geariot.platform.freelycar.utils.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ClientService {

	@Autowired
	private ClientDao clientDao;

	@Autowired
	private CarDao carDao;

	@Autowired
	private InsuranceOrderDao insuranceOrderDao;

	@Autowired
	private ConsumOrderDao consumOrderDao;

	@Autowired
	private IncomeOrderDao incomeOrderDao;

	public String list(int page, int number) {
		int from = (page - 1) * number;
		List<Client> list = clientDao.list(from, number);
		if (list == null || list.isEmpty()) {
			return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
		}
		long realSize = clientDao.getCount();
		int size = (int) Math.ceil(realSize / (double) number);
		JsonConfig config = JsonResFactory.dateConfig();
		config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor());
		JsonPropertyFilter filter = new JsonPropertyFilter(Client.class);
		/* filter.setColletionProperties(CarType.class); */
		config.setJsonPropertyFilter(filter);
		config.registerPropertyExclusions(Admin.class,
				new String[] { "password", "role", "current", "createDate", "comment" });
		JSONArray jsonArray = JSONArray.fromObject(list, config);
		net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS, jsonArray);
		obj.put(Constants.RESPONSE_SIZE_KEY, size);
		obj.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
		return obj.toString();
	}

	public String add(Client client) {
		if (client.getPhone() != null && !client.getPhone().isEmpty() && !client.getPhone().trim().isEmpty()) {
			Client exist = clientDao.findByPhone(client.getPhone());
			if (exist != null) {
				return JsonResFactory.buildOrg(RESCODE.PHONE_EXIST).toString();
			}
			if (client.getCars() != null) {
				for (Car car : client.getCars()) {
					if (carDao.findByLicense(car.getLicensePlate()) != null) {
						return JsonResFactory.buildOrg(RESCODE.CAR_LICENSE_EXIST).toString();
					}
					car.setCreateDate(new Date());
					car.setClient(client);
				}
			}
			client.setCreateDate(new Date());
			client.setIsMember(false);
			clientDao.save(client);
			Client added = this.carDao.findByLicense(client.getCars().iterator().next().getLicensePlate()).getClient();
			JsonConfig config = JsonResFactory.dateConfig(Collection.class);
			return JsonResFactory.buildNetWithData(RESCODE.SUCCESS, net.sf.json.JSONObject.fromObject(added, config))
					.toString();
		} else {

			String name = client.getName();
			if (CommonUtils.isEmpty(name)) {
				name = "未知";
			}

			if (client.getCars() != null) {
				for (Car car : client.getCars()) {
					if (carDao.findByLicense(car.getLicensePlate()) != null) {
						return JsonResFactory.buildOrg(RESCODE.CAR_LICENSE_EXIST).toString();
					}

					client.setName(name);
					client.setPhone(car.getLicensePlate());
					car.setCreateDate(new Date());
					car.setClient(client);
				}
			}
			client.setCreateDate(new Date());
			client.setIsMember(false);
			clientDao.save(client);
			Client added = this.carDao.findByLicense(client.getCars().iterator().next().getLicensePlate()).getClient();
			JsonConfig config = JsonResFactory.dateConfig(Collection.class);
			return JsonResFactory.buildNetWithData(RESCODE.SUCCESS, net.sf.json.JSONObject.fromObject(added, config))
					.toString();
		}
	}

	public String modify(Client client) {
		clientDao.update(client);
		return JsonResFactory.buildOrg(RESCODE.SUCCESS).toString();
	}

	public String delete(List<Integer> clientIds) {
		this.clientDao.delete(clientIds);
		return JsonResFactory.buildOrg(RESCODE.SUCCESS).toString();
	}

	public Map<String, Object> query(String name, String phone, String licensePlate,int isMember, int page, int number) {
		int from = (page - 1) * number;
		if (licensePlate == null || licensePlate.isEmpty() || licensePlate.trim().isEmpty()) {
			List<Client> list = clientDao.queryClient(name, phone, isMember, from, number);
			if (list == null || list.isEmpty()) {
				return RESCODE.NOT_FOUND.getJSONRES();
			}
			long realSize = this.clientDao.getQueryClientCount(name, phone, isMember);
			int size = (int) Math.ceil(realSize / (double) number);
			return RESCODE.SUCCESS.getJSONRES(list, size, realSize);
		} else {
			List<Car> list = clientDao.query(name, phone, licensePlate,from, number);
			if (!list.isEmpty()) {
				List<Integer> clientIds = new ArrayList<Integer>();
				for (Car car : list) {
					clientIds.add(car.getClient().getId());
				}
				List<Client> clist = clientDao.carQuery(clientIds, isMember);
				if (clist == null || clist.isEmpty()) {
					return RESCODE.NOT_FOUND.getJSONRES();
				}
				long realSize = this.clientDao.getCarQueryCount(clientIds, isMember);
				int size = (int) Math.ceil(realSize / (double) number);

				return RESCODE.SUCCESS.getJSONRES(clist, size, realSize);
			}
		}
		return RESCODE.NOT_FOUND.getJSONRES();
	}

	public String detail(int clientId) {
		int payMethod = 0;
		Client client = clientDao.findById(clientId);
		if (client == null) {
			return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
		}
		JsonConfig config = JsonResFactory.dateConfig();
		// config.registerPropertyExclusion(Car.class, "client");
		// config.registerPropertyExclusion(CarBrand.class, "types");
		JsonPropertyFilter filter = new JsonPropertyFilter(Client.class);
		/* filter.setColletionProperties(CarType.class); */
		config.setJsonPropertyFilter(filter);
		JSONObject obj = JsonResFactory.buildNet(RESCODE.SUCCESS, Constants.RESPONSE_CLIENT_KEY,
				JSONObject.fromObject(client, config));

		// 修改成和具体信息一样的格式
		List<ConsumOrder> list = this.consumOrderDao.queryByClientIdAll(clientId);
		List<IncomeOrder> incomeList = this.incomeOrderDao.listByClientIdAll(clientId);
		List<ConsumHist> consumHists = new ArrayList<>();
		ConsumHist consumHist = null;
		int id = 0;
		if (list != null) {
			for (ConsumOrder consumOrder : list) {
				String project = "";
				if (consumOrder.getPayMethod().contains("$")) {
					payMethod = Integer.parseInt(consumOrder.getPayMethod().substring(0, 1));
				} else {
					payMethod = Integer.parseInt(consumOrder.getPayMethod());
				}
				float consumAmount = 0f;
				for (ProjectInfo projectInfo : consumOrder.getProjects()) {
					project = project + projectInfo.getName() + "、";
//					consumAmount = consumAmount + projectInfo.getPresentPrice();
				}
				//每一条订单的金额按照订单的实付金额显示，不需要逐条进行合计
				consumAmount = consumOrder.getPresentPrice();

				int length = project.length();
				project = "".equals(project) ? "" : project.substring(0, length - 1);
				consumHist = new ConsumHist();
				consumHist.setId(++id);
				consumHist.setConsumAmount(consumAmount);
				consumHist.setPayMethod(payMethod);
				consumHist.setProject(project);
				consumHist.setServiceDate(consumOrder.getCreateDate());
				consumHists.add(consumHist);
			}
		}
		if (incomeList != null) {
			for (IncomeOrder incomeOrder : incomeList) {
				consumHist = new ConsumHist();
				consumHist.setId(++id);
				consumHist.setProject(incomeOrder.getProgramName());
				consumHist.setConsumAmount(incomeOrder.getAmount());
				consumHist.setPayMethod(incomeOrder.getPayMethod());
				consumHist.setServiceDate(incomeOrder.getPayDate());
				consumHists.add(consumHist);
			}
		}
		Collections.sort(consumHists, new Comparator<ConsumHist>() {
			@Override
			public int compare(ConsumHist o1, ConsumHist o2) {
				if(o1.getServiceDate().after(o2.getServiceDate())){
					return 1;
				}
				if(o1.getServiceDate().equals(o2.getServiceDate())){
					if(o1.getConsumAmount() > o2.getConsumAmount()){
						
						return 1;
					}
					if(o1.getConsumAmount() == o2.getConsumAmount()){
						return 0;
					}
					if(o1.getConsumAmount() < o2.getConsumAmount()){
						return -1;
					}
				}
				return 1;
			}
		});
		// List<ConsumOrder> consumHist =
		// this.consumOrderDao.findWithClientId(clientId);
		// config.registerPropertyExclusions(Admin.class, new
		// String[]{"password", "role", "current", "createDate", "comment"});
		if (consumHists != null) {
			obj.put(Constants.RESPONSE_DATA_KEY, JSONArray.fromObject(consumHists, config));
		}
		return obj.toString();
	}

	public String addCar(Car car) {
		Client client = clientDao.findById(car.getClient().getId());
		if (client == null) {
			return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
		}
		Car exist = carDao.findByLicense(car.getLicensePlate());
		if (exist != null) {
			return JsonResFactory.buildOrg(RESCODE.CAR_LICENSE_EXIST).toString();
		}
		car.setCreateDate(new Date());
		if (client.getCars() != null && !client.getCars().isEmpty()) {
			car.setDefaultCar(false);
			client.getCars().add(car);
		} else {
			car.setDefaultCar(true);
			client.getCars().add(car);
		}
		return JsonResFactory.buildOrg(RESCODE.SUCCESS).toString();
	}

	public String deleteCar(int carId) {
		carDao.deleteById(carId);
		return JsonResFactory.buildOrg(RESCODE.SUCCESS).toString();
	}

	public String getClientNames(String name) {
		List<String> names = this.clientDao.getClientNames(name);
		return JsonResFactory.buildNetWithData(RESCODE.SUCCESS, net.sf.json.JSONArray.fromObject(names)).toString();
	}

	public String stat() {
		// 会员总数
		long all = this.clientDao.getQueryCount(null, null);

		long thisMonth = this.clientDao.getQueryCount(DateHandler.setTimeToBeginningOfMonth(),
				DateHandler.setTimeToEndOfMonth());
		// 本日新增
		long today = this.clientDao.getQueryCount(DateHandler.setTimeToBeginningOfDay(),
				DateHandler.setTimeToEndofDay());

		org.json.JSONObject res = JsonResFactory.buildOrg(RESCODE.SUCCESS);
		res.put(Constants.RESPONSE_REAL_SIZE_KEY, all);
		res.put("thisMonth", thisMonth);
		res.put("today", today);
		return res.toString();
	}

	public String consumHistoryToday(int clientId, int page, int number) {
		int payMethod = 0;
		int from = (page - 1) * number;
		List<ConsumOrder> list = this.consumOrderDao.queryByClientIdToday(clientId, from, number);
		List<IncomeOrder> incomeList = this.incomeOrderDao.listByClientIdToday(clientId, from, number);
		if ((list == null || list.isEmpty()) && (incomeList == null || incomeList.isEmpty())) {
			return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
		}
		List<ConsumHist> consumHists = new ArrayList<>();
		ConsumHist consumHist = null;
		int id = 0;
		if (list != null) {
			for (ConsumOrder consumOrder : list) {
				String project = "";
				if (consumOrder.getPayMethod().contains("$")) {
					payMethod = Integer.parseInt(consumOrder.getPayMethod().substring(0, 1));
				} else {
					payMethod = Integer.parseInt(consumOrder.getPayMethod());
				}
				float consumAmount = 0f;
				for (ProjectInfo projectInfo : consumOrder.getProjects()) {
					project = project + projectInfo.getName() + "、";
					consumAmount = consumAmount + projectInfo.getPresentPrice();
				}
				int length = project.length();
				project = project.substring(0, length - 1);
				consumHist = new ConsumHist();
				consumHist.setId(++id);
				consumHist.setConsumAmount(consumAmount);
				consumHist.setPayMethod(payMethod);
				consumHist.setProject(project);
				consumHist.setServiceDate(consumOrder.getCreateDate());
				consumHists.add(consumHist);
			}
		}
		if (incomeList != null) {
			for (IncomeOrder incomeOrder : incomeList) {
				consumHist = new ConsumHist();
				consumHist.setId(++id);
				consumHist.setProject(incomeOrder.getProgramName());
				consumHist.setConsumAmount(incomeOrder.getAmount());
				consumHist.setPayMethod(incomeOrder.getPayMethod());
				consumHist.setServiceDate(incomeOrder.getPayDate());
				consumHists.add(consumHist);
			}
		}
		Collections.sort(consumHists, new Comparator<ConsumHist>() {
			@Override
			public int compare(ConsumHist o1, ConsumHist o2) {
				if(o1.getServiceDate().after(o2.getServiceDate())){
					return 1;
				}
				if(o1.getServiceDate().equals(o2.getServiceDate())){
					if(o1.getConsumAmount() > o2.getConsumAmount()){
						
						return 1;
					}
					if(o1.getConsumAmount() == o2.getConsumAmount()){
						return 0;
					}
					if(o1.getConsumAmount() < o2.getConsumAmount()){
						return -1;
					}
				}
				return 1;
			}
		});
		long realSize = consumHists.size();
		int size = (int) Math.ceil(realSize / (double) number);
		// 遍历list中projectinfo集合,将现价总计 返回
		float amount = 0f;
		for (ConsumHist hist : consumHists) {
			amount = amount + hist.getConsumAmount();
		}
		net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS,
				net.sf.json.JSONArray.fromObject(consumHists, JsonResFactory.dateConfig()));
		obj.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
		obj.put(Constants.RESPONSE_SIZE_KEY, size);
		obj.put(Constants.RESPONSE_AMOUNT_KEY, amount);
		return obj.toString();
	}

	public String consumHistoryMonth(int clientId, int page, int number) {
		int payMethod = 0;
		int from = (page - 1) * number;
		ConsumHist consumHist = null;
		List<ConsumOrder> list = this.consumOrderDao.queryByClientIdMonth(clientId, from, number);
		List<IncomeOrder> incomeList = this.incomeOrderDao.listByClientIdMonth(clientId, from, number);
		if ((list == null || list.isEmpty()) && (incomeList == null || incomeList.isEmpty())) {
			return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
		}
		List<ConsumHist> consumHists = new ArrayList<>();
		int id = 0;
		if (list != null) {
			for (ConsumOrder consumOrder : list) {
				String project = "";
				if (consumOrder.getPayMethod().contains("$")) {
					payMethod = Integer.parseInt(consumOrder.getPayMethod().substring(0, 1));
				} else {
					payMethod = Integer.parseInt(consumOrder.getPayMethod());
				}
				float consumAmount = 0f;
				for (ProjectInfo projectInfo : consumOrder.getProjects()) {
					project = project + projectInfo.getName() + "、";
					consumAmount = consumAmount + projectInfo.getPresentPrice();
				}
				int length = project.length();
				project = project.substring(0, length - 1);
				consumHist = new ConsumHist();
				consumHist.setId(++id);
				consumHist.setConsumAmount(consumAmount);
				consumHist.setPayMethod(payMethod);
				consumHist.setProject(project);
				consumHist.setServiceDate(consumOrder.getCreateDate());
				consumHists.add(consumHist);
			}
		}
		if (incomeList != null) {
			for (IncomeOrder incomeOrder : incomeList) {
				consumHist = new ConsumHist();
				consumHist.setId(++id);
				consumHist.setProject(incomeOrder.getProgramName());
				consumHist.setConsumAmount(incomeOrder.getAmount());
				consumHist.setPayMethod(incomeOrder.getPayMethod());
				consumHist.setServiceDate(incomeOrder.getPayDate());
				consumHists.add(consumHist);
			}
		}
		Collections.sort(consumHists, new Comparator<ConsumHist>() {
			@Override
			public int compare(ConsumHist o1, ConsumHist o2) {
				if(o1.getServiceDate().after(o2.getServiceDate())){
					return 1;
				}
				if(o1.getServiceDate().equals(o2.getServiceDate())){
					if(o1.getConsumAmount() > o2.getConsumAmount()){
						
						return 1;
					}
					if(o1.getConsumAmount() == o2.getConsumAmount()){
						return 0;
					}
					if(o1.getConsumAmount() < o2.getConsumAmount()){
						return -1;
					}
				}
				return 1;
			}
		});
		long realSize = consumHists.size();
		int size = (int) Math.ceil(realSize / (double) number);
		// 遍历list中projectinfo集合,将现价总计 返回
		float amount = 0f;
		for (ConsumHist hist : consumHists) {
			amount = amount + hist.getConsumAmount();
		}
		net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS,
				net.sf.json.JSONArray.fromObject(consumHists, JsonResFactory.dateConfig()));
		obj.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
		obj.put(Constants.RESPONSE_SIZE_KEY, size);
		obj.put(Constants.RESPONSE_AMOUNT_KEY, amount);
		return obj.toString();
	}

	public String consumHistoryDate(int clientId, int page, int number, Date startTime, Date endTime) {
		int payMethod = 0;
		if (startTime == null && endTime == null) {
			ConsumHist consumHist = null;
			List<ConsumOrder> list = this.consumOrderDao.queryByClientIdAll(clientId);
			List<IncomeOrder> incomeList = this.incomeOrderDao.listByClientIdAll(clientId);
			if ((list == null || list.isEmpty()) && (incomeList == null || incomeList.isEmpty())) {
				return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
			}
			List<ConsumHist> consumHists = new ArrayList<>();
			int id = 0;
			if (list != null) {
				for (ConsumOrder consumOrder : list) {
					String project = "";
					if (consumOrder.getPayMethod().contains("$")) {
						payMethod = Integer.parseInt(consumOrder.getPayMethod().substring(0, 1));
					} else {
						payMethod = Integer.parseInt(consumOrder.getPayMethod());
					}
					float consumAmount = 0f;
					for (ProjectInfo projectInfo : consumOrder.getProjects()) {
						project = project + projectInfo.getName() + "、";
						consumAmount = consumAmount + projectInfo.getPresentPrice();
					}
					int length = project.length();
					project = project.substring(0, length - 1);
					consumHist = new ConsumHist();
					consumHist.setId(++id);
					consumHist.setConsumAmount(consumAmount);
					consumHist.setPayMethod(payMethod);
					consumHist.setProject(project);
					consumHist.setServiceDate(consumOrder.getCreateDate());
					consumHists.add(consumHist);
				}
			}
			if (incomeList != null) {
				for (IncomeOrder incomeOrder : incomeList) {
					consumHist = new ConsumHist();
					consumHist.setId(++id);
					consumHist.setProject(incomeOrder.getProgramName());
					consumHist.setConsumAmount(incomeOrder.getAmount());
					consumHist.setPayMethod(incomeOrder.getPayMethod());
					consumHist.setServiceDate(incomeOrder.getPayDate());
					consumHists.add(consumHist);
				}
			}
			Collections.sort(consumHists, new Comparator<ConsumHist>() {
				@Override
				public int compare(ConsumHist o1, ConsumHist o2) {
					if(o1.getServiceDate().after(o2.getServiceDate())){
						return 1;
					}
					if(o1.getServiceDate().equals(o2.getServiceDate())){
						if(o1.getConsumAmount() > o2.getConsumAmount()){
							
							return 1;
						}
						if(o1.getConsumAmount() == o2.getConsumAmount()){
							return 0;
						}
						if(o1.getConsumAmount() < o2.getConsumAmount()){
							return -1;
						}
					}
					return 1;
				}
			});
			long realSize = consumHists.size();
			int size = (int) Math.ceil(realSize / (double) number);
			// 遍历list中projectinfo集合,将现价总计 返回
			float amount = 0f;
			for (ConsumHist hist : consumHists) {
				amount = amount + hist.getConsumAmount();
			}
			net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS,
					net.sf.json.JSONArray.fromObject(consumHists, JsonResFactory.dateConfig()));
			obj.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
			obj.put(Constants.RESPONSE_SIZE_KEY, size);
			obj.put(Constants.RESPONSE_AMOUNT_KEY, amount);
			return obj.toString();
		} else {
			int from = (page - 1) * number;
			ConsumHist consumHist = null;
			List<ConsumOrder> list = this.consumOrderDao.queryByClientIdDate(clientId, from, number, startTime,
					endTime);
			List<IncomeOrder> incomeList = this.incomeOrderDao.listByClientIdDate(clientId, from, number, startTime,
					endTime);
			if ((list == null || list.isEmpty()) && (incomeList == null || incomeList.isEmpty())) {
				return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
			}
			List<ConsumHist> consumHists = new ArrayList<>();
			int id = 0;
			if (list != null) {
				for (ConsumOrder consumOrder : list) {
					String project = "";
					if (consumOrder.getPayMethod().contains("$")) {
						payMethod = Integer.parseInt(consumOrder.getPayMethod().substring(0, 1));
					} else {
						payMethod = Integer.parseInt(consumOrder.getPayMethod());
					}
					float consumAmount = 0f;
					for (ProjectInfo projectInfo : consumOrder.getProjects()) {
						project = project + projectInfo.getName() + "、";
						consumAmount = consumAmount + projectInfo.getPresentPrice();
					}
					int length = project.length();
					project = project.substring(0, length - 1);
					consumHist = new ConsumHist();
					consumHist.setId(++id);
					consumHist.setConsumAmount(consumAmount);
					consumHist.setPayMethod(payMethod);
					consumHist.setProject(project);
					consumHist.setServiceDate(consumOrder.getCreateDate());
					consumHists.add(consumHist);
				}
			}
			if (incomeList != null) {
				for (IncomeOrder incomeOrder : incomeList) {
					consumHist = new ConsumHist();
					consumHist.setId(++id);
					consumHist.setProject(incomeOrder.getProgramName());
					consumHist.setConsumAmount(incomeOrder.getAmount());
					consumHist.setPayMethod(incomeOrder.getPayMethod());
					consumHist.setServiceDate(incomeOrder.getPayDate());
					consumHists.add(consumHist);
				}
			}
			long realSize = consumHists.size();
			int size = (int) Math.ceil(realSize / (double) number);
			// 遍历list中projectinfo集合,将现价总计 返回
			float amount = 0f;
			for (ConsumHist hist : consumHists) {
				amount = amount + hist.getConsumAmount();
			}
			net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS,
					net.sf.json.JSONArray.fromObject(consumHists, JsonResFactory.dateConfig()));
			obj.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
			obj.put(Constants.RESPONSE_SIZE_KEY, size);
			obj.put(Constants.RESPONSE_AMOUNT_KEY, amount);
			return obj.toString();
		}
	}

	public String getInsurance(int page, int number) {
		int from = (page - 1) * number;
		List<InsuranceOrder> insuranceOrders = insuranceOrderDao.getInsuranceOrder(from, number);
		if (insuranceOrders == null || insuranceOrders.isEmpty()) {
			return JsonResFactory.buildOrg(RESCODE.NO_RECORD).toString();
		} else {
			long realSize = this.insuranceOrderDao.getCount();
			int size = (int) Math.ceil(realSize / (double) number);
			net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS,
					net.sf.json.JSONArray.fromObject(insuranceOrders, JsonResFactory.dateConfig()));
			obj.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
			obj.put(Constants.RESPONSE_SIZE_KEY, size);
			return obj.toString();
		}
	}
}
