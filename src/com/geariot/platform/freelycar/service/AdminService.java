package com.geariot.platform.freelycar.service;


import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.geariot.platform.freelycar.dao.AdminDao;
import com.geariot.platform.freelycar.dao.CardDao;
import com.geariot.platform.freelycar.dao.ConsumOrderDao;
import com.geariot.platform.freelycar.dao.InventoryOrderDao;
import com.geariot.platform.freelycar.entities.Admin;
import com.geariot.platform.freelycar.entities.Card;
import com.geariot.platform.freelycar.entities.ConsumOrder;
import com.geariot.platform.freelycar.entities.InventoryOrder;
import com.geariot.platform.freelycar.entities.Role;
import com.geariot.platform.freelycar.model.RESCODE;
import com.geariot.platform.freelycar.utils.Constants;
import com.geariot.platform.freelycar.utils.DateHandler;
import com.geariot.platform.freelycar.utils.DateJsonValueProcessor;
import com.geariot.platform.freelycar.utils.JsonResFactory;
import com.geariot.platform.freelycar.utils.MD5;
import com.geariot.platform.freelycar.utils.PermissionsList;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

@Service
@Transactional
public class AdminService {
	
	private static final Logger log = LogManager.getLogger(AdminService.class);
	
	@Autowired
	private AdminDao adminDao;
	
	@Autowired
	private InventoryOrderDao inventoryOrderDao;
	
	@Autowired
	private CardDao cardDao;
	
	@Autowired
	private ConsumOrderDao consumOrderDao;
	
	public Admin findAdminByAccount(String account) {
		return adminDao.findAdminByAccount(account);
	}
	
	public String login(String account, String password, boolean rememberMe) {
		log.debug("账户：" + account + " 尝试登陆");
		JSONObject obj = null;
		Subject curUser = SecurityUtils.getSubject();
		if (!curUser.isAuthenticated()) {
			UsernamePasswordToken token = new UsernamePasswordToken(account, password);
			token.setRememberMe(rememberMe);
            try {
            	curUser.login(token);
            	obj = JsonResFactory.buildOrg(RESCODE.SUCCESS);
            } catch (UnknownAccountException ue) {
            	/*ue.printStackTrace();
            	System.out.println(ue.getMessage());*/
                obj = JsonResFactory.buildOrg(RESCODE.ACCOUNT_ERROR);
                return obj.toString();
            } catch (IncorrectCredentialsException ie) {
            	/*ie.printStackTrace();
            	System.out.println(ie.getMessage());*/
            	obj = JsonResFactory.buildOrg(RESCODE.PSW_ERROR);
            	return obj.toString();
            } catch (LockedAccountException le) {
            	/*le.printStackTrace();
            	System.out.println(le.getMessage());*/
            	obj = JsonResFactory.buildOrg(RESCODE.ACCOUNT_LOCKED_ERROR);
            	return obj.toString();
            } catch (AuthenticationException ae) {
            	/*ae.printStackTrace();
            	System.out.println(ae.getMessage());*/
            	obj = JsonResFactory.buildOrg(RESCODE.PERMISSION_ERROR);
            	return obj.toString();
            }
		}
		else {
			obj = JsonResFactory.buildOrg(RESCODE.ALREADY_LOGIN);
		}
		log.debug("账号：" + account + " 登录结果：" + obj);
		curUser = SecurityUtils.getSubject();
		Session session = curUser.getSession(false);
//		log.debug("-----session id:" + session.getId());
//		log.debug(session.getAttributeKeys());
//		for(Object key : session.getAttributeKeys()){
//			log.debug(session.getAttribute(key));
//		}
//		session.setTimeout(60000L);
		return obj.toString();
		
	}
	
	public String logout() {
		Subject curUser = SecurityUtils.getSubject();
		if(curUser.isAuthenticated()){
			curUser.logout();
		}
		return JsonResFactory.buildOrg(RESCODE.SUCCESS).toString();
	}

	public String addAdmin(Admin admin) {
		Admin exist = adminDao.findAdminByAccount(admin.getAccount());
		if(exist != null){
			return JsonResFactory.buildOrg(RESCODE.ACCOUNT_EXIST).toString();
		}
		else {
			admin.setCreateDate(DateHandler.getCurrentDate());
			admin.setPassword(MD5.compute(admin.getPassword()));
			adminDao.save(admin);
			Admin added = this.adminDao.findAdminByAccount(admin.getAccount());
			JsonConfig config = JsonResFactory.dateConfig();
			//config.registerPropertyExclusions(Admin.class, new String[]{"password", "staff"});
			config.registerPropertyExclusions(Admin.class, new String[]{"password"});
			return JsonResFactory.buildNetWithData(RESCODE.SUCCESS, net.sf.json.JSONObject.fromObject(added, config)).toString();
		}
	}

	public String modify(Admin admin) {
		Admin exist = adminDao.findAdminById(admin.getId());
		if(exist == null){
			return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
		}
		String psw = admin.getPassword();
		exist.setComment(admin.getComment());
		exist.setName(admin.getName());
		exist.setRole(admin.getRole());
		if(psw != null && !psw.isEmpty()){
			exist.setPassword(MD5.compute(psw));
		}
		return JsonResFactory.buildOrg(RESCODE.SUCCESS).toString();
	}

	public String delete(String[] accounts) {
		String curUser = (String) SecurityUtils.getSubject().getPrincipal();
		boolean delSelf = false;
		for(String account : accounts){
			log.debug("删除账号：" + account + ", 将inventoryOrder、card、consumOrder中相关制单人设为空");
			if(StringUtils.equalsIgnoreCase(curUser, account)){
				log.debug("尝试删除当前登录账号，删除失败");
				delSelf = true;
			}
			else{
				//找到所有与admin相关的数据，将其中的admin字段设为空。
				for(InventoryOrder inventoryOrder : this.inventoryOrderDao.findByMakerAccount(account)){
					log.debug("inventoryOrder id为:" + inventoryOrder.getId() + "的订单制单人设为空");
					inventoryOrder.setOrderMaker(null);
				}
				for(Card card : this.cardDao.findByMakerAccount(account)){
					log.debug("card id为:" + card.getId() + "的制单人设为空");
					card.setOrderMaker(null);
				}
				for(ConsumOrder consumOrder : this.consumOrderDao.findByMakerAccount(account)){
					log.debug("consumOrder id为:" + consumOrder.getId() + "的制单人设为空");
					consumOrder.setOrderMaker(null);
				}
				adminDao.delete(account);
			}
		}
		
		if(delSelf){
			return JsonResFactory.buildOrg(RESCODE.CANNOT_DELETE_SELF).toString();
		}
		return JsonResFactory.buildOrg(RESCODE.SUCCESS).toString();
	}

	public String list(int page, int number) {
		int from = (page - 1) * number;
		List<Admin> list = adminDao.listAdmins(from, number);
		if(list == null || list.isEmpty()){
			return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
		}
		long realSize = adminDao.getCount();
		int size=(int) Math.ceil(realSize/(double)number);
		JsonConfig config = new JsonConfig();
		config.registerJsonValueProcessor(Date.class, new DateJsonValueProcessor());
		config.registerPropertyExclusion(Admin.class, "password");
		JSONArray jsonArray = JSONArray.fromObject(list, config);
		net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS, jsonArray);
		obj.put(Constants.RESPONSE_SIZE_KEY, size);
		obj.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
		return obj.toString();
	}
	
	public Map<String,Object> query(String account, String name, int page, int number) {
		int from = (page - 1) * number;
		List<Admin> list = adminDao.queryByNameAndAccount(account, name, from, number);
		if(list == null || list.isEmpty()){
			return RESCODE.NOT_FOUND.getJSONRES();
		}
		long count = this.adminDao.getQueryCount(account, name);
		int size = (int) Math.ceil(count/(double)number);
		return RESCODE.SUCCESS.getJSONRES(list,size,count);
	}

	public String disable(String account) {
		Admin admin = adminDao.findAdminByAccount(account);
		Subject curUser = SecurityUtils.getSubject();
		log.debug("当前登录账号：" + (String) curUser.getPrincipal() + " 尝试禁用账号：" + account);
		if(admin == null){
			return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
		}
		if(account.equals((String) curUser.getPrincipal())){
			return JsonResFactory.buildOrg(RESCODE.DISABLE_CURRENT_USER).toString();
		}
		admin.setCurrent(false);
		return JsonResFactory.buildOrg(RESCODE.SUCCESS).toString();
	}

	public String enable(String account) {
		Admin admin = adminDao.findAdminByAccount(account);
		if(admin == null){
			return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
		}
		admin.setCurrent(true);
		return JsonResFactory.buildOrg(RESCODE.SUCCESS).toString();
	}
	
	public String getAdminByAccount(String account) {
		Admin admin = this.findAdminByAccount(account);
		if(admin == null){
			return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
		}
		JsonConfig config = new JsonConfig();
		config.registerPropertyExclusion(Admin.class, "password");
		return JsonResFactory.buildNetWithData(RESCODE.SUCCESS, net.sf.json.JSONObject.fromObject(admin, config)).toString();
	}
	
	public String readRoles() {
		this.adminDao.clearRoles();
		Set<Role> roles = PermissionsList.getRoles();
		log.debug("从配置文件中读取到角色数据共：" + roles.size() + "条");
		for(Role role : roles){
			log.debug("角色名称:" + role.getRoleName() + ", 角色描述:" + role.getDescription() + ", 权限:" + role.getPermissions());
			adminDao.save(role);
		}
		return JsonResFactory.buildOrg(RESCODE.SUCCESS).toString();
	}

}
