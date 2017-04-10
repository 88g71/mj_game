/*
 * File name : OnlineUser.java
 * Created on : Feb 16, 2012 7:33:34 PM
 * Creator : 韩峰
 */
package com.naqi.service;

import org.apache.mina.core.session.IoSession;

/**
 * <pre>
 * Description : 在线用户列表
 * @author 韩峰
 * </pre>
 */
public class OnlineUser {
	private int userId;
	private IoSession conn;
	private String token;
	
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public IoSession getConn() {
		return conn;
	}
	public void setConn(IoSession conn) {
		this.conn = conn;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public boolean isOnline() {
		return conn.isConnected();
	}
	
}

