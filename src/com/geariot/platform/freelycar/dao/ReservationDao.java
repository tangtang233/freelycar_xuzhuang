package com.geariot.platform.freelycar.dao;

import com.geariot.platform.freelycar.entities.Reservation;
import com.geariot.platform.freelycar.utils.hibernate.BaseDaoInter;

import java.util.List;
import java.util.Map;

/**
 * @author 唐炜
 */
public interface ReservationDao extends BaseDaoInter<Integer,Reservation> {
//    void saveOrUpdate(Reservation reservation);

//    Reservation save(Reservation reservation);

//    Reservation findById(Integer reservationId);

    List<Reservation> query(Map<String, Object> paramMap, int from, int pageSize);

    long getReservationCount(Map<String, Object> paramMap);
}
