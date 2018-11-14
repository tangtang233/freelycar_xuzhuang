package com.geariot.platform.freelycar.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geariot.platform.freelycar.dao.CardDao;
import com.geariot.platform.freelycar.dao.FavourDao;
import com.geariot.platform.freelycar.dao.IncomeOrderDao;
import com.geariot.platform.freelycar.entities.Card;
import com.geariot.platform.freelycar.entities.IncomeOrder;
import com.geariot.platform.freelycar.entities.Ticket;
import com.geariot.platform.freelycar.model.RESCODE;
import com.geariot.platform.freelycar.utils.Constants;

@Service
@Transactional
public class CardService {

	@Autowired
	private CardDao cardDao;
	
	@Autowired
	private FavourDao favourDao;
	
	@Autowired
	private IncomeOrderDao incomeDao;
	
	public Map<String,Object> getAvailableCard(int clientId, int projectId) {
		List<Integer> cardIds = cardDao.getAvailableCardId(projectId);
		List<Integer> ticketIds = favourDao.getAvailableTicketId(projectId);
		
		if(cardIds == null && ticketIds == null || (ticketIds.isEmpty() && cardIds.isEmpty())){
			return RESCODE.NOT_FOUND.getJSONRES();
		}
		List<Card> cards = cardDao.getAvailableCard(clientId, cardIds);
		List<Ticket> tickets = favourDao.getAvailableTicket(clientId, ticketIds);
		
		Map<String, Object> jsonres = RESCODE.SUCCESS.getJSONRES();
		if(!cards.isEmpty() && !tickets.isEmpty()){
			jsonres.put("data", cards);
			jsonres.put("ticket", tickets);
		} else if (!cards.isEmpty()){
			jsonres.put("data", cards);
		} else if(!tickets.isEmpty()){
			jsonres.put("data", tickets);
		} else {
			return RESCODE.NOT_FOUND.getJSONRES();
		}
		return jsonres;
		
	}
	
	
	public Map<String,Object> renewal(int cardId, float payMoney, float cardMoney,int payMethod ,int handlerId) {
		//忘卡里冲钱
		Card card = cardDao.updateBalanceByCardId(cardId,cardMoney,handlerId);
		//记上收入
		IncomeOrder income = new IncomeOrder(card.getClientId(), payMoney, new Date(), payMethod, Constants.PROGRAM_RENEWALCARD);
		incomeDao.save(income);
		return RESCODE.SUCCESS.getJSONRES();
	}
	
}
