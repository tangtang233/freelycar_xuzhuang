/**
 * 
 */
package com.geariot.platform.freelycar.dao;

import java.util.List;

import com.geariot.platform.freelycar.entities.Favour;
import com.geariot.platform.freelycar.entities.FavourProjectInfos;
import com.geariot.platform.freelycar.entities.FavourProjectRemainingInfo;
import com.geariot.platform.freelycar.entities.Ticket;

/**
 * @author mxy940127
 *
 */
public interface FavourDao {

	void save(Favour favour);
	
	void delete(int favourId);
	
	List<Favour> queryByName(String name, String type, int from, int pageSize);
	
	Favour findByFavourId(int favourId);
	
	long getCount();
	
	long getConditionCount(String name, String type);
	
	List<Ticket> getAllNotFailed();
	
	Ticket findById(int id);
	
	int findByProjectIdAndFavourId(int projectId, int favourId);
	
	FavourProjectInfos findByFavourProjectInfosId(int id);
	
	FavourProjectRemainingInfo getProjectRemainingInfo(int ticketId, int projectId);
	
	List<Integer> getAvailableTicketId(int projectId);
	
	List<Ticket> getAvailableTicket(int clientId, List<Integer> ticketIds);
	
	List<Ticket> getTicketByClientId(int clientId);
}
