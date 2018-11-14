package com.geariot.platform.freelycar.utils.query;

public class MonthStat {
	
	public MonthStat(float income, float expend, String payDate) {
		this.income = income;
		this.expend = expend;
		this.payDate = payDate;
	}
	private float income;
	private float expend;
	private String payDate;
	public float getExpend() {
		return expend;
	}
	public float getIncome() {
		return income;
	}
	public String getPayDate() {
		return payDate;
	}
	public void setExpend(float expend) {
		this.expend = expend;
	}
	public void setIncome(float income) {
		this.income = income;
	}
	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}
	
	@Override
	public String toString() {
		return "MonthStat [income=" + income + ", expend=" + expend + ", payDate=" + payDate + "]";
	}
	
	
}
