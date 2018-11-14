/**
 * 
 */
package com.geariot.platform.freelycar.model;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.geariot.platform.freelycar.dao.CarDao;
import com.geariot.platform.freelycar.dao.FavourDao;
import com.geariot.platform.freelycar.dao.WXUserDao;
import com.geariot.platform.freelycar.entities.Car;
import com.geariot.platform.freelycar.entities.Client;
import com.geariot.platform.freelycar.entities.Ticket;
import com.geariot.platform.freelycar.entities.WXUser;
import com.geariot.platform.freelycar.utils.DateHandler;
import com.geariot.platform.freelycar.wxutils.WechatTemplateMessage;

/**
 * @author mxy940127
 *
 */

@Component
@Transactional
public class InsuranceRemind {

	@Autowired
	private CarDao carDao;

	@Autowired
	private WXUserDao wxUserDao;
	
	@Autowired
	private FavourDao favourDao;
	
	@Scheduled(cron = "0 0 8-18 * * ?") // 每天8-18点准点检查失效券
	public void checkTicket(){
	List<Ticket> list1 = favourDao.getAllNotFailed();
	for(Ticket remainings : list1){
		Date now = new Date();
		if(now.getTime() - remainings.getExpirationDate().getTime() > 0){
			remainings.setFailed(true);
		}
	}}

	@Scheduled(cron = "0 0 8-18 * * ?") // 每天8-18点准点推送消息
	public void sendRemind() {
		Date now = new Date();
		List<Car> list = carDao.insuranceRemind();
		if (list != null && !list.isEmpty()) {
			for (Car car : list) {
				if (car.getInsuranceEndtime() != null) {
					if (car.getClient() != null) {
						Client client = car.getClient();
						if (client.getPhone() != null) {
							WXUser wxUser = wxUserDao.findUserByPhone(client.getPhone());
							if (wxUser != null && wxUser.getOpenId() != null) {
								if (DateHandler.insuranceCheck(DateHandler.toCalendar(now), DateHandler.toCalendar(car.getInsuranceEndtime()))) {
									WechatTemplateMessage.insuranceRemind(car, wxUser.getOpenId(), wxUser);
									car.setNeedInsuranceRemind(false);
								}
							}
						}
					}
				}
			}
		}
	}

	@Scheduled(cron = "0 0 8-18 * * ?") // 每天8-18点准点推送消息
	public void sendCheck() {
		Date now = new Date();
		List<Car> list = carDao.annualCheck();
		if (list != null && !list.isEmpty()) {
			for (Car car : list) {
				if (car.getLicenseDate() != null) {
					if (car.getClient() != null) {
						Client client = car.getClient();
						if (client.getPhone() != null) {
							WXUser wxUser = wxUserDao.findUserByPhone(client.getPhone());
							if (wxUser != null && wxUser.getOpenId() != null) {
									switch(DateHandler.annualCheck(DateHandler.toCalendar(now), DateHandler.toCalendar(car.getLicenseDate()))){
										case 0:
											break;
										case 1:
											WechatTemplateMessage.annualCheckRemind(car.getLicensePlate(),wxUser.getOpenId(),wxUser,car.getLicenseDate());
											car.setNeedInspectionRemind(false);
											break;
										case 2:
											WechatTemplateMessage.annualCheckRemind(car.getLicensePlate(),wxUser.getOpenId(),wxUser,DateHandler.addValidYear(DateHandler.toCalendar(car.getLicenseDate()), 2).getTime());
											car.setNeedInspectionRemind(false);
											break;
										case 4:
											WechatTemplateMessage.annualCheckRemind(car.getLicensePlate(),wxUser.getOpenId(),wxUser,DateHandler.addValidYear(DateHandler.toCalendar(car.getLicenseDate()), 4).getTime());
											car.setNeedInspectionRemind(false);
											break;
										case 6:
											WechatTemplateMessage.annualCheckRemind(car.getLicensePlate(),wxUser.getOpenId(),wxUser,DateHandler.addValidYear(DateHandler.toCalendar(car.getLicenseDate()), 6).getTime());
											car.setNeedInspectionRemind(false);
											break;
										default:
											break;
									}
								}
							}
						}
					}
				}
			}
		}
}

