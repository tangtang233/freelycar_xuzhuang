package com.geariot.platform.freelycar.utils;

import java.util.Set;

import com.geariot.platform.freelycar.entities.ProjectInfo;

public class ConsumOrderPayParamWrapper {
	private String consumOrdersId;
	private String payMethod;         //默认支付方式
	private String payMethod1;			//支付方式1
	private float actualPrice;			//默认支付价格
	private float actualPrice1;			//支付价格1
	private Set<ProjectInfo> projectInfos;
	private boolean pay;
	public String getConsumOrdersId() {
		return consumOrdersId;
	}
	public void setConsumOrdersId(String consumOrdersId) {
		this.consumOrdersId = consumOrdersId;
	}
	public String getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}
	public Set<ProjectInfo> getProjectInfos() {
		return projectInfos;
	}
	public void setProjectInfos(Set<ProjectInfo> projectInfos) {
		this.projectInfos = projectInfos;
	}
	public boolean isPay() {
		return pay;
	}
	public void setPay(boolean pay) {
		this.pay = pay;
	}
	public String getPayMethod1() {
		return payMethod1;
	}
	public void setPayMethod1(String payMethod1) {
		this.payMethod1 = payMethod1;
	}
	public float getActualPrice() {
		return actualPrice;
	}
	public void setActualPrice(float actualPrice) {
		this.actualPrice = actualPrice;
	}
	public float getActualPrice1() {
		return actualPrice1;
	}
	public void setActualPrice1(float actualPrice1) {
		this.actualPrice1 = actualPrice1;
	}
	@Override
	public String toString() {
		return "ConsumOrderPayParamWrapper [consumOrdersId=" + consumOrdersId + ", payMethod=" + payMethod
				+ ", payMethod1=" + payMethod1 + ", actualPrice=" + actualPrice + ", actualPrice1=" + actualPrice1
				+ ", projectInfos=" + projectInfos + ", pay=" + pay + "]";
	}
	
}
