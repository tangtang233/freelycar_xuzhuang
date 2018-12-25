package com.geariot.platform.freelycar.utils;

import com.geariot.platform.freelycar.entities.Car;
import com.geariot.platform.freelycar.entities.Client;
import com.geariot.platform.freelycar.model.OrderSummary;
import net.sf.json.JSONObject;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class ClientExcelExport {

    static Logger log = Logger.getLogger(ClientExcelExport.class);

    // 第一步，创建一个webbook，对应一个Excel文件
    public HSSFWorkbook generateExcel() {
        return new HSSFWorkbook();
    }

    public HSSFWorkbook generateSheet(HSSFWorkbook wb, String sheetName, String[] fields, List<Client> list) {

        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        HSSFRow row = sheet.createRow(0);
        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // 设置表头字段名
        HSSFCell cell;
        int m = 0;
        for (String fieldName : fields) {
            sheet.setDefaultColumnStyle(m, style);
            cell = row.createCell(m);
            cell.setCellValue(fieldName);
            m++;
        }

        for (int i = 0; i < list.size(); i++) {
            row = sheet.createRow(i + 1);
            Client data = list.get(i);
            // 第五步，创建单元格，并设置值

            //设置序号
            row.createCell(0).setCellValue(i + 1);
            //设置客户姓名
            row.createCell(1).setCellValue(data.getName());
            //设置手机号
            row.createCell(2).setCellValue(data.getPhone());
            //设置是否会员
            if (data.getIsMember() == null) {
                row.createCell(4).setCellValue("否");
            } else {
                if (data.getIsMember()) {
                    row.createCell(4).setCellValue("是");
                } else {
                    row.createCell(4).setCellValue("否");
                }
            }
            String licensePlate = "";
            String licenseDate = "";
            Set<Car> cars = data.getCars();
            if (cars == null || cars.isEmpty()) {
                row.createCell(3).setCellValue("空");
                row.createCell(5).setCellValue("空");
            } else {
                for (Car car : cars) {
                    licensePlate = licensePlate + (car.getLicensePlate() == null ? "空" : car.getLicensePlate()) + ",";
                    licenseDate = licenseDate + (car.getInsuranceEndtime() == null ? "空" : sdf.format(car.getInsuranceEndtime())) + ",";
                }
                int plateLength = licensePlate.length();
                int dateLength = licenseDate.length();
                licenseDate = licenseDate.substring(0, dateLength - 1);
                licensePlate = licensePlate.substring(0, plateLength - 1);
                row.createCell(3).setCellValue(licensePlate);
                row.createCell(5).setCellValue(licenseDate);
            }
        }
        return wb;
    }

    public void export(HSSFWorkbook wb, HttpServletResponse response) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = new Date();
        try {
            response.setHeader("content-disposition",
                    "attachment;filename=" + URLEncoder.encode("客户信息表" + sdf.format(now), "utf-8") + ".xls");
            OutputStream out = response.getOutputStream();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            wb.write(baos);
            byte[] xlsBytes = baos.toByteArray();
            out.write(xlsBytes);
            out.close();
        } catch (IOException e) {
            log.error("IOException:" + e.getMessage());
        }

    }

    public HSSFWorkbook generateBusinessSummarySheet(HSSFWorkbook wb, String sheetName, List<String> earningsTitle, List<String> expendituresTitle, List<Integer> expenditureIds, List<JSONObject> exportData) {

        //计算“支出”的合并单元格需要额外扩展多少，若没有支出项，也需要占一个格子
        int expenditureCellMergeSize = 0;
        if (expenditureIds != null) {
            if (expenditureIds.isEmpty()) {
                expenditureCellMergeSize = 1;
            } else {
                expenditureCellMergeSize = expenditureIds.size();
            }
        }

        //创建合并单元格对象
        CellRangeAddress cellRangeAddressForTitle = new CellRangeAddress(0, 0, 0, 6 + expenditureCellMergeSize - 1);
        CellRangeAddress cellRangeAddressForEarnings = new CellRangeAddress(1, 1, 1, 5);
        CellRangeAddress expendituresForEarnings = new CellRangeAddress(1, 1, 6, 6 + expenditureCellMergeSize);

        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);

        //设置合并的单元格
        sheet.addMergedRegion(cellRangeAddressForTitle);
        sheet.addMergedRegion(cellRangeAddressForEarnings);
        sheet.addMergedRegion(expendituresForEarnings);

        HSSFCell cell;


        //创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        HSSFRow row = sheet.createRow(0);
        //第一行为文档的标题
        cell = row.createCell(0);
        cell.setCellValue(sheetName);
        cell.setCellStyle(style);

        //设置“收入”、“支出”的头
        row = sheet.createRow(1);
        cell = row.createCell(1);
        cell.setCellValue("收入");
        cell.setCellStyle(style);
        cell = row.createCell(6);
        cell.setCellValue("支出");
        cell.setCellStyle(style);


        // 设置表头字段名
        //添加“日期”的头字段
        row = sheet.createRow(2);
        sheet.setDefaultColumnStyle(0, style);
        cell = row.createCell(0);
        cell.setCellValue("日期");

        //添加其他的收支字段
        row = sheet.createRow(3);
        //将收入和支出的头字段合并到一个数组
        earningsTitle.addAll(expendituresTitle);
        int m = 1;
        for (String fieldName : earningsTitle) {
            sheet.setDefaultColumnStyle(m, style);
            cell = row.createCell(m);
            cell.setCellValue(fieldName);
            m++;
        }

        for (int i = 0; i < exportData.size(); i++) {
            row = sheet.createRow(i + 4);
            JSONObject data = exportData.get(i);

            //获取每一行记录的各个字段
            int dayOfMonth = data.get("dayOfMonth") == null ? Integer.valueOf(i + 1) : (Integer) data.get("dayOfMonth");
            double cashAmount = data.get("cash") == null ? Double.valueOf(0) : (Double) data.get("cash");
            double cardAmount = data.get("card") == null ? Double.valueOf(0) : (Double) data.get("card");
            double alipayAmount = data.get("alipay") == null ? Double.valueOf(0) : (Double) data.get("alipay");
            double wechatAmount = data.get("wechat") == null ? Double.valueOf(0) : (Double) data.get("wechat");
            double otherpayAmount = data.get("otherpay") == null ? Double.valueOf(0) : (Double) data.get("otherpay");


            //0:日期
            row.createCell(0).setCellValue(dayOfMonth);
            //1:现金
            row.createCell(1).setCellValue(cashAmount);
            //2：微信
            row.createCell(2).setCellValue(wechatAmount);
            //3：支付宝
            row.createCell(3).setCellValue(alipayAmount);
            //4：刷卡
            row.createCell(4).setCellValue(cardAmount);
            //5：其他
            row.createCell(5).setCellValue(otherpayAmount);

            for (int j = 0; j < expenditureIds.size(); j++) {
                row.createCell(j + 6).setCellValue(data.get(String.valueOf(expenditureIds.get(j))) == null ? Double.valueOf(0) : (Double) data.get(String.valueOf(expenditureIds.get(j))));
            }
        }
        return wb;
    }

    public void downLoadExcel(String fileName, HSSFWorkbook workbook, HttpServletResponse response, HttpServletRequest request) {
        OutputStream out = null;
        try {
            out = response.getOutputStream();
            response.setContentType("application/x-msdownload");
            this.setHeader(response, request, fileName);
            workbook.write(out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setHeader(HttpServletResponse response,
                           HttpServletRequest request, String filename)
            throws UnsupportedEncodingException {
        response.reset();
        // 设置为下载 application/x-download
        response.setContentType("application/x-download charset=UTF-8");
        // 通常解决汉字乱码方法用 URLEncoder.encode(...)
        String filenamedisplay = URLEncoder.encode(filename, "UTF-8");
        if ("FF".equals(getBrowser(request))) {
            // 针对火狐浏览器处理方式不一样了
            filenamedisplay = new String(filename.getBytes("UTF-8"),
                    "iso-8859-1");
        }
        response.setHeader("Content-Disposition", "attachment;filename="
                + filenamedisplay);
    }

    // 以下为服务器端判断客户端浏览器类型的方法
    private String getBrowser(HttpServletRequest request) {
        String UserAgent = request.getHeader("USER-AGENT").toLowerCase();
        if (UserAgent != null) {
            if (UserAgent.indexOf("msie") >= 0)
                return "IE";
            if (UserAgent.indexOf("firefox") >= 0)
                return "FF";
            if (UserAgent.indexOf("safari") >= 0)
                return "SF";
        }
        return null;
    }


    public HSSFWorkbook generateOrderSummarySheet(HSSFWorkbook wb, String sheetName, String[] fields, List<OrderSummary> orderSummaryList) {

        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
        HSSFSheet sheet = wb.createSheet(sheetName);
        // 第三步，在sheet中添加表头第0行,注意老版本poi对Excel的行数列数有限制short
        HSSFRow row = sheet.createRow(0);
        // 第四步，创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = wb.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        // 设置表头字段名
        HSSFCell cell;
        int m = 0;
        for (String fieldName : fields) {
            sheet.setDefaultColumnStyle(m, style);
            cell = row.createCell(m);
            cell.setCellValue(fieldName);
            m++;
        }

        for (int i = 0; i < orderSummaryList.size(); i++) {
            row = sheet.createRow(i + 1);
            OrderSummary data = orderSummaryList.get(i);
            // 第五步，创建单元格，并设置值

            //设置序号
            row.createCell(0).setCellValue(i + 1);
            //设置车型
            row.createCell(1).setCellValue(data.getCarBrand());
            //设置车牌号码
            row.createCell(2).setCellValue(data.getLicensePlate());
            //设置车主姓名
            row.createCell(3).setCellValue(data.getName());
            //设置联系方式
            row.createCell(4).setCellValue(data.getPhone());
            //设置消费项目
            row.createCell(5).setCellValue(data.getProjectName());
            //设置金额
            row.createCell(6).setCellValue(data.getTotalActualPrice());
            //设置时间
            row.createCell(7).setCellValue(data.getCreateDate());
            //设置车牌号码
            row.createCell(8).setCellValue("1".equals(data.getIsMember()) ? "是" : "否");
        }
        return wb;
    }

}
