package com.geariot.platform.freelycar.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.geariot.platform.freelycar.service.CardService;

@RestController
@RequestMapping("/card")
public class CardController {
	
	@Autowired
	private CardService cardService;
	
	@RequestMapping(value="/avail", method=RequestMethod.GET)
	public Map<String,Object> getAvailableCard(int clientId, int projectId){
		return cardService.getAvailableCard(clientId, projectId);
	}
	
	/**
	 * 
	 * @param cardId card的id
	 * @param payMoney 你实际给的钱
	 * @param cardMoney 卡的价值
	 * @param payMethod 支付方式
	 * @return
	 */
	@RequestMapping(value="/renewal", method=RequestMethod.POST)
	public Map<String,Object> renewal(int cardId, float payMoney, float cardMoney,int payMethod,int handlerId){
		return cardService.renewal(cardId, payMoney,cardMoney,payMethod,handlerId);
	}
	
}
