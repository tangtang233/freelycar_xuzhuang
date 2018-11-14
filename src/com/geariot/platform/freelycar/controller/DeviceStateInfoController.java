package com.geariot.platform.freelycar.controller;

import com.geariot.platform.freelycar.service.DeviceStateInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author 唐炜
 */
@RestController
@RequestMapping(value = "/deviceStateInfo")
public class DeviceStateInfoController {
    @Autowired
    private DeviceStateInfoService deviceStateInfoService;

    /**
     * 查询某智能柜下所有柜子的使用状态
     * @param cabinetSN
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/showDeviceStateInfo", method = RequestMethod.GET)
    public Map<String, Object> showDeviceStateInfo(
            @RequestParam(name = "cabinetSN") String cabinetSN
    ) {
        return deviceStateInfoService.showDeviceStateInfo(cabinetSN);
    }
}
