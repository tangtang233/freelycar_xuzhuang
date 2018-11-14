package com.geariot.platform.freelycar.controller;

import com.geariot.platform.freelycar.entities.Reservation;
import com.geariot.platform.freelycar.service.ReservationService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 唐炜
 */
@RestController
@RequestMapping(value = "/reservation")
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    /**
     * 添加一条预约
     *
     * @param reservation 预约数据实体对象
     * @return string
     */
    @ResponseBody
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String add(@RequestBody Reservation reservation) {
        return reservationService.add(reservation);
    }

    /**
     * 取消一条预约
     *
     * @param reservationId 主键ID
     * @return string
     */
    @ResponseBody
    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public String cancel(@RequestParam Integer reservationId) {
        return reservationService.cancel(reservationId);
    }


    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Map<String, Object> list(
            @RequestParam(name = "licensePlate", required = false) String licensePlate,
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "openId", required = false) String openId,
            @RequestParam(name = "state", required = false) Integer state,
            @RequestParam(name = "sortColumn", required = false) String sortColumn,
            @RequestParam(name = "sortType", required = false) String sortType,
            int page, int number
    ) {
        Map<String, Object> paramMap = new HashMap<>(6);
        if (StringUtils.isNotEmpty(licensePlate)) {
            paramMap.put("licensePlate", licensePlate);
        }
        if (StringUtils.isNotEmpty(name)) {
            paramMap.put("name", name);
        }
        if (StringUtils.isNotEmpty(openId)) {
            paramMap.put("openId", openId);
        }
        if (null != state) {
            paramMap.put("state", state);
        }
        if (StringUtils.isNotEmpty(sortColumn)) {
            paramMap.put("sortColumn", sortColumn);
        }
        if (StringUtils.isNotEmpty(sortType)) {
            paramMap.put("sortType", sortType);
        }
        return reservationService.list(paramMap, page, number);
    }
}
