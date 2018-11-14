package com.geariot.platform.freelycar.utils.query;

public class PayMethodStat {
	
	public PayMethodStat(String payMethod , float value){
		this.payMethod = payMethod;
		this.value = value;
	}
	
	private String payMethod;
	private float value;
	
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	public String getPayMethod() {
		return payMethod;
	}
	public void setPayMethod(String payMethod) {
		this.payMethod = payMethod;
	}
	
	@Override
	public String toString() {
		return "PayMethodStat [payMethod=" + payMethod + ", value=" + value + "]";
	}
	
}
