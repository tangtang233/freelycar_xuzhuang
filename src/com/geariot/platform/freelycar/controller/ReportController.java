/**
 *
 */
package com.geariot.platform.freelycar.controller;

import com.geariot.platform.freelycar.exception.ForRollbackException;
import com.geariot.platform.freelycar.model.RESCODE;
import com.geariot.platform.freelycar.service.ReportService;
import com.geariot.platform.freelycar.shiro.PermissionRequire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;


/**
 * @author mxy940127
 */

@Controller
@RequestMapping(value = "/report")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @RequestMapping(value = "/insurance", method = RequestMethod.GET)
    @PermissionRequire("report:insurance")
    public void exportInsuranceExcel(HttpServletResponse response) {
        reportService.exportInsuranceExcel(response);
    }

    @RequestMapping(value = "/client", method = RequestMethod.GET)
    @PermissionRequire("report:client")
    public void exportClientExcel(HttpServletResponse response) {
        reportService.exportClientExcel(response);
    }

    @RequestMapping(value = "/stat", method = RequestMethod.GET)
    @PermissionRequire("report:stat")
    public void exportStatExcel(HttpServletResponse response) {
        reportService.exportStatExcel(response);
    }

    @RequestMapping(value = "/file", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> importProject(@RequestParam("file") MultipartFile file) {
        try {
            return reportService.importProject(file);
        } catch (ForRollbackException e) {
            return RESCODE.FOR_EXCEPTION.getJSONRES(e);
        }
    }


    @RequestMapping(value = "/inventory", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> importInventory(@RequestParam("file") MultipartFile multipartFile) {
        try {
            return reportService.importInventory(multipartFile);
        } catch (ForRollbackException e) {
            return RESCODE.FOR_EXCEPTION.getJSONRES(e);
        }
    }


    @RequestMapping(value = "/statMonth", method = RequestMethod.GET)
    @PermissionRequire("client:statMonth")
    public void exportStatExcelWithMonth(int month, HttpServletResponse response, HttpServletRequest request) {
        reportService.exportStatExcelWithMonth(month, response, request);
    }

    @RequestMapping(value = "/orderSummary", method = RequestMethod.GET)
    @PermissionRequire("client:orderSummary")
    public void exportOrderSummaryExcelWithDate(String startTime, String endTime, HttpServletResponse response, HttpServletRequest request) {
        startTime = startTime.replaceAll("/", "-");
        endTime = endTime.replaceAll("/", "-");
        reportService.exportOrderSummaryExcelWithDate(startTime, endTime, response, request);
    }
}
