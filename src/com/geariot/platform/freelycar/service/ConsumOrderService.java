package com.geariot.platform.freelycar.service;

import com.geariot.platform.freelycar.dao.*;
import com.geariot.platform.freelycar.entities.*;
import com.geariot.platform.freelycar.exception.ForRollbackException;
import com.geariot.platform.freelycar.model.RESCODE;
import com.geariot.platform.freelycar.utils.*;
import com.geariot.platform.freelycar.utils.query.ConsumOrderQueryCondition;
import com.geariot.platform.freelycar.wxutils.WechatTemplateMessage;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class ConsumOrderService {

	private static final Logger log = LogManager.getLogger(ConsumOrderService.class);

	@Autowired
	private ConsumOrderDao orderDao;

	@Autowired
	private InventoryDao inventoryDao;

	@Autowired
	private ClientDao clientDao;

	@Autowired
	private WXUserDao wxUserDao;
	
	@Autowired
	private CardDao cardDao;
	
	@Autowired
	private SocketHelper socketHelper;
	
	public Map<String,Object> book(ConsumOrder consumOrder) {
		log.debug("客户id：" + consumOrder.getClientId() + ", 客户姓名:" + consumOrder.getClientName() + ", 尝试创建消费订单");
		log.debug("开单详情:"+consumOrder);
		String consumOrderId = IDGenerator.generate(IDGenerator.MAINTAIN_CONSUM);
		consumOrder.setId(consumOrderId);
		Date tempDate = new Date();
		if (consumOrder.getPickTime() == null) {
			consumOrder.setPickTime(tempDate);
		}
		consumOrder.setCreateDate(tempDate);
		Set<ConsumExtraInventoriesInfo> infos = consumOrder.getInventoryInfos();
		// 比较消耗数量与库存实际数量
		for (ConsumExtraInventoriesInfo info : infos) {
			Inventory inventory = inventoryDao.findById(info.getInventory().getId());
			log.debug("订单需要消耗库存(id:" + inventory.getId() + ", 名称：" + inventory.getName() + ")总计" + info.getNumber()
					+ inventory.getStandard());
			log.debug("实际库存剩余：" + inventory.getAmount() + inventory.getStandard());
			// 如果库存不足，抛出异常，操作回滚
			if (inventory.getAmount() < info.getNumber()) {
				log.debug("实际库存不足，当前操作回滚，订单添加失败");
				throw new ForRollbackException(RESCODE.INVENTORY_NOT_ENOUGH.getMsg(),
						RESCODE.INVENTORY_NOT_ENOUGH.getValue());
			}
		}
		//项目预处理
		Set<ProjectInfo> preHandle = consumOrder.getProjects();
		for(ProjectInfo projectInfo : preHandle){
			projectInfo.setPresentPrice(projectInfo.getPrice());
			projectInfo.setClientId(consumOrder.getClientId());
		}
		this.orderDao.save(consumOrder);
		log.debug("消费订单(id:" + consumOrder.getId() + ")保存成功，准备创建出库订单并保存");
		Map<String,Object> map = RESCODE.SUCCESS.getJSONRES();
		map.put("id", consumOrderId);
		return map;
	}
	
	public String list(int page, int number) {
		int from = (page - 1) * number;
		List<ConsumOrder> list = this.orderDao.list(from, number);
		if (list == null || list.isEmpty()) {
			return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
		}
		long realSize = this.orderDao.getCount();
		int size = (int) Math.ceil(realSize / (double) number);
		JsonConfig config = JsonResFactory.dateConfig();
		JsonPropertyFilter filter = new JsonPropertyFilter();
		filter.setColletionProperties(Provider.class);
		config.setJsonPropertyFilter(filter);
		config.registerPropertyExclusions(Admin.class,
				new String[] { "password", "role", "current", "createDate", "comment" });
		JSONArray array = JSONArray.fromObject(list, config);
		net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS, array);
		obj.put(Constants.RESPONSE_SIZE_KEY, size);
		obj.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
		return obj.toString();
	}

	public String finish(String consumOrderId, Date date, String comment, String parkingLocation) {
		ConsumOrder order = this.orderDao.findById(consumOrderId);
		if (order == null) {
			return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
		}
		log.debug("消费订单(id:" + order.getId() + ")施工完成, 完成时间:" + date + ", 备注:" + comment);
		JsonConfig config = JsonResFactory.dateConfig();
		order.setFinishTime(date);
		order.setComment(comment);
		order.setParkingLocation(parkingLocation);
		order.setState(2);
		WXUser wxUser = wxUserDao.findUserByPhone(order.getPhone());
		if (wxUser != null && wxUser.getOpenId() != null) {
			WechatTemplateMessage.consumOrderChange(order, wxUser.getOpenId(),parkingLocation);
		}
		
		socketHelper.onMessage("{\"message\":{\"type\":\"finishWork\"}}", null);
		return JsonResFactory.buildNetWithData(RESCODE.SUCCESS, JSONObject.fromObject(order, config)).toString();
	}

	public String deliverCar(String consumOrderId, Date date, String comment) {
		ConsumOrder order = this.orderDao.findById(consumOrderId);
		if (order == null) {
			return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
		}
		if (order.getState() < 2) {
			log.debug("消费订单(id:" + order.getId() + ")施工未完成，无法交车");
			return JsonResFactory.buildOrg(RESCODE.WORK_NOT_FINISH).toString();
		}
		log.debug("消费订单(id:" + order.getId() + ")交车, 时间:" + date + ", 备注:" + comment);
		JsonConfig config = JsonResFactory.dateConfig();
		order.setState(3);
		order.setDeliverTime(date);
		order.setComment(comment);
		WXUser wxUser = wxUserDao.findUserByPhone(order.getPhone());
		if (wxUser != null && wxUser.getOpenId() != null) {
			WechatTemplateMessage.consumOrderChange(order, wxUser.getOpenId(), order.getParkingLocation());
		}
		return JsonResFactory.buildNetWithData(RESCODE.SUCCESS, JSONObject.fromObject(order, config)).toString();
	}

	// ***************************
	public Map<String,Object> modify(ConsumOrder consumOrder) { 
		ConsumOrder updateOrderConsutom = orderDao.updateOrderConsutom(consumOrder);
		Map<String, Object> jsonres = RESCODE.SUCCESS.getJSONRES();
		jsonres.put("id", updateOrderConsutom.getId());
		return jsonres;
	}

	public Map<String,Object> query(ConsumOrderQueryCondition condition) {
		int from = (condition.getPage() - 1) * condition.getNumber();
		int number = condition.getNumber();
		
		List<ConsumOrder> list = this.orderDao.query(condition, from, number);
		if (list == null || list.isEmpty()) {
			return RESCODE.NOT_FOUND.getJSONRES();
		}
		long realSize = this.orderDao.getQueryCount(condition);
		int size = (int) Math.ceil(realSize / (double) number);
		return RESCODE.SUCCESS.getJSONRES(list, size, realSize);
	}

	public String getOrderById(String consumOrderId) {
		ConsumOrder order = this.orderDao.findById(consumOrderId);
		if (order == null) {
			return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
		}
		Set<Card> cards = clientDao.findById(order.getClientId()).getCards();
		List<Ticket> tickets = clientDao.findById(order.getClientId()).getTickets();
		JsonConfig config = JsonResFactory.dateConfig();
		config.registerPropertyExclusion(Project.class, "inventoryInfos");
		config.registerPropertyExclusion(Admin.class, "password");
		JSONObject jsonObject = JsonResFactory.buildNetWithData(RESCODE.SUCCESS,
				net.sf.json.JSONObject.fromObject(order, config));
		jsonObject.put("card", JSONArray.fromObject(cards, config));
		jsonObject.put("ticket", JSONArray.fromObject(tickets, config));
		return jsonObject.toString();
	}
	
	public void checkBalance(String payMethod, String consumOrderId, float actualPrice){
		//判断储值卡余额是否能完成支付
		String tempPayMethod = payMethod;
		if(tempPayMethod == null || tempPayMethod.isEmpty() || tempPayMethod.trim().isEmpty()){
			log.debug("订单id:" + consumOrderId + "付款方式为空");
			throw new ForRollbackException(RESCODE.NOT_SET_PAYMETHOD.getMsg(),RESCODE.NOT_SET_PAYMETHOD.getValue());
		}else if(tempPayMethod.matches("^5\\$\\w+\\-?\\w+\\$$")){
			Pattern pattern = Pattern.compile("\\w+\\-?\\w+");
			Matcher matcher = pattern.matcher(tempPayMethod);
			while(matcher.find()){
			log.debug("订单id:" + consumOrderId + "付款方式为储值卡支付");
			String balanceCardNumber = matcher.group(0);
			log.debug("储值卡卡号为:"+balanceCardNumber);
			//判断储值卡 扣金额是否足够
			Card card = cardDao.findByCardNumber(balanceCardNumber);
				if(card == null){
					throw new ForRollbackException(RESCODE.NO_CARD.getMsg(),RESCODE.NO_CARD.getValue());
				}else if(card.getBalance() < actualPrice){
					throw new ForRollbackException(RESCODE.CARD_BALANCE_NOT_ENOUGH.getMsg(),RESCODE.CARD_BALANCE_NOT_ENOUGH.getValue());
				}
			}
		} 
	}
	
	public Map<String, Object> bigScreenData(int page, int number){
		int from = (page - 1) * number;
		int finish = 0;
		int pick = 0;
		List<ConsumOrder> bigScreenOrder = orderDao.bigScreenOrder(from, number);
		List<ConsumOrder> totalScreenOrder = orderDao.bigScreenOrder();
		for(ConsumOrder order :totalScreenOrder){
			if(order.getState() == 1){
				pick++;
			}else if(order.getState() == 2){
				finish++;
			}
		}
		long realSize = orderDao.getBigScreenOrderCount();
		int size = (int) Math.ceil(realSize / (double) number);
		Map<String, Object> map = RESCODE.SUCCESS.getJSONRES(bigScreenOrder);
		map.put("realSize", realSize);
		map.put("pick", pick);
		map.put("finish", finish);
		map.put("size", size);
		return map;
	}
}

