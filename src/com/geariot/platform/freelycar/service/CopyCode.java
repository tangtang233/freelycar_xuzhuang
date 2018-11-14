package com.geariot.platform.freelycar.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geariot.platform.freelycar.dao.CardDao;
import com.geariot.platform.freelycar.dao.ClientDao;
import com.geariot.platform.freelycar.dao.ConsumOrderDao;
import com.geariot.platform.freelycar.dao.FavourDao;
import com.geariot.platform.freelycar.dao.InventoryDao;
import com.geariot.platform.freelycar.dao.InventoryOrderDao;
import com.geariot.platform.freelycar.dao.ProjectDao;
import com.geariot.platform.freelycar.dao.WXUserDao;
import com.geariot.platform.freelycar.entities.Admin;
import com.geariot.platform.freelycar.entities.Card;
import com.geariot.platform.freelycar.entities.CardProjectRemainingInfo;
import com.geariot.platform.freelycar.entities.ConsumExtraInventoriesInfo;
import com.geariot.platform.freelycar.entities.ConsumOrder;
import com.geariot.platform.freelycar.entities.Favour;
import com.geariot.platform.freelycar.entities.FavourProjectInfos;
import com.geariot.platform.freelycar.entities.FavourProjectRemainingInfo;
import com.geariot.platform.freelycar.entities.Inventory;
import com.geariot.platform.freelycar.entities.InventoryOrder;
import com.geariot.platform.freelycar.entities.InventoryOrderInfo;
import com.geariot.platform.freelycar.entities.Project;
import com.geariot.platform.freelycar.entities.ProjectInfo;
import com.geariot.platform.freelycar.entities.Provider;
import com.geariot.platform.freelycar.entities.Staff;
import com.geariot.platform.freelycar.entities.Ticket;
import com.geariot.platform.freelycar.entities.WXUser;
import com.geariot.platform.freelycar.exception.ForRollbackException;
import com.geariot.platform.freelycar.model.RESCODE;
import com.geariot.platform.freelycar.utils.Constants;
import com.geariot.platform.freelycar.utils.IDGenerator;
import com.geariot.platform.freelycar.utils.JsonPropertyFilter;
import com.geariot.platform.freelycar.utils.JsonResFactory;
import com.geariot.platform.freelycar.wxutils.WechatTemplateMessage;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

@Service
@Transactional
public class CopyCode {

	private static final Logger log = LogManager.getLogger(ConsumOrderService.class);

	@Autowired
	private ConsumOrderDao orderDao;

	@Autowired
	private InventoryDao inventoryDao;

	@Autowired
	private InventoryOrderDao inventoryOrderDao;

	@Autowired
	private CardDao cardDao;

	@Autowired
	private ProjectDao projectDao;

	@Autowired
	private FavourDao favourDao;

	@Autowired
	private ClientDao clientDao;

	@Autowired
	private WXUserDao wxUserDao;

	public String book(ConsumOrder consumOrder) {
		log.debug("修改之前order----------" + consumOrder);
		if (consumOrder.getPickTime() == null) {
			consumOrder.setPickTime(new Date());
		}
		String consumOrderId = IDGenerator.generate(IDGenerator.MAINTAIN_CONSUM);
		consumOrder.setId(consumOrderId);
		log.debug("客户id：" + consumOrder.getClientId() + ", 客户姓名:" + consumOrder.getClientName() + ", 尝试创建消费订单");
		consumOrder.setCreateDate(new Date());
		Set<ConsumExtraInventoriesInfo> infos = consumOrder.getInventoryInfos();
		List<InventoryOrderInfo> list = new ArrayList<>();
		float totalAmount = 0.0f;
		float totalPrice = 0.0f;
		// 项目预处理
		Set<ProjectInfo> preHandle = consumOrder.getProjects();
		for (ProjectInfo prepro : preHandle) {
			prepro.setClientId(consumOrder.getClientId());
			if (prepro.getCardId() == null) {
				prepro.setCardId("0");
				prepro.setTicketId("0");
				prepro.setPresentPrice(prepro.getPrice());
			} else {
				if (prepro.getCardId().contains("$")) {
					String str = prepro.getCardId();
					String ticketId = prepro.getCardId().substring(0, str.length() - 1);
					prepro.setTicketId(ticketId);
					prepro.setPayMethod(2);
					Favour favour = favourDao.findById(Integer.parseInt(ticketId)).getFavour();
					prepro.setFavourName(favour.getName());
					switch (favour.getType()) {
					case 1:
						prepro.setPresentPrice(0);
						break;
					case 2:
						int a = favourDao.findByProjectIdAndFavourId(prepro.getProjectId(), favour.getId());
						FavourProjectInfos favourProjectInfos = favourDao.findByFavourProjectInfosId(a);
						prepro.setPresentPrice(favourProjectInfos.getPresentPrice());
						break;
					default:
						break;
					}
					prepro.setCardId("0");
				} else {
					prepro.setPresentPrice(0);
					String cardNumber = cardDao.getCardById(Integer.parseInt(prepro.getCardId())).getCardNumber();
					log.error(cardNumber);
					prepro.setCardNumber(cardNumber);
				}
			}
		}
		// 判断项目付款方式
		for (ProjectInfo proinfo : preHandle) {
			log.debug(("项目(id:" + proinfo.getProjectId() + ", 名称:" + proinfo.getName() + ")" + "付款方式:"
					+ proinfo.getPayMethod()));
			// 如果用卡付款，根据设置的卡id与项目id查找剩余次数信息
			if (!proinfo.getCardId().equals("0")) {
				CardProjectRemainingInfo remain = this.cardDao
						.getProjectRemainingInfo(Integer.parseInt(proinfo.getCardId()), proinfo.getProjectId());
				// 没有找到，卡未设置或卡中没有对应的项目信息，操作回滚
				if (remain == null) {
					log.debug("该项目尝试用卡次支付但没有对应卡信息。本次订单操作回滚");
					throw new ForRollbackException(RESCODE.NOT_SET_PAY_CARD.getMsg(),
							RESCODE.NOT_SET_PAY_CARD.getValue());
				} else {
					// 找到剩余次数信息，但剩余次数不够支付卡次的，返回次数不足,，操作回滚
					if (remain.getRemaining() < proinfo.getPayCardTimes()) {
						log.debug("该项目尝试用卡(id:" + proinfo.getCardId() + " ,对应剩余次数:" + remain.getRemaining() + ")"
								+ "不足支付次数:" + proinfo.getPayCardTimes() + "。本次订单操作回滚");
						throw new ForRollbackException(RESCODE.CARD_REMAINING_NOT_ENOUGH.getMsg(),
								RESCODE.CARD_REMAINING_NOT_ENOUGH.getValue());
					}
				}
			}
			// 如果用券付款,根据设置的
			else if (!proinfo.getTicketId().equals("0")) {
				FavourProjectRemainingInfo infoRemain = this.favourDao
						.getProjectRemainingInfo(Integer.parseInt(proinfo.getTicketId()), proinfo.getProjectId());
				// 没有找到，券未设置或券中没有对应的项目信息，操作回滚
				if (infoRemain == null) {
					log.debug("该项目尝试用卡次支付但没有对应卡信息。本次订单操作回滚");
					throw new ForRollbackException(RESCODE.NOT_SET_PAY_TICKET.getMsg(),
							RESCODE.NOT_SET_PAY_TICKET.getValue());
				} else {
					// 找到剩余次数信息，但剩余次数不够支付券次的，返回次数不足,，操作回滚
					if (infoRemain.getRemaining() < proinfo.getPayCardTimes()) {
						log.debug("该项目尝试用券(id:" + proinfo.getTicketId() + " ,对应剩余次数:" + infoRemain.getRemaining() + ")"
								+ "不足支付次数:" + proinfo.getPayCardTimes() + "。本次订单操作回滚");
						throw new ForRollbackException(RESCODE.TICKET_REMAINING_NOT_ENOUGH.getMsg(),
								RESCODE.TICKET_REMAINING_NOT_ENOUGH.getValue());
					}
				}
			}
		}

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
			inventory.setAmount(inventory.getAmount() - info.getNumber());
			InventoryOrderInfo temp = new InventoryOrderInfo();
			temp.setAmount(info.getNumber());
			Inventory inv = info.getInventory();
			temp.setBrandName(inv.getBrandName());
			temp.setInventoryId(inv.getId());
			temp.setName(inv.getName());
			temp.setProperty(inv.getProperty());
			temp.setStandard(inv.getStandard());
			temp.setTypeName(inv.getTypeName());
			temp.setPrice(info.getInventory().getPrice() * temp.getAmount());
			list.add(temp);
			totalAmount += temp.getAmount();
			totalPrice += temp.getPrice();
		}
		// 获取project信息保存到该订单
		for (ProjectInfo pInfo : preHandle) {
			Set<Staff> staffs = pInfo.getStaffs();
			pInfo.setStaffs(staffs);
			Project project = this.projectDao.findProjectById(pInfo.getProjectId());
			pInfo.setName(project.getName());
			pInfo.setPrice(project.getPrice());
			pInfo.setClientName(consumOrder.getClientName());
			pInfo.setLicensePlate(consumOrder.getLicensePlate());
			pInfo.setBrandName(consumOrder.getCarBrand());
			pInfo.setCreateDate(new Date());
			if (!pInfo.getCardId().equals("0")) {
				Card payCard = this.cardDao.getCardById(Integer.parseInt(pInfo.getCardId()));
				pInfo.setCardName(payCard.getService().getName());
				pInfo.setPayMethod(0);
			} else {
				pInfo.setPayMethod(1);
			}

		}
		/*
		 * //对Car添加LastMile Car
		 * car=carDao.findByLicense(consumOrder.getLicensePlate());
		 * car.setLastMiles(consumOrder.getMiles()); carDao.save(car);
		 */
		this.orderDao.save(consumOrder);
		log.debug("保存之前order----------" + consumOrder);
		log.debug("消费订单(id:" + consumOrder.getId() + ")保存成功，准备创建出库订单并保存");
		// 创建出库订单并保存
		if (list != null && !list.isEmpty()) {
			InventoryOrder order = new InventoryOrder();
			order.setId(IDGenerator.generate(IDGenerator.OUT_STOCK));
			order.setCreateDate(new Date());
			order.setInventoryInfos(list);
			order.setState(0);
			order.setTotalPrice(totalPrice);
			order.setType(consumOrder.getProgramId());
			inventoryOrderDao.save(order);
			log.debug("出库订单(id:" + order.getId() + ")保存成功");
		}
		return JsonResFactory.buildOrg(RESCODE.SUCCESS, "id", consumOrderId).toString();
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
		orderDao.updateOrderConsutom(consumOrder); 
		return RESCODE.SUCCESS.getJSONRES();
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

}
