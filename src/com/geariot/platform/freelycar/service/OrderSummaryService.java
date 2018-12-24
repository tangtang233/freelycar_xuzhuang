package com.geariot.platform.freelycar.service;

import com.geariot.platform.freelycar.dao.ConsumOrderDao;
import com.geariot.platform.freelycar.model.OrderSummary;
import com.geariot.platform.freelycar.model.RESCODE;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * @author tangwei - Toby
 * @date 2018-12-21
 * @email toby911115@gmail.com
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
public class OrderSummaryService {
    @Autowired
    private ConsumOrderDao consumOrderDao;

    public Map<String, Object> listAllPaidOrders(String startTime, String endTime, int page, int number) {
        int from = (page - 1) * number;
        List<OrderSummary> list = consumOrderDao.listAllPaidOrders(startTime, endTime, from, number);
        if (null == list || list.isEmpty()) {
            return RESCODE.NOT_FOUND.getJSONRES();
        }

        long count = consumOrderDao.getAllPaidOrdersCount(startTime, endTime);
        int size = (int) Math.ceil(count / (double) number);
        return RESCODE.SUCCESS.getJSONRES(list, size, count);
    }

    public List<OrderSummary> listAllPaidOrders(String startTime, String endTime) {
        return consumOrderDao.listAllPaidOrders(startTime, endTime);
    }
}
