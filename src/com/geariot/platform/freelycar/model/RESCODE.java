package com.geariot.platform.freelycar.model;


import com.geariot.platform.freelycar.exception.ForRollbackException;
import com.geariot.platform.freelycar.utils.Constants;

import java.util.HashMap;
import java.util.Map;

public enum RESCODE {

	SUCCESS(0, "成功"), 
	WRONG_PARAM(1, "参数错误"), 
	NOT_FOUND(2, "无该条记录"),
	UPDATE_ERROR(3, "更新数据错误"), 
	CREATE_ERROR(4, "存储数据错误"), 
	DATE_FORMAT_ERROR(5, "日期格式错误"),
	DELETE_ERROR(6, "删除错误"), 
	DUPLICATED_ERROR(7,"重复数据"),
	FILE_ERROR(8, "上传文件错误"),
	ACCOUNT_ERROR(9, "账号不存在"), 
	PSW_ERROR(10, "密码错误"), 
	ACCOUNT_LOCKED_ERROR(11, "账号已被锁定"), 
	PERMISSION_ERROR(12, "没有此权限"), 
	ALREADY_LOGIN(13, "已经登录"), 
	ACCOUNT_EXIST(14, "该账号已存在"),
	CANNOT_DELETE_SELF(15, "无法删除当前登录账户"),
	PHONE_EXIST(16, "手机号码已存在"),
	CAR_LICENSE_EXIST(17, "车牌号码已存在"),
	PART_SUCCESS(18, "批处理部分成功"),
	NAME_EXIST(19, "名称已存在"),
	WORK_NOT_FINISH(20, "当前订单还未完工"),
	NOT_SET_PAY_CARD(21, "未设置项目付款卡"),
	CARD_REMAINING_NOT_ENOUGH(22, "所选会员卡对应项目剩余次数不足"),
	INVENTORY_NOT_ENOUGH(23, "库存不足"),
	DISABLE_CURRENT_USER(24, "无法禁用当前登录账户"),
	UNABLE_TO_DELETE(25, "所选项目被其他引用，无法删除"),
	NO_INCOME(26, "无对应日期订单记录"),
	UNSUPPORT_TYPE(27, "不支持该操作"),
	NO_RECORD(28,"数据库无记录"),
	CANNOT_CANCEL_INVOICES(29,"配件库存不足以退货"),
	CARDNUMBER_EXIST(30,"会员卡号已存在"),
	NOT_SET_PAY_TICKET(31, "未设置项目付款券"),
	TICKET_REMAINING_NOT_ENOUGH(32, "所选会员卡对应项目剩余次数不足"),
	STORE_NAME_CAN_NOT_BE_EMPTY(33, "店名不能为空"),
	UNKNOWN_ERROR(34,"未知错误"),
	NO_CARD(35,"该账户没有办理会员卡"),
	NOT_SET_PAYMETHOD(36,"没有设置订单支付方式"),
	CARD_BALANCE_NOT_ENOUGH(37,"储值卡余额不足"),
	TEMPLE_ORDER_SUCCESS(38,"消费订单挂单成功"),
	FILE_TPYE_ERROR(39,"上传文件格式错误"),
	FOR_EXCEPTION,
	REMOTE_OPERATION_FAILURE(101,"调用远程操作失败")
	;
	
	// 定义私有变量
	private int nCode;

	private String nMsg;

	// 构造函数，枚举类型只能为私有
	private RESCODE(int _nCode, String _nMsg) {

		this.nCode = _nCode;
		this.nMsg = _nMsg;
	}
	
	private RESCODE(){
	}

	public String getMsg() {

		return nMsg;
	}

	public int getValue() {

		return nCode;
	}
	
	@Override
	public String toString() {
		return String.valueOf(this.nCode);
	}
	
	

	/**
	 * 最新的返回json
	 */
	public Map<String,Object> getJSONRES(){
		Map<String,Object> map = new HashMap<>();
		map.put(Constants.RESPONSE_CODE_KEY, this.nCode);
		map.put(Constants.RESPONSE_MSG_KEY, this.nMsg);
		return map;
	}
	
	
	public Map<String,Object> getJSONRES(Object entity){
		Map<String, Object> jsonres = getJSONRES();
		jsonres.put(Constants.RESPONSE_DATA_KEY, entity);
		return jsonres;
	}
	
	public Map<String,Object> getJSONRES(Object entity,int pages,long count){
		Map<String, Object> jsonres = getJSONRES();
		jsonres.put(Constants.RESPONSE_DATA_KEY, entity);
		jsonres.put(Constants.RESPONSE_SIZE_KEY, pages);
		jsonres.put(Constants.RESPONSE_REAL_SIZE_KEY, count);
		return jsonres;
	}
	
	public Map<String,Object> getJSONRES(ForRollbackException e){
		Map<String,Object> map = new HashMap<>();
		map.put(Constants.RESPONSE_CODE_KEY, e.getErrorCode());
		map.put(Constants.RESPONSE_MSG_KEY, e.getMessage());
		return map;
	}
	
}
