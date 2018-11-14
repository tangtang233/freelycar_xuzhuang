package com.geariot.platform.freelycar.utils.query;

import java.math.BigInteger;

public class ProgramPayStat {

	private String programName;
	private double value;
	private BigInteger count;
	
	public ProgramPayStat(){
		
	}
	
	public ProgramPayStat(double value , String programName , BigInteger count){
		this.value = value;
		this.programName = programName ;
		this.count = count;
	}

	public BigInteger getCount() {
		return count;
	}

	public void setCount(BigInteger count) {
		this.count = count;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public String getprogramName() {
		return programName;
	}

	public void seprogramName(String programName) {
		this.programName = programName;
	}
}
