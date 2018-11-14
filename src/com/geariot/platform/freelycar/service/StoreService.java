/**
 * 
 */
package com.geariot.platform.freelycar.service;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.geariot.platform.freelycar.dao.ConsumOrderDao;
import com.geariot.platform.freelycar.dao.StoreDao;
import com.geariot.platform.freelycar.entities.ConsumOrder;
import com.geariot.platform.freelycar.entities.Store;
import com.geariot.platform.freelycar.model.RESCODE;
import com.geariot.platform.freelycar.utils.Constants;
import com.geariot.platform.freelycar.utils.JsonResFactory;

import net.sf.json.JSONArray;
import net.sf.json.JsonConfig;

/**
 * @author mxy940127
 *
 */

@Service
@Transactional
public class StoreService {

	@Autowired
	private StoreDao storeDao;

	@Autowired
	private ConsumOrderDao consumOrderDao;

	@Autowired
	private ServletContext context;

	public String addStore(Store store) {
		Store exist = storeDao.findStoreByName(store.getName());
		if (exist != null) {
			return JsonResFactory.buildOrg(RESCODE.NAME_EXIST).toString();
		}
		store.setCreateDate(new Date());
		storeDao.save(store);
		JsonConfig config = JsonResFactory.dateConfig();
		return JsonResFactory.buildNetWithData(RESCODE.SUCCESS, net.sf.json.JSONObject.fromObject(store, config))
				.toString();
	}

	public String modifyStore(Store store) {
		Store exist = storeDao.findStoreById(store.getId());
		if (exist == null) {
			return JsonResFactory.buildOrg(RESCODE.NOT_FOUND).toString();
		} 
		storeDao.update(store);
		JsonConfig config = JsonResFactory.dateConfig();
		return JsonResFactory.buildNetWithData(RESCODE.SUCCESS, net.sf.json.JSONObject.fromObject(store, config)).toString();
	}

	public String delStore(Integer... storeIds) {
		int count = 0;
		for (int storeId : storeIds) {
			Store exist = storeDao.findStoreById(storeId);
			if (exist == null) {
				count++;
			} else {
				storeDao.delete(storeId);
			}
		}
		if (count != 0) {
			String tips = "共" + count + "条未在数据库中存在记录";
			net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.PART_SUCCESS, tips);
			long realSize = storeDao.getCount();
			obj.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
			return obj.toString();
		} else {
			JSONObject obj = JsonResFactory.buildOrg(RESCODE.SUCCESS);
			long realSize = storeDao.getCount();
			obj.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
			return obj.toString();
		}
	}

	public Map<String,Object> query(String name, int page, int number) {
		int from = (page - 1) * number;
		List<Store> list = storeDao.query(name, from, number);
		if (list == null || list.isEmpty()) {
			return RESCODE.NO_RECORD.getJSONRES();
		}
		long count = (long) storeDao.getQueryCount(name);
		int size = (int) Math.ceil(count / (double) number);
		return RESCODE.SUCCESS.getJSONRES(list,size,count);
	}

	public String detail(int storeId) {
		Store exist = storeDao.findStoreById(storeId);
		if (exist == null) {
			return JsonResFactory.buildOrg(RESCODE.NO_RECORD).toString();
		} else {
			JsonConfig config = JsonResFactory.dateConfig();
			return JsonResFactory.buildNetWithData(RESCODE.SUCCESS, net.sf.json.JSONObject.fromObject(exist, config))
					.toString();
		}
	}


	public String evaluation(int storeId, int page, int number) {
		Store exist = storeDao.findStoreById(storeId);
		if (exist == null) {
			return JsonResFactory.buildOrg(RESCODE.NO_RECORD).toString();
		} else {
			int from = (page - 1) * number;
			List<ConsumOrder> consumOrders = this.consumOrderDao.findByStoreId(storeId, from, number);
			if (consumOrders.isEmpty() || consumOrders == null) {
				return JsonResFactory.buildOrg(RESCODE.NO_RECORD).toString();
			} else {
				long realSize = consumOrders.size();
				float average = this.consumOrderDao.storeAverage(storeId);
				int size = (int) Math.ceil(realSize / (double) number);
				JsonConfig config = JsonResFactory.dateConfig();
				JSONArray jsonArray = JSONArray.fromObject(consumOrders, config);
				net.sf.json.JSONObject obj = JsonResFactory.buildNetWithData(RESCODE.SUCCESS, jsonArray);
				obj.put(Constants.RESPONSE_SIZE_KEY, size);
				obj.put(Constants.RESPONSE_REAL_SIZE_KEY, realSize);
				obj.put("average",average);
				return obj.toString();
			}
		}
	}

	public String addStorePicture(MultipartFile file) {
		String baseUrl = "";
		File folder = new File(context.getRealPath("") + "\\store" + "\\");
		if (folder.exists() && folder.isDirectory()) {
			baseUrl = folder + "\\";
		} else {
			folder.mkdirs();
			baseUrl = folder + "\\";
		}
		// 获取上传路径baseUrl
		if (file != null) {
			String fileName = file.getOriginalFilename();
			if (!"".equalsIgnoreCase(fileName)) {
				StringBuilder sb = new StringBuilder();
				Long currentTimeMillis = System.currentTimeMillis();
				String ctm = currentTimeMillis.toString();
				// 截取后六位
				ctm = ctm.substring(ctm.length() - 6, ctm.length());
				sb.append(ctm);
				for (int i = 0; i < 3; i++) {
					sb.append((int) (Math.random() * 10));
				}
				// 获取后缀
				String suffix = fileName.substring(fileName.indexOf("."), fileName.length());
				sb.append(suffix);
				try {
					file.transferTo(new File(baseUrl + sb));
				} catch (Exception e) {
					return JsonResFactory.buildOrg(RESCODE.UNKNOWN_ERROR).toString();
				} 
				//Map<String, String> data = new HashMap<>();
				/*data.put("data", "store\\"+sb);
				JSONArray array = new JSONArray();
				array.add(data);
				return JsonResFactory.buildNetWithData(RESCODE.SUCCESS, array).toString();*/
				return sb.toString();
			}
			else{
				return JsonResFactory.buildOrg(RESCODE.UNKNOWN_ERROR).toString();
			}
		} else {
			return JsonResFactory.buildOrg(RESCODE.UNKNOWN_ERROR).toString();
		}
	}
	
}
