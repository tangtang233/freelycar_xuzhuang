/**
 * 
 */
package com.geariot.platform.freelycar.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.geariot.platform.freelycar.dao.FavourDao;
import com.geariot.platform.freelycar.entities.Favour;
import com.geariot.platform.freelycar.model.RESCODE;
import com.geariot.platform.freelycar.utils.Constants;
import com.geariot.platform.freelycar.utils.DateHandler;
import com.geariot.platform.freelycar.utils.JsonResFactory;

import net.sf.json.JsonConfig;

/**
 * @author mxy940127
 *
 */

@Service
@Transactional
public class FavourService {
	
	@Autowired
	private FavourDao favourDao;
	
	public String addFavour(Favour favour){
		if(favour.getBuyDeadline() !=null){
			Date buyDeadline = favour.getBuyDeadline();
			favour.setBuyDeadline(DateHandler.setTimeToEndofDay(buyDeadline));
		}
		if(favour.getBuyStartline() !=null){
			Date buyStartline = favour.getBuyStartline();
			favour.setBuyDeadline(DateHandler.setTimeToBeginningOfDay(buyStartline));
		}
		favour.setCreateDate(new Date());
		favourDao.save(favour);
		JsonConfig config = JsonResFactory.dateConfig();
		return JsonResFactory.buildNetWithData(RESCODE.SUCCESS, net.sf.json.JSONObject.fromObject(favour, config)).toString();
	}
	
	public String delFavour(Integer... favourIds){
		int count = 0;
		for(int favourId : favourIds){
			Favour exist = favourDao.findByFavourId(favourId);
			if(exist == null){
				count++;
			}
			else{
				exist.setDeleted(true);
				favourDao.save(exist);
			}
		}
		if(count !=0){
			String tips = "共"+count+"条未在数据库中存在记录";
			net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.PART_SUCCESS , tips);
			long realSize = favourDao.getCount();
			obj.put(Constants.RESPONSE_REAL_SIZE_KEY,realSize);
			return obj.toString();
		}
		else{
			JSONObject obj = JsonResFactory.buildOrg(RESCODE.SUCCESS);
			long realSize = favourDao.getCount();
			obj.put(Constants.RESPONSE_REAL_SIZE_KEY,realSize);
			return obj.toString();
		}
	}
	
	public Map<String,Object> query(String name, String type, int page, int number){
		int from = ( page - 1 ) * number ;
		List<Favour> favours = favourDao.queryByName(name, type, from, number);
		if(favours == null || favours.isEmpty()){
			return RESCODE.NOT_FOUND.getJSONRES();
		} else{
			long realSize = favourDao.getConditionCount(name, type);
			int size = (int) Math.ceil(realSize/(double)number);
			return RESCODE.SUCCESS.getJSONRES(favours, size, realSize);
		}
	}
}
