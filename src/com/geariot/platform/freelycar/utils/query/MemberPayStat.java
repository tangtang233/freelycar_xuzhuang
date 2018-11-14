package com.geariot.platform.freelycar.utils.query;

public class MemberPayStat {
	
	private Boolean member;
	private float amount;
	
	public MemberPayStat(Boolean member , float amount){
		this.member = member;
		this.amount = amount;
	}

	public Boolean getMember() {
		return member;
	}

	public void setMember(Boolean member) {
		this.member = member;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}
	
	
}
