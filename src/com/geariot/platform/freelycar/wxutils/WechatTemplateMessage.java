package com.geariot.platform.freelycar.wxutils;

import com.geariot.platform.freelycar.entities.*;
import org.apache.http.entity.StringEntity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WechatTemplateMessage {
	
	private static final String PAY_SUCCESS_TEMPLATE_ID = "F5CyYJ5u9_1wRcapK1ECOYRrjxcLcL3rjB0xUg_VQn0";
	private static final String PAY_FAIL_ID = "o0TOjg7KkxoL4CtQ91j--TVVmxYDSNk-dLWqoUVd8mw";
	private static final String CONSUMORDER_STATE_UPDATE = "Au2k3CSXdYZu7ujvagXT6GxTzjDGUmQTkI8xutL30Fc";
	private static final String INSURANCE_REMIND = "yHeGeF-aI1EmUHtyvEMT_imdLA2ByFhCoyiz49EaZG0";
	private static final String ANNUAL_CHECK_REMIND = "obv2EnhQao_pRW9cWZQ9m261SK3rcNCSqeHLeJsu5Q0";
	
//	private static final Logger log = Logger.getLogger(WechatTemplateMessage.class);
	private static final Logger log = LogManager.getLogger(WechatTemplateMessage.class);
			
	private static final String PAY_ERROR_DATABASE_FAIL = "服务异常";
	
	private static final String ASK_FOR_INSURANCE = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wx89ac1808e298928d&redirect_uri=http%3a%2f%2fwww.freelycar.com%2ffreelycar_wechat%2fapi%2fuser%2fmenuRedirect%3fhtmlPage%3dinquiry&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
	
	private static String invokeTemplateMessage(JSONObject params){
		StringEntity entity = new StringEntity(params.toString(),"utf-8"); //解决中文乱码问题   
		String result = HttpRequest.postCall(WechatConfig.WECHAT_TEMPLATE_MESSAGE_URL + 
				WechatConfig.getAccessTokenForInteface().getString("access_token"),
				entity, null);
		log.debug("微信模版消息结果：" + result);
		return result;
	}
	


//{{first.DATA}}
//类型：{{keyword1.DATA}}
//金额：{{keyword2.DATA}}
//状态：{{keyword3.DATA}}
//时间：{{keyword4.DATA}}
//备注：{{keyword5.DATA}}
//{{remark.DATA}}
	public static void paySuccess(WXPayOrder wxPayOrder){
		log.debug("准备支付成功模版消息。。。");
		SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		JSONObject params = new JSONObject();
		JSONObject data = new JSONObject();
		params.put("touser", wxPayOrder.getOpenId());
		params.put("template_id", PAY_SUCCESS_TEMPLATE_ID);
//		params.put("url", "http://www.geariot.com/fitness/class.html");
		data.put("first", keywordFactory("支付成功", "#173177"));
		data.put("keyword1", keywordFactory(wxPayOrder.getProductName(), "#173177"));
		data.put("keyword2", keywordFactory((float)(Math.round(wxPayOrder.getTotalPrice()*100))/100 + "元", "#173177"));
		data.put("keyword3", keywordFactory("成功", "#173177"));
		data.put("keyword4", keywordFactory(df.format(wxPayOrder.getFinishDate()), "#173177"));
		data.put("keyword5", keywordFactory(""));
		data.put("remark", keywordFactory(""));
		params.put("data", data);
		String result = invokeTemplateMessage(params);
		log.debug("微信支付成功模版消息结果：" + result);
//		return result;
	}
	public static void paySuccess(ConsumOrder consumOrder,String openId){
		log.debug("准备支付成功模版消息。。。");
		SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
		JSONObject params = new JSONObject();
		JSONObject data = new JSONObject();
		params.put("touser", openId);
		params.put("template_id", PAY_SUCCESS_TEMPLATE_ID);
//		params.put("url", "http://www.geariot.com/fitness/class.html");
		data.put("first", keywordFactory("支付成功", "#173177"));
		data.put("keyword1", keywordFactory(getConsumOrderProductName(consumOrder), "#173177"));
		data.put("keyword2", keywordFactory((float)(Math.round(consumOrder.getTotalPrice()*100))/100 + "元", "#173177"));
		data.put("keyword3", keywordFactory("成功", "#173177"));
		data.put("keyword4", keywordFactory(df.format(consumOrder.getFinishTime()), "#173177"));
		data.put("keyword5", keywordFactory(""));
		data.put("remark", keywordFactory(""));
		params.put("data", data);
		String result = invokeTemplateMessage(params);
		log.debug("微信支付成功模版消息结果：" + result);
//		return result;
	}
	
	
//	{{first.DATA}}
//	支付金额：{{keyword1.DATA}}
//	商品信息：{{keyword2.DATA}}
//	失败原因：{{keyword3.DATA}}
//	{{remark.DATA}}
	public static void errorCancel(WXPayOrder wxPayOrder){
		log.debug("支付成功，数据库更新失败！");
		JSONObject params = new JSONObject();
		JSONObject data = new JSONObject();
		params.put("touser", wxPayOrder.getId());
		params.put("template_id", PAY_FAIL_ID);
		
		data.put("first", keywordFactory("支付失败", "#173177"));
		data.put("keyword1", keywordFactory((float)(Math.round(wxPayOrder.getTotalPrice()*100))/100 + "元", "#173177"));
		data.put("keyword2", keywordFactory(wxPayOrder.getProductName(), "#173177"));
		data.put("keyword3", keywordFactory("服务异常", "#173177"));
		data.put("remark", keywordFactory("请妥善保存单号，联系客服人员"));
		params.put("data", data);
		String result = invokeTemplateMessage(params);
		log.debug("微信支付失败结果：" + result);
	}
	public static void errorWXCancel(ConsumOrder consumOrder,String openId){
		log.debug("支付成功，数据库更新失败！");
		JSONObject params = new JSONObject();
		JSONObject data = new JSONObject();
		params.put("touser", openId);
		params.put("template_id", PAY_FAIL_ID);
		data.put("first", keywordFactory("支付失败", "#173177"));
		data.put("keyword1", keywordFactory((float)(Math.round(consumOrder.getTotalPrice()*100))/100 + "元", "#173177"));
		data.put("keyword2", keywordFactory(getConsumOrderProductName(consumOrder), "#173177"));
		data.put("keyword3", keywordFactory("服务异常", "#173177"));
		data.put("remark", keywordFactory("请妥善保存单号，联系客服人员"));
		params.put("data", data);
		String result = invokeTemplateMessage(params);
		log.debug("微信支付失败结果：" + result);
	}
	
	public static void insuranceRemind(Car car, String openId, WXUser wxUser){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		log.debug("车辆保险即将到期,推送至微信用户:"+openId);
		JSONObject params = new JSONObject();
		JSONObject data = new JSONObject();
		params.put("touser", openId);
		params.put("template_id", INSURANCE_REMIND);
		params.put("url", ASK_FOR_INSURANCE);
		data.put("first", keywordFactory("尊敬的"+wxUser.getName()+"您好", "#173177"));
		data.put("keyword1",keywordFactory(car.getLicensePlate(),"#173177"));
		data.put("keyword2",keywordFactory("商业险","#173177"));
		data.put("keyword3",keywordFactory(car.getInsuranceCompany()==null?"未提供投保公司":car.getInsuranceCompany(),"#173177"));
		data.put("keyword4",keywordFactory(sdf.format(car.getInsuranceEndtime()),"#173177"));
		data.put("remark", keywordFactory("点击立即获取最新续保信息"));
		params.put("data", data);
		String result = invokeTemplateMessage(params);
		log.debug("推送状态结果：" + result);
	}
	
	public static void annualCheckRemind(String licensePlate, String openId, WXUser wxUser, Date licenseDate){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		log.debug("车辆即将年检,推送至微信用户:"+openId);
		JSONObject params = new JSONObject();
		JSONObject data = new JSONObject();
		params.put("touser", openId);
		params.put("template_id", ANNUAL_CHECK_REMIND);
		data.put("first", keywordFactory("尊敬的"+wxUser.getName()+"您好,您的车辆年检即将到期。", "#173177"));
		data.put("keyword1",keywordFactory(licensePlate,"#173177"));
		data.put("keyword2",keywordFactory(sdf.format(licenseDate),"#173177"));
		data.put("keyword3",keywordFactory("未知","#173177"));
		data.put("remark", keywordFactory("在办理年检之前，请确认无任何未处理违法记录。"));
		params.put("data", data);
		String result = invokeTemplateMessage(params);
		log.debug("推送状态结果：" + result);
	}
	
	public static void consumOrderChange(ConsumOrder consumOrder, String openId, String parkingLocation){
		String str = "";
		switch(consumOrder.getState()){
			case 1 :
				break;
			case 2 :
				str = "您的爱车"+consumOrder.getLicensePlate()+"已完工,您的爱车停放在"+parkingLocation+"。";
				break;
			case 3 :
				str = "您的爱车"+consumOrder.getLicensePlate()+"已交车,您的爱车停放在"+parkingLocation+"。";
			default :
				break;
		}
		log.debug("订单状态更新,推送至微信用户:"+openId);
		JSONObject params = new JSONObject();
		JSONObject data = new JSONObject();
		params.put("touser", openId);
		params.put("template_id", CONSUMORDER_STATE_UPDATE);
		data.put("first", keywordFactory("订单状态更新", "#173177"));
		data.put("OrderSn",keywordFactory(consumOrder.getId(),"#173177"));
		data.put("OrderStatus",keywordFactory(str,"#173177"));
		data.put("remark", keywordFactory("小易爱车竭诚为您服务"));
		params.put("data", data);
		String result = invokeTemplateMessage(params);
		log.debug("订单状态更新结果：" + result);
	}
	
	private static String getConsumOrderProductName(ConsumOrder consumOrder){
		String productName = "";
		for(ProjectInfo projectInfo : consumOrder.getProjects())
			productName+=projectInfo.getName();
		return productName;
	}
	
	private static JSONObject keywordFactory(String value){
		JSONObject keyword = new JSONObject();
		keyword.put("value", value);
		return keyword;
	}
	
	private static JSONObject keywordFactory(String value, String color){
		JSONObject keyword = keywordFactory(value);
		keyword.put("color", color);
		return keyword;
	}
	
}
