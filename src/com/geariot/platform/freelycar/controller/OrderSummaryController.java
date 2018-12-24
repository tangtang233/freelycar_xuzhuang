package com.geariot.platform.freelycar.controller;

import com.geariot.platform.freelycar.service.OrderSummaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 订单汇总
 *
 * @author tangwei - Toby
 * @date 2018-12-21
 * @email toby911115@gmail.com
 */
@RestController
@RequestMapping(value = "/orderSummary")
public class OrderSummaryController {
    @Autowired
    private OrderSummaryService orderSummaryService;

    @GetMapping(value = "/list")
    public Map<String, Object> list(@RequestParam String startTime, @RequestParam String endTime, @RequestParam int page, @RequestParam int pageSize) {
        startTime = startTime.replaceAll("/", "-");
        endTime = endTime.replaceAll("/", "-");
        return orderSummaryService.listAllPaidOrders(startTime, endTime, page, pageSize);
    }
}
