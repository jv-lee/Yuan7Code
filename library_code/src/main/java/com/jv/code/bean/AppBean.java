package com.jv.code.bean;

public class AppBean {
	
	private int noid;
	private String id;
	private String packageName;
	private String sendRecordId;
	
	public AppBean(){}
	
	public AppBean(String id, String packageName, String sendRecordId) {
		super();
		this.id = id;
		this.packageName = packageName;
		this.sendRecordId = sendRecordId;
	}
	
	

	public int getNoid() {
		return noid;
	}

	public void setNoid(int noid) {
		this.noid = noid;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getSendRecordId() {
		return sendRecordId;
	}
	public void setSendRecordId(String sendRecordId) {
		this.sendRecordId = sendRecordId;
	}
	
	

}
