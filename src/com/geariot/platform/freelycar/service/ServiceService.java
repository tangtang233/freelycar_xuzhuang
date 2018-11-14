package com.geariot.platform.freelycar.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.geariot.platform.freelycar.dao.AdminDao;
import com.geariot.platform.freelycar.dao.ClientDao;
import com.geariot.platform.freelycar.dao.InsuranceOrderDao;
import com.geariot.platform.freelycar.dao.ProjectDao;
import com.geariot.platform.freelycar.dao.ServiceDao;
import com.geariot.platform.freelycar.entities.Service;
import com.geariot.platform.freelycar.model.RESCODE;
import com.geariot.platform.freelycar.utils.Constants;
import com.geariot.platform.freelycar.utils.JsonResFactory;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

@org.springframework.stereotype.Service
@Transactional
public class ServiceService {

	private static final Logger log = LogManager.getLogger(ServiceService.class);

	@Autowired
	private ServiceDao serviceDao;

	@Autowired
	private ClientDao clientDao;
	
	@Autowired
	private ProjectDao projectDao;
	
	@Autowired
	private AdminDao adminDao;
	
	@Autowired
	private InsuranceOrderDao insuranceOrderDao;

	public String addService(Service service) {
		service.setCreateDate(new Date());
		serviceDao.save(service);
		JsonConfig config = JsonResFactory.dateConfig();
		return JsonResFactory.buildNetWithData(RESCODE.SUCCESS, net.sf.json.JSONObject.fromObject(service, config))
				.toString();
	}

	public String deleteService(Integer... serviceIds) {
		int count = 0;
		for (int serviceId : serviceIds) {
			com.geariot.platform.freelycar.entities.Service exist = serviceDao.findServiceById(serviceId);
			if (exist == null) {
				count++;
			} else {
				// 删除service只将deleted字段设为true，不在数据库中删除此条字段
				exist.setDeleted(true);
			}
		}
		if (count != 0) {
			String tips = "共" + count + "条未在数据库中存在记录";
			net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.PART_SUCCESS, tips);
			long realSize = serviceDao.getCount();
			obj.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
			return obj.toString();
		} else {
			JSONObject obj = JsonResFactory.buildOrg(RESCODE.SUCCESS);
			long realSize = serviceDao.getCount();
			obj.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
			return obj.toString();
		}
	}

	public String modifyService(Service service) {
		Service exist = serviceDao.findServiceById(service.getId());
		JSONObject obj = null;
		if (exist == null) {
			obj = JsonResFactory.buildOrg(RESCODE.NOT_FOUND);
		} else {
			exist.setComment(service.getComment());
			exist.setName(service.getName());
			exist.setValidTime(service.getValidTime());
			exist.setPrice(service.getPrice());
			obj = JsonResFactory.buildOrg(RESCODE.SUCCESS);
		}
		return obj.toString();
	}

	public Map<String,Object> getSelectService(String name, int page, int number) {
		int from = (page - 1) * number;
		List<Service> list = serviceDao.listServices(name, from, number);
		if (list == null || list.isEmpty()) {
			return RESCODE.NOT_FOUND.getJSONRES();
		}
		long count = serviceDao.getConditionCount(name);
		int size = (int) Math.ceil(count / (double) number);
		return RESCODE.SUCCESS.getJSONRES(list,size,count);
	}

	public String getAllName() {
		List<Object> list = serviceDao.listName();
		JSONArray jsonArray = JSONArray.fromObject(list);
		net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS, jsonArray);
		return obj.toString();
	}

	public Map<String, Object> enableBookState(int serviceId) {
		Service service = serviceDao.findServiceById(serviceId);
		if(service == null){
			return RESCODE.NOT_FOUND.getJSONRES();
		}else{
			service.setBookOnline(true);
			return RESCODE.SUCCESS.getJSONRES();
		}
	}

	public Map<String, Object> disableBookState(int serviceId) {
		Service service = serviceDao.findServiceById(serviceId);
		if(service == null){
			return RESCODE.NOT_FOUND.getJSONRES();
		}else{
			service.setBookOnline(false);
			return RESCODE.SUCCESS.getJSONRES();
		}
	}

	/*public String test() throws FileNotFoundException, IOException, ParseException {
		String filePath = "C:\\Users\\eric\\Desktop\\小易爱车会员表.xls";
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(filePath));
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet = wb.getSheetAt(0);
		for (int i = 1; i < 100; i++) {
			if (sheet.getRow(i) == null) {
				continue;
			} else {
				Client client = new Client();
				Car car = new Car();
				//设置cardprojectremaininginfo表
				CardProjectRemainingInfo remainingInfo = new CardProjectRemainingInfo();
				remainingInfo.setProject(projectDao.findProjectById(5));
				String ok = sheet.getRow(i).getCell(18).toString();
				ok = ok.substring(0, ok.length()-2);
				remainingInfo.setRemaining(Integer.parseInt(ok));
				//设置完成 添加到card中
				Set<CardProjectRemainingInfo> infos = new HashSet<>();
				infos.add(remainingInfo);
				
				//设置card
				Card card = new Card();
				card.setCardNumber(sheet.getRow(i).getCell(0).toString());
				card.setPayDate(sdf1.parse(sheet.getRow(i).getCell(14).toString()));
				card.setExpirationDate(sdf.parse(sheet.getRow(i).getCell(15).toString()));
				card.setPayMethod(0);
				card.setService(serviceDao.findServiceById(5));
				card.setOrderMaker(adminDao.findAdminById(1));
				card.setFailed(false);
				card.setProjectInfos(infos);
				//设置完成 添加到set<card>中
				Set<Card> set = new HashSet<>();
				set.add(card);
				//设置Car
				car.setLicensePlate(sheet.getRow(i).getCell(11).toString());
				car.setCarMark(sheet.getRow(i).getCell(12).toString());
				if(sheet.getRow(i).getCell(12).toString().equals("未知")){
					car.setCarbrand("未知");
				}else{
					car.setCarbrand(sheet.getRow(i).getCell(12).toString()+sheet.getRow(i).getCell(13).toString());
				}
				car.setCreateDate(new Date());
				car.setDefaultCar(true);
				Set<Car> cars = new HashSet<>();
				cars.add(car);
				
				//设置client
				client.setName(sheet.getRow(i).getCell(1).toString());
				client.setConsumAmout(500);
				client.setIsMember(true);
				client.setConsumTimes(1);
				client.setPhone(sheet.getRow(i).getCell(2).toString());
				client.setCreateDate(new Date());
				client.setLastVisit(new Date());
				client.setCards(set);
				client.setCars(cars);
				clientDao.save(client);
			}
		}
		return "success";
	}*/
	
	
	/*public String insurance() throws FileNotFoundException, IOException, ParseException{
		String filePath = "C:\\Users\\eric\\Desktop\\车险客户.xls";
		Date now = new Date();
		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
		POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(filePath));
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet = wb.getSheetAt(0);
		for (int i = 1; i <160; i++) {
			if (sheet.getRow(i) == null) {
				continue;
			} else {
				InsuranceOrder insuranceOrder = new InsuranceOrder();
				insuranceOrder.setName(sheet.getRow(i).getCell(0).toString());
				insuranceOrder.setLicensePlate(sheet.getRow(i).getCell(1).toString());
				insuranceOrder.setPhone(sheet.getRow(i).getCell(2).toString());
				//System.out.println(sheet.getRow(i).getCell(2).toString());
				insuranceOrder.setInsuranceCompany(sheet.getRow(i).getCell(3).toString());
				insuranceOrder.setCreateDate(sdf1.parse(sheet.getRow(i).getCell(4).toString()));
				insuranceOrderDao.save(insuranceOrder);
			}
	}
		return "success";
	}*/
}
