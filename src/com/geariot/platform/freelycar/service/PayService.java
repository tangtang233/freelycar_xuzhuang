package com.geariot.platform.freelycar.service;

import com.geariot.platform.freelycar.dao.*;
import com.geariot.platform.freelycar.entities.*;
import com.geariot.platform.freelycar.exception.ForRollbackException;
import com.geariot.platform.freelycar.model.RESCODE;
import com.geariot.platform.freelycar.utils.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class PayService {

	private static final Logger log = LogManager.getLogger(PayService.class);

	@Autowired
	private IncomeOrderDao incomeOrderDao;

	@Autowired
	private ClientDao clientDao;

	@Autowired
	private ServiceDao serviceDao;

	@Autowired
	private CardDao cardDao;

	@Autowired
	private CarDao carDao;

	@Autowired
	private ConsumOrderDao consumOrderDao;

	@Autowired
	private FavourDao favourDao;

	@Autowired
	private ProjectDao projectDao;

	@Autowired
	private InventoryDao inventoryDao;

	@Autowired
	private InventoryOrderDao inventoryOrderDao;

	public String buyCard(int clientId, Card card) {
		if (card.getCardNumber() == null || card.getCardNumber().isEmpty() || card.getCardNumber().trim().isEmpty()) {
			String cardNumber = String.valueOf(CommonUtils.RANDOM.nextInt(900000) + 100000);
			// String cardNumber = String.valueOf((int) ((Math.random() * 9 + 1)
			// * 100000));
			while (cardDao.findByCardNumber(cardNumber) != null) {
				cardNumber = String.valueOf(CommonUtils.RANDOM.nextInt(900000) + 100000);
			}
			card.setCardNumber(cardNumber);
		} else if (cardDao.findByCardNumber(card.getCardNumber()) != null) {
			return JsonResFactory.buildOrg(RESCODE.CARDNUMBER_EXIST).toString();
		}
		Client client = clientDao.findById(clientId);
		com.geariot.platform.freelycar.entities.Service service = this.serviceDao
				.findServiceById(card.getService().getId());
		if (client == null || service == null) {
			return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
		}
		// 若service type 为储值卡 要将储值卡信息记录
		if (service.getType() == 2) {
			card.setBalance(service.getActualPrice());
		}
		// 将优惠券信息添加到客户卡列表中
		List<Ticket> tickets = new ArrayList<>();
		for (FavourInfos favourInfos : service.getFavourInfos()) {
			for (int i = 0; i < favourInfos.getCount(); i++) {
				Set<FavourProjectRemainingInfo> remainingInfos = new HashSet<>();
				Ticket ticket = new Ticket();
				ticket.setFavour(favourInfos.getFavour());
				ticket.setExpirationDate(DateHandler
						.addValidMonth(DateHandler.toCalendar(new Date()), favourInfos.getFavour().getValidTime())
						.getTime());
				FavourProjectRemainingInfo projectRemainingInfo = new FavourProjectRemainingInfo();
				for (FavourProjectInfos projectInfos : favourInfos.getFavour().getSet()) {
					projectRemainingInfo.setProject(projectInfos.getProject());
					projectRemainingInfo.setRemaining(projectInfos.getTimes());
					remainingInfos.add(projectRemainingInfo);
				}
				ticket.setRemainingInfos(remainingInfos);
				tickets.add(ticket);
			}
		}
		List<Ticket> list = client.getTickets();
		if (list == null) {
			list = new ArrayList<>();
			client.setTickets(tickets);
		}
		for (Ticket add : tickets) {
			list.add(add);
		}
		// 将服务信息次数复制到卡中
		Set<CardProjectRemainingInfo> cardInfos = new HashSet<>();
		CardProjectRemainingInfo cardInfo = null;
		for (ServiceProjectInfo info : service.getProjectInfos()) {
			cardInfo = new CardProjectRemainingInfo();
			cardInfo.setProject(info.getProject());
			cardInfo.setRemaining(info.getTimes());
			cardInfos.add(cardInfo);
		}
		card.setProjectInfos(cardInfos);
		// 将新增卡增加到客户卡列表中
		Set<Card> cards = client.getCards();
		if (cards == null) {
			cards = new HashSet<>();
			client.setCards(cards);
		}
		card.setPayDate(new Date());
		Calendar exp = Calendar.getInstance();
		exp.setTime(new Date());
		exp.add(Calendar.YEAR, service.getValidTime());
		card.setExpirationDate(exp.getTime());
		cards.add(card);
		// 创建新的收入订单并保存
		IncomeOrder order = new IncomeOrder();
		order.setAmount(service.getPrice());
		order.setClientId(clientId);
		order.setLicensePlate(null);
		order.setMember(true);
		order.setPayDate(new Date());
		order.setProgramName(Constants.PROGRAM_DOCARD);
		order.setPayMethod(card.getPayMethod());
		/*
		 * Admin admin =
		 * this.adminDao.findAdminById(card.getOrderMaker().getId());
		 * order.setStaffNames(admin.getStaff().getName());
		 */
		this.incomeOrderDao.save(order);
		// 更新客户的消费次数与消费情况信息。
		client.setConsumTimes(client.getConsumTimes() + 1);
		client.setConsumAmout(client.getConsumAmout() + order.getAmount());
		client.setLastVisit(new Date());
		client.setIsMember(true);
		return JsonResFactory.buildOrg(RESCODE.SUCCESS).toString();
	}

	public Map<String, Object> templeOrder(String consumOrderId, String payMethod, String payMethod1, float actualPrice,
			float actualPrice1, Set<ProjectInfo> projectInfos, boolean pay) {
		ConsumOrder order = consumOrderDao.findById(consumOrderId);
		if (order == null) {
			return RESCODE.NO_RECORD.getJSONRES();
		}
		Set<ProjectInfo> infos = order.getProjects();
		log.info("消费订单(id:" + order.getId() + ")进行项目内容更改");
		for (ProjectInfo projectInfo : infos) {
			for (ProjectInfo changeInfo : projectInfos) {
				if (projectInfo.getId() == changeInfo.getId()) {
					projectDao.updateProjectInfo(changeInfo);
				}
			}
		}
		//
		Set<ProjectInfo> preHandle = order.getProjects();
		for (ProjectInfo prepro : preHandle) {
			prepro.setClientId(order.getClientId());
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
					String cardId = prepro.getCardId();
					log.info("projectInfo 卡号:(" + cardId + ")");

					String cardNumber = cardDao.getCardById(Integer.parseInt(cardId)).getCardNumber();
					prepro.setCardNumber(cardNumber);
					prepro.setCardName(cardDao.getCardById(Integer.parseInt(cardId)).getService().getName());
				}
			}
		}
		log.info("消费订单(id:" + order.getId() + ")项目内容更改完成");
		checkBalance(payMethod, order, actualPrice);
		order.setPayMethod(payMethod);
		order.setActualPrice(actualPrice);
		checkBalance(payMethod1, order, actualPrice1);
		order.setPayMethod1(payMethod1);
		order.setActualPrice1(actualPrice1);
		if (pay) {
			return consumPay(consumOrderId);
		} else {
			return RESCODE.TEMPLE_ORDER_SUCCESS.getJSONRES();
		}
	}

	public Map<String, Object> consumPay(String consumOrderId) {
		InventoryOrderInfo temp = null;
		IncomeOrder recorder = null;
		List<InventoryOrderInfo> list = new ArrayList<>();
		float inventoryTotalPrice = 0.0f;
		ConsumOrder order = this.consumOrderDao.findById(consumOrderId);
		if (order == null) {
			return RESCODE.NO_RECORD.getJSONRES();
		}
		log.debug("消费订单(id:" + order.getId() + ")进行结算，订单包含项目:");
		// 判断项目付款方式
		Set<ProjectInfo> projects = order.getProjects();
		for (ProjectInfo info : projects) {
			log.debug(
					("项目(id:" + info.getProjectId() + ", 名称:" + info.getName() + ")" + "付款方式:" + info.getPayMethod()));
			// 如果用卡付款，根据设置的卡id与项目id查找剩余次数信息
			if (info.getPayMethod() == Constants.PROJECT_WITH_CARD) {
				info.setCardNumber(cardDao.getCardById(Integer.parseInt(info.getCardId())).getCardNumber());
				CardProjectRemainingInfo remain = this.cardDao
						.getProjectRemainingInfo(Integer.parseInt(info.getCardId()), info.getProjectId());
				// 没有找到，卡未设置或卡中没有对应的项目信息，操作回滚
				if (remain == null) {
					log.debug("该项目尝试用卡次支付但没有对应卡信息。本次订单操作回滚");
					throw new ForRollbackException(RESCODE.NOT_SET_PAY_CARD.getMsg(),
							RESCODE.NOT_SET_PAY_CARD.getValue());
				} else {
					// 找到剩余次数信息，但剩余次数不够支付卡次的，返回次数不足,，操作回滚
					if (remain.getRemaining() < info.getPayCardTimes()) {
						log.debug("该项目尝试用卡(id:" + info.getCardId() + " ,对应剩余次数:" + remain.getRemaining() + ")"
								+ "不足支付次数:" + info.getPayCardTimes() + "。本次订单操作回滚");
						throw new ForRollbackException(RESCODE.CARD_REMAINING_NOT_ENOUGH.getMsg(),
								RESCODE.CARD_REMAINING_NOT_ENOUGH.getValue());
					}
					// 剩余次数足够，扣除次数
					else {
						log.debug("用卡(id:" + info.getCardId() + " ,对应项目:" + info.getName() + " ,剩余次数:"
								+ remain.getRemaining() + ")扣除次数:" + info.getPayCardTimes());
						remain.setRemaining(remain.getRemaining() - info.getPayCardTimes());
						// 检查卡是否失效
						Card card = cardDao.getCardById(Integer.parseInt(info.getCardId()));
						int count = 0;
						for (CardProjectRemainingInfo projectRemainingInfo : card.getProjectInfos()) {
							count = count + projectRemainingInfo.getRemaining();
						}
						card.setFailed(count == 0);
					}
				}
			} else if (info.getPayMethod() == Constants.PROJECT_WITH_FAVOUR) {
				FavourProjectRemainingInfo remain = this.favourDao
						.getProjectRemainingInfo(Integer.parseInt(info.getTicketId()), info.getProjectId());
				if (remain == null) {
					log.debug("该项目尝试用券类支付但没有对应券类信息。本次订单操作回滚");
					throw new ForRollbackException(RESCODE.NOT_SET_PAY_TICKET.getMsg(),
							RESCODE.NOT_SET_PAY_TICKET.getValue());
				} else {
					// 找到剩余次数信息，但剩余次数不够支付券类次数的，返回次数不足,，操作回滚
					if (remain.getRemaining() < info.getPayCardTimes()) {
						log.debug("该项目尝试用券(id:" + info.getTicketId() + " ,对应剩余次数:" + remain.getRemaining() + ")"
								+ "不足支付次数:" + info.getPayCardTimes() + "。本次订单操作回滚");
						throw new ForRollbackException(RESCODE.TICKET_REMAINING_NOT_ENOUGH.getMsg(),
								RESCODE.TICKET_REMAINING_NOT_ENOUGH.getValue());
					}
					// 剩余次数足够，扣除次数
					else {
						log.debug("用券(id:" + info.getTicketId() + " ,对应项目:" + info.getName() + " ,剩余次数:"
								+ remain.getRemaining() + ")扣除次数:" + info.getPayCardTimes());
						remain.setRemaining(remain.getRemaining() - info.getPayCardTimes());
						// 检查 券是否失效
						Ticket ticket = favourDao.findById(Integer.parseInt(info.getTicketId()));
						int count = 0;
						for (FavourProjectRemainingInfo favourProjectRemainingInfo : ticket.getRemainingInfos()) {
							count = count + favourProjectRemainingInfo.getRemaining();
						}
						ticket.setFailed(count == 0);
					}
				}
			}
		}
		// 使用的配件进行删除
		// 比较消耗数量与库存实际数量
		for (ConsumExtraInventoriesInfo info : order.getInventoryInfos()) {
			Inventory inventory = inventoryDao.findById(info.getInventory().getId());
			log.debug("订单需要消耗库存(id:" + inventory.getId() + ", 名称：" + inventory.getName() + ")总计" + info.getNumber()
					+ inventory.getStandard());
			log.debug("实际库存剩余：" + inventory.getAmount() + inventory.getStandard());
			// 如果库存不足，抛出异常，操作回滚
			if (inventory.getAmount() < info.getNumber()) {
				log.debug("实际库存不足，当前操作回滚，订单结算失败");
				throw new ForRollbackException(RESCODE.INVENTORY_NOT_ENOUGH.getMsg(),
						RESCODE.INVENTORY_NOT_ENOUGH.getValue());
			}
			inventory.setAmount(inventory.getAmount() - info.getNumber());
			temp = new InventoryOrderInfo();
			temp.setAmount(info.getNumber());
			Inventory inv = info.getInventory();
			temp.setBrandName(inv.getBrandName());
			temp.setInventoryId(inv.getId());
			temp.setName(inv.getName());
			temp.setProperty(inv.getProperty());
			temp.setStandard(inv.getStandard());
			temp.setTypeName(inv.getTypeName());
			temp.setPrice(info.getInventory().getPrice());
			list.add(temp);
			inventoryTotalPrice += info.getInventory().getPrice() * temp.getAmount();
		}

		// 将projectInfo信息补充完整
		// 获取project信息保存到该订单
		for (ProjectInfo pInfo : order.getProjects()) {
			Set<Staff> staffs = pInfo.getStaffs();
			pInfo.setStaffs(staffs);
			Project project = this.projectDao.findProjectById(pInfo.getProjectId());
			pInfo.setName(project.getName());
			pInfo.setPrice(project.getPrice());
			pInfo.setClientName(order.getClientName());
			pInfo.setLicensePlate(order.getLicensePlate());
			pInfo.setBrandName(order.getCarBrand());
			pInfo.setCreateDate(new Date());
		}

		// 对Car添加LastMile
		Car car = carDao.findByLicense(order.getLicensePlate());
		car.setLastMiles(order.getMiles());
		carDao.save(car);
		// 现金支付的情况，直接进行结算
		order.setPayState(1);
		float tempTotalPrice = order.getActualPrice() + order.getActualPrice1();
//		order.setTotalPrice(tempTotalPrice);
		log.debug("消费订单结算完成，生成收入订单");
		// 结算完成后，记录到IncomeOrder。
		recorder = new IncomeOrder();
		IncomeOrder recorder1 = null;
		if (order.getPayMethod().matches("^5\\$\\w+\\-?\\w+\\$$")) {
			balancePay(order.getPayMethod(), order.getActualPrice());
			recorder.setPayMethod(Integer.parseInt("5"));
			recorder.setAmount(order.getActualPrice());
			recorder.setClientId(order.getClientId());
			recorder.setLicensePlate(order.getLicensePlate());
			recorder.setPayDate(new Date());
			recorder.setProgramName(order.getProgramName());
		} else {
			recorder.setPayMethod(Integer.parseInt(order.getPayMethod()));
			recorder.setAmount(order.getActualPrice());
			recorder.setClientId(order.getClientId());
			recorder.setLicensePlate(order.getLicensePlate());
			recorder.setPayDate(new Date());
			recorder.setProgramName(order.getProgramName());
		}
		if (order.getPayMethod1() != null) {
			if (order.getPayMethod1().matches("^5\\$\\w+\\-?\\w+\\$$")) {
				balancePay(order.getPayMethod1(), order.getActualPrice1());
				recorder1 = new IncomeOrder();
				recorder1.setPayMethod(Integer.parseInt("5"));
				recorder1.setAmount(order.getActualPrice1());
				recorder1.setClientId(order.getClientId());
				recorder1.setLicensePlate(order.getLicensePlate());
				recorder1.setPayDate(new Date());
				recorder1.setProgramName(order.getProgramName());
			} else {
				recorder1 = new IncomeOrder();
				recorder1.setPayMethod(Integer.parseInt(order.getPayMethod1()));
				recorder1.setAmount(order.getActualPrice1());
				recorder1.setClientId(order.getClientId());
				recorder1.setLicensePlate(order.getLicensePlate());
				recorder1.setPayDate(new Date());
				recorder1.setProgramName(order.getProgramName());
			}
		}
		// 查看客户是否有卡,判断是否属于会员
		Client client = clientDao.findById(order.getClientId());
		if (client.getCards() == null || client.getCards().isEmpty()) {
			recorder.setMember(false);
		} else {
			recorder.setMember(true);
		}
		this.incomeOrderDao.save(recorder);
		if (recorder1 != null) {
			if (client.getCards() == null || client.getCards().isEmpty()) {
				recorder1.setMember(false);
			} else {
				recorder1.setMember(true);
			}
			this.incomeOrderDao.save(recorder1);
		}
		client.setConsumTimes(client.getConsumTimes() + 1);
		client.setConsumAmout(client.getConsumAmout() + recorder.getAmount());
		client.setLastVisit(new Date());

		if (list != null && !list.isEmpty()) {
			InventoryOrder inventoryOrder = new InventoryOrder();
			inventoryOrder.setId(IDGenerator.generate(IDGenerator.OUT_STOCK));
			inventoryOrder.setCreateDate(new Date());
			inventoryOrder.setInventoryInfos(list);
			inventoryOrder.setState(0);
			inventoryOrder.setTotalPrice(inventoryTotalPrice);
			inventoryOrder.setType(order.getProgramId());
			inventoryOrderDao.save(inventoryOrder);
			log.debug("出库订单(id:" + order.getId() + ")保存成功");
		}
		return RESCODE.SUCCESS.getJSONRES();

	}

	private void balancePay(String payMethod, float price) {
		Pattern pattern = Pattern.compile("\\w+\\-?\\w+");
		Matcher matcher = pattern.matcher(payMethod);
		while (matcher.find()) {
			String balanceCardNumber = matcher.group(0);
			Card card = cardDao.findByCardNumber(balanceCardNumber);
			card.setBalance(card.getBalance() - price);
		}
	}

	public String otherCard(String code) {
		if (code.length() != 11) {
			Card card = cardDao.findByCardNumber(code);
			if (card == null) {
				return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
			}
			return JsonResFactory
					.buildNetWithData(RESCODE.SUCCESS, JSONObject.fromObject(card, JsonResFactory.dateConfig()))
					.toString();
		} else {
			Client exist = clientDao.findByPhone(code);
			if (exist == null) {
				return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
			} else {
				if (exist.getCards() == null || exist.getCards().isEmpty()) {
					return JsonResFactory.buildOrg(RESCODE.NO_CARD).toString();
				} else {
					Set<Card> cards = exist.getCards();
					JSONArray array = JSONArray.fromObject(cards, JsonResFactory.dateConfig());
					return JsonResFactory.buildNetWithData(RESCODE.SUCCESS, array).toString();
				}
			}
		}
	}

	public void checkBalance(String payMethod, ConsumOrder consumOrder, float actualPrice) {
		// 判断储值卡余额是否能完成支付
		String tempPayMethod = payMethod;
		if (tempPayMethod == null || tempPayMethod.isEmpty() || tempPayMethod.trim().isEmpty()) {
			log.debug("订单id:" + consumOrder.getId() + "付款方式为空");
		} else if (tempPayMethod.matches("^5\\$\\w+\\-?\\w+\\$$")) {
			Pattern pattern = Pattern.compile("\\w+\\-?\\w+");
			Matcher matcher = pattern.matcher(tempPayMethod);
			while (matcher.find()) {
				log.debug("订单id:" + consumOrder.getId() + "付款方式为储值卡支付");
				String balanceCardNumber = matcher.group(0);
				log.debug("储值卡卡号为:" + balanceCardNumber);
				// 判断储值卡 扣金额是否足够
				Card card = cardDao.findByCardNumber(balanceCardNumber);
				if (card == null) {
					throw new ForRollbackException(RESCODE.NO_CARD.getMsg(), RESCODE.NO_CARD.getValue());
				} else if (card.getBalance() < actualPrice) {
					throw new ForRollbackException(RESCODE.CARD_BALANCE_NOT_ENOUGH.getMsg(),
							RESCODE.CARD_BALANCE_NOT_ENOUGH.getValue());
				}
			}
		}
	}

}
