/**
 *
 */
package com.geariot.platform.freelycar.service;

import com.geariot.platform.freelycar.dao.*;
import com.geariot.platform.freelycar.entities.*;
import com.geariot.platform.freelycar.exception.ExcelAnalyzeException;
import com.geariot.platform.freelycar.model.InsuranceExcelData;
import com.geariot.platform.freelycar.model.OrderSummary;
import com.geariot.platform.freelycar.model.RESCODE;
import com.geariot.platform.freelycar.utils.*;
import com.geariot.platform.freelycar.utils.CommonUtils.FileSuffix;
import net.sf.json.JSONObject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author mxy940127
 */
@Service
@Transactional
public class ReportService {

    @Autowired
    private ClientDao clientDao;

    @Autowired
    private ServletContext context;

    @Autowired
    private ProjectDao projectDao;

    @Autowired
    private ProgramDao programDao;

    @Autowired
    private InventoryDao inventoryDao;

    @Autowired
    private ProviderDao providerDao;

    @Autowired
    private InventoryBrandDao inventoryBrandDao;

    @Autowired
    private InventoryTypeDao inventoryTypeDao;

    @Autowired
    private OtherExpendOrderDao otherExpendOrderDao;

    @Autowired
    private IncomeOrderDao incomeOrderDao;

    @Autowired
    private ChargeDao chargeDao;

    @Autowired
    private ConsumOrderDao consumOrderDao;

    public boolean exportInsuranceExcel(HttpServletResponse response) {
        try {
            List<InsuranceExcelData> list = clientDao.getExcelData();
            String[] fields = {"序号", "车主姓名", "车牌号码", "手机号码", "保险公司", "询价时间", "客户意向"};
            InsuranceExcelExport export = new InsuranceExcelExport();
            HSSFWorkbook wb = export.generateExcel();
            wb = export.generateSheet(wb, "车险询价记录表", fields, list);
            export.export(wb, response);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean exportClientExcel(HttpServletResponse response) {
        try {
            List<Client> clients = clientDao.listAll();
            String[] fields = {"序号", "客户姓名", "手机号码", "车牌号码", "是否会员", "保险到期时间"};
            ClientExcelExport export = new ClientExcelExport();
            HSSFWorkbook wb = export.generateExcel();
            wb = export.generateSheet(wb, "客户详情表", fields, clients);
            export.export(wb, response);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean exportStatExcel(HttpServletResponse response) {
        return true;
    }

    public Map<String, Object> importProject(MultipartFile multipartFile) {
        EnumSet<FileSuffix> sets = EnumSet.of(FileSuffix.xls, FileSuffix.xlsx);
        Map<String, Object> uploadFile = CommonUtils.uploadFile(multipartFile, context.getRealPath("") + "ProjectUpload", sets);
        if ((int) uploadFile.get(Constants.RESPONSE_CODE_KEY) == RESCODE.SUCCESS.getValue()) {
            try {
                List<List<String>> analyzeExcel = CommonUtils.analyzeExcel((String) uploadFile.get(Constants.RESPONSE_DATA_KEY), 4);
                return dealProjectExcelDate(analyzeExcel);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (ExcelAnalyzeException e) {
                e.printStackTrace();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
        return RESCODE.FILE_ERROR.getJSONRES();
    }

    /**
     * 处理项目excel中的数据
     *
     * @param excelDate
     * @return
     */
    private Map<String, Object> dealProjectExcelDate(List<List<String>> excelDate) {
        Program beauty = programDao.insertIfExist(1, "美容");
        Program repair = programDao.insertIfExist(2, "维修");

        Project project = null;
        List<Integer> RepeatRow = new ArrayList<>();
        int row_index = 0;
        for (List<String> row : excelDate) {
            project = new Project();
            if (projectDao.findProjectByName(row.get(0)) != null) {
                RepeatRow.add(row_index);
            } else {
                project.setName(row.get(0));
                if (row.get(1).equals("美容")) {
                    project.setProgram(beauty);
                } else {
                    project.setProgram(repair);
                }
                float price = Float.parseFloat(row.get(2));
                project.setPrice(price);
                float hour = Float.parseFloat(row.get(3));
                project.setPricePerUnit(hour);
                float unitPrice = Float.parseFloat(row.get(4));
                project.setPricePerUnit(unitPrice);
                project.setComment(row.get(5));
                project.setCreateDate(DateHandler.getCurrentDate());
                projectDao.save(project);
            }
        }
        Map<String, Object> map = RESCODE.SUCCESS.getJSONRES();
        map.put("repeat", RepeatRow);
        return map;
    }

    public Map<String, Object> importInventory(MultipartFile multipartFile) {
        EnumSet<FileSuffix> sets = EnumSet.of(FileSuffix.xls, FileSuffix.xlsx);
        Map<String, Object> uploadFile = CommonUtils.uploadFile(multipartFile, context.getRealPath("") + "InventoryUpload", sets);
        if ((int) uploadFile.get(Constants.RESPONSE_CODE_KEY) == RESCODE.SUCCESS.getValue()) {
            try {
                List<List<String>> analyzeExcel = CommonUtils.analyzeExcel((String) uploadFile.get(Constants.RESPONSE_DATA_KEY), 4);
                return dealInventoryExcelDate(analyzeExcel);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (ExcelAnalyzeException e) {
                e.printStackTrace();
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
        return RESCODE.FILE_ERROR.getJSONRES();
    }

    /**
     * 处理项目excel中的数据
     *
     * @param excelDate
     * @return
     */
    private Map<String, Object> dealInventoryExcelDate(List<List<String>> excelDate) {
        Inventory inventory = null;

        InventoryType beauty = inventoryTypeDao.insertIfExist(1, "美容类");
        InventoryType repair = inventoryTypeDao.insertIfExist(2, "维修类");
        InventoryBrand unknown = inventoryBrandDao.insertIfExist(3, "未知");

        List<Integer> RepeatRow = new ArrayList<>();
        int row_index = 0;
        for (List<String> row : excelDate) {
            inventory = new Inventory();
            //设置配件名称、规格、属性(如三者相同 视为冲突)
            if (inventoryDao.checkUnique(row.get(1), row.get(4), row.get(5))) {
                inventory.setName(row.get(1));
                inventory.setStandard((row.get(4)));
                inventory.setProperty((row.get(5)));
            } else {
                RepeatRow.add(row_index);
                continue;
            }
            //设置厂家编号
            inventory.setManufactureNumber(row.get(0));
            //设置配件品牌
            String brandName = row.get(3);
            if (brandName == null || brandName.isEmpty() || brandName.trim().isEmpty() || brandName.equals("")) {
                inventory.setBrandId(unknown.getId());
                inventory.setBrandName(unknown.getName());
            } else {
                InventoryBrand brand = inventoryBrandDao.findByName(row.get(3));
                if (brand == null) {
                    brand = new InventoryBrand();
                    brand.setName(row.get(3));
                    brand.setCreateDate(DateHandler.getCurrentDate());
                    int brandId = inventoryBrandDao.add(brand);
                    inventory.setBrandName(row.get(3));
                    inventory.setBrandId(brandId);
                } else {
                    inventory.setBrandId(brand.getId());
                    inventory.setBrandName(brand.getName());
                }
            }
            //设置供应商
            String providerName = row.get(7);
            if (CommonUtils.isEmpty(providerName)) {
                inventory.setProvider(null);
            } else {
                Provider provider = providerDao.findProviderByName(providerName);
                if (provider == null) {
                    provider = new Provider();
                    provider.setCreateDate(DateHandler.getCurrentDate());
                    provider.setName(providerName);
                    providerDao.save(provider);
                    inventory.setProvider(provider);
                } else {
                    inventory.setProvider(provider);
                }
            }
            //设置Id
            inventory.setId(IDGenerator.generate(IDGenerator.INV_ID));
            //设置typeId,typeName
            if (row.get(2).equals("维修类")) {
                inventory.setTypeName("维修类");
                inventory.setTypeId(repair.getId());
            } else {
                inventory.setTypeName("美容类");
                inventory.setTypeId(beauty.getId());
            }
            //设置comment
            inventory.setComment(row.get(8));
            //设置金额
            float amount = Float.parseFloat(row.get(6));
            inventory.setAmount(amount);
        }
        Map<String, Object> map = RESCODE.SUCCESS.getJSONRES();
        map.put("repeat", RepeatRow);
        return map;
    }


    public boolean exportStatExcelWithMonth(int month, HttpServletResponse response, HttpServletRequest request) {
        try {
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            //测试用
//            int year = 2017;

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

            /*
             生成当前年月的数组
             */
            String firstDay = FisrtDayOfMonth.getFisrtDayOfMonth(year, month);
            String lastDay = LastDayOfMonth.getLastDayOfMonth(year, month);

            Date dBegin = sdf.parse(firstDay);
            Date dEnd = sdf.parse(lastDay);
            List<Date> lDate = DateUtil.findDates(dBegin, dEnd);

            /*
            获取收入信息
             */
            List<Object[]> earnings = incomeOrderDao.statInfoByMonth(firstDay);


            //获取其他支出类型
//            String[] otherExpend = null;
            List<String> otherExpendTypeNames = new ArrayList<>();
            List<Integer> otherExpendTypeIds = new ArrayList<>();
            List<OtherExpendType> otherExpendTypes = chargeDao.listAll();
            if (otherExpendTypes != null && !otherExpendTypes.isEmpty()) {
                for (OtherExpendType otherExpendType : otherExpendTypes) {
                    otherExpendTypeNames.add(otherExpendType.getName());
                    otherExpendTypeIds.add(otherExpendType.getId());
                }
            }

            //获取支出信息
            List<Object[]> expenditures = otherExpendOrderDao.statInfoByMonth(firstDay);


            //组装成导出的数据
            List<JSONObject> exportData = new ArrayList<>();

            for (Date date : lDate) {
                String currentDateString = sdf.format(date);
                JSONObject jsonExportData = new JSONObject();

                //日期
                cal.setTime(date);
                int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
                jsonExportData.put("dayOfMonth", dayOfMonth);

                // 0,1,2,3,4  现金,刷卡,支付宝,微信,易付宝
                for (Object[] earn : earnings) {
                    String payDate = (String) earn[2];
                    int payMethod = (int) earn[1];
                    double amount = (double) earn[0];
                    if (currentDateString.equals(payDate)) {
                        switch (payMethod) {
                            case 0:
                                jsonExportData.put("cash", amount);
                                break;
                            case 1:
                                jsonExportData.put("card", amount);
                                break;
                            case 2:
                                jsonExportData.put("alipay", amount);
                                break;
                            case 3:
                                jsonExportData.put("wechat", amount);
                                break;
                            default:
                                jsonExportData.put("otherpay", amount);
                                break;
                        }
                    }
                }

                //支出部分
                for (Object[] expenditure : expenditures) {
                    String expendDate = (String) expenditure[3];
                    Integer typeId = (Integer) expenditure[1];
                    Double amount = (Double) expenditure[0];

                    if (currentDateString.equals(expendDate)) {
                        for (Integer otherExpendTypeId : otherExpendTypeIds) {
                            if (otherExpendTypeId == typeId) {
                                jsonExportData.put(otherExpendTypeId, amount);
                            }
                        }
                    }
                }

                exportData.add(jsonExportData);
            }

            //导出列
            List<String> earningsTitle = new ArrayList<>();
            earningsTitle.add("现金");
            earningsTitle.add("微信");
            earningsTitle.add("支付宝");
            earningsTitle.add("刷卡");
            earningsTitle.add("其他");
            //将“其他支出类型”添加到fields中


            ClientExcelExport export = new ClientExcelExport();
            HSSFWorkbook wb = export.generateExcel();
            wb = export.generateBusinessSummarySheet(wb, month + "月份店面盈亏平衡", earningsTitle, otherExpendTypeNames, otherExpendTypeIds, exportData);
            export.downLoadExcel(month + "月份店面盈亏平衡.xls", wb, response, request);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 导出流水明细列表
     *
     * @param startTime
     * @param endTime
     * @param response
     * @param request
     * @return
     */
    public boolean exportOrderSummaryExcelWithDate(String startTime, String endTime, HttpServletResponse response, HttpServletRequest request) {
        try {
            //获取流水明细
            List<OrderSummary> orderSummaryList = consumOrderDao.listAllPaidOrders(startTime, endTime);

            String[] fields = new String[]{"序号", "车型", "车牌号码", "车主姓名", "联系方式", "消费项目", "金额", "时间", "是否会员"};


            ClientExcelExport export = new ClientExcelExport();
            HSSFWorkbook wb = export.generateExcel();
            wb = export.generateOrderSummarySheet(wb, "流水明细", fields, orderSummaryList);
            export.downLoadExcel("流水明细.xls", wb, response, request);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
