package com.geariot.platform.freelycar.service;

import com.geariot.platform.freelycar.dao.AdminDao;
import com.geariot.platform.freelycar.dao.ConsumOrderDao;
import com.geariot.platform.freelycar.dao.StaffDao;
import com.geariot.platform.freelycar.entities.*;
import com.geariot.platform.freelycar.model.RESCODE;
import com.geariot.platform.freelycar.utils.Constants;
import com.geariot.platform.freelycar.utils.JsonPropertyFilter;
import com.geariot.platform.freelycar.utils.JsonResFactory;
import com.geariot.platform.freelycar.utils.MD5;
import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class StaffService {

    @Autowired
    private StaffDao staffDao;

    @Autowired
    private AdminDao adminDao;

    @Autowired
    private ConsumOrderDao consumOrderDao;

    public String addStaff(Staff staff) {
        Staff exist = staffDao.findStaffByPhone(staff.getPhone());
        if (exist != null) {
            return JsonResFactory.buildOrg(RESCODE.PHONE_EXIST).toString();
        } else {
            staff.setCreateDate(new Date());
            staff.setTechniciansAccount(0);
            staffDao.saveStaff(staff);
            JsonConfig config = JsonResFactory.dateConfig();
            return JsonResFactory.buildNetWithData(RESCODE.SUCCESS, net.sf.json.JSONObject.fromObject(staff, config)).toString();
        }
    }

    public String deleteStaff(Integer... staffIds) {
        String curUser = (String) SecurityUtils.getSubject().getPrincipal();
        Admin curAdmin = adminDao.findAdminByAccount(curUser);
        boolean delSelf = false;
        for (int staffId : staffIds) {
            //如果要删除的员工是当前登陆账号所绑定的员工，则不能删除
            if (curAdmin != null && curAdmin.getStaff().getId() == staffId) {
                delSelf = true;
            } else {
                //删除员工要将与员工绑定的Admin登陆账号删除
                //并且将ConsumOrder施工人员中有该员工的订单中去除该员工
                this.adminDao.deleteByStaffId(staffId);
                this.consumOrderDao.removeStaffInConsumOrderStaffs(staffId);
                //订单中相关接车人员设为空
                for (ConsumOrder c : this.consumOrderDao.findByPickCarStaffId(staffId)) {
                    c.setPickCarStaff(null);
                }

                staffDao.deleteStaff(staffId);
            }
        }
        if (delSelf) {
            return JsonResFactory.buildOrg(RESCODE.CANNOT_DELETE_SELF).toString();
        }
        return JsonResFactory.buildOrg(RESCODE.SUCCESS).toString();
    }

    public String getStaffList(int page, int number) {
        int from = (page - 1) * number;
        List<Staff> list = staffDao.listStaffs(from, number);
        if (list == null || list.isEmpty()) {
            return JsonResFactory.buildOrg(RESCODE.NO_RECORD).toString();
        }
        long realSize = staffDao.getCount();
        int size = (int) Math.ceil(realSize / (double) number);
        JsonConfig config = JsonResFactory.dateConfig();
        JSONArray jsonArray = JSONArray.fromObject(list, config);
        net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS, jsonArray);
        obj.put(Constants.RESPONSE_SIZE_KEY, size);
        obj.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
        return obj.toString();
    }

    public Map<String, Object> getSelectStaff(String staffId, String staffName, int page, int number) {
        int from = (page - 1) * number;
        List<Staff> list = staffDao.getConditionQuery(staffId, staffName, from, number);
        if (list == null || list.isEmpty()) {
            return RESCODE.NO_RECORD.getJSONRES();
        }
        long count = (long) staffDao.getConditionCount(staffId, staffName);
        int size = (int) Math.ceil(count / (double) number);
        return RESCODE.SUCCESS.getJSONRES(list, size, count);
    }

    public String modifyStaff(Staff staff) {
        Staff exist = staffDao.findStaffByStaffId(staff.getId());
        if (exist == null) {
            return JsonResFactory.buildOrg(RESCODE.NO_RECORD).toString();
        } else {
            if (!StringUtils.isEmpty(staff.getName())) {
                exist.setName(staff.getName());
            }
            if (!StringUtils.isEmpty(staff.getGender())) {
                exist.setGender(staff.getGender());
            }
            if (!StringUtils.isEmpty(staff.getPhone())) {
                exist.setPhone(staff.getPhone());
            }
            if (!StringUtils.isEmpty(staff.getPosition())) {
                exist.setPosition(staff.getPosition());
            }
            if (!StringUtils.isEmpty(staff.getLevel())) {
                exist.setLevel(staff.getLevel());
            }
            if (!StringUtils.isEmpty(staff.getComment())) {
                exist.setComment(staff.getComment());
            }
            if (!StringUtils.isEmpty(staff.getTechniciansAccount())) {
                exist.setTechniciansAccount(staff.getTechniciansAccount());
            }
            if (!StringUtils.isEmpty(staff.getAccount())) {
                exist.setAccount(staff.getAccount());
            }
            if (!StringUtils.isEmpty(staff.getPassword())) {
                exist.setPassword(MD5.compute(staff.getPassword()));
            }
        }
        return JsonResFactory.buildOrg(RESCODE.SUCCESS).toString();
    }

    public String staffServiceDetail(int staffId, int page, int number) {
        Staff exist = staffDao.findStaffByStaffId(staffId);
        if (exist == null) {
            return JsonResFactory.buildOrg(RESCODE.NO_RECORD).toString();
        } else {
            int from = (page - 1) * number;
            List<ProjectInfo> list = staffDao.staffServiceDetails(staffId, from, number);
            if (list == null || list.isEmpty()) {
                net.sf.json.JSONObject obj = JsonResFactory.buildNet(RESCODE.NOT_FOUND);
                obj.put("staffInfo", exist);
                return obj.toString();
            }
            JsonConfig config = JsonResFactory.dateConfig();
            JsonPropertyFilter filter = new JsonPropertyFilter();
            filter.setColletionProperties(Car.class, Card.class, Staff.class, Inventory.class);
            config.setJsonPropertyFilter(filter);
            JSONArray jsonArray = JSONArray.fromObject(list, config);
            net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS, jsonArray);
            obj.put(Constants.RESPONSE_REAL_SIZE_KEY, list.size());
            obj.put("staffInfo", exist);
            return obj.toString();
        }
    }

    /**
     * 开通或关闭技师账号
     * @param staff
     * @return
     */
    public String openOrCloseStaffLoginAccount(Staff staff) {
        int staffId = staff.getId();
        Staff exist = staffDao.findStaffByStaffId(staffId);
        if (null == exist) {
            return JsonResFactory.buildOrg(RESCODE.NO_RECORD).toString();
        }
        Integer flag = staff.getTechniciansAccount();
        if (null != flag) {
            //关闭账号
            if (flag == 0) {
                exist.setTechniciansAccount(staff.getTechniciansAccount());
                exist.setAccount("");
                exist.setPassword("");
                staffDao.saveStaff(exist);
                return JsonResFactory.buildOrg(RESCODE.SUCCESS).toString();
            }
            //开通账号
            if (flag == 1) {
                String staffAccount = staff.getAccount();
                String staffPasswordPlaintext = staff.getPassword();
                if (StringUtils.isEmpty(staffAccount)) {
                    return JsonResFactory.buildOrg(RESCODE.WRONG_PARAM).toString();
                }
                if (StringUtils.isEmpty(staffPasswordPlaintext)) {
                    return JsonResFactory.buildOrg(RESCODE.WRONG_PARAM).toString();
                }

                //账号验重
                Staff repeatAccountStaff = staffDao.verifyTheRepeat(staffId, staffAccount);
                if (null != repeatAccountStaff) {
                    return JsonResFactory.buildOrg(RESCODE.ACCOUNT_EXIST).toString();
                }

                exist.setAccount(staffAccount);
                exist.setPassword(MD5.compute(staffPasswordPlaintext));
                exist.setTechniciansAccount(staff.getTechniciansAccount());
                staffDao.saveStaff(exist);
                return JsonResFactory.buildOrg(RESCODE.SUCCESS).toString();
            }
        }
        return JsonResFactory.buildOrg(RESCODE.WRONG_PARAM).toString();
    }
}
	

