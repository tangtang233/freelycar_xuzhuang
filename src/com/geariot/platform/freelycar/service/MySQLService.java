///**
// * 
// */
//package com.geariot.platform.freelycar.service;
//
//import java.util.List;
//import java.util.Map;
//
//import javax.transaction.Transactional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.geariot.platform.freelycar.dao.CardDao;
//import com.geariot.platform.freelycar.dao.ClientDao;
//import com.geariot.platform.freelycar.entities.Card;
//import com.geariot.platform.freelycar.entities.Client;
//import com.geariot.platform.freelycar.model.RESCODE;
//
///**
// * @author mxy940127
// *
// */
//@Service
//@Transactional
//public class MySQLService {
//	
//	@Autowired
//	private CardDao cardDao;
//	
//	@Autowired
//	private ClientDao clientDao;
//	
//	//对数据库client isMember为空字段的处理
//	public Map<String, Object> dealMember() {
//		List<Client> clients = clientDao.listAll();
//		for(Client client : clients){
//			List<Card> cards = cardDao.findCardByClientId(client.getId());
//			if(client.getIsMember() == null){
//				if(cards.size()>0){
//					client.setIsMember(true);
//				}else{
//					client.setIsMember(false);
//				}
//			}
//		}
//		return RESCODE.SUCCESS.getJSONRES();
//	}
//
//}
