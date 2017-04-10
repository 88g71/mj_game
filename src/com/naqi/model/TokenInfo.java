package com.naqi.model;

public class TokenInfo {
	public static final int LIFE_TIME = 1200000;
	
	private String token;
	private long date;
	private int userId;
	
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
}
