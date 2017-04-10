/**********************************************************************
 *
 * $RCSfile$  $Revision$  $Date$
 *
 * $Log:$
 *
 *
 *********************************************************************/
/*
 * Copyright 2011 韩峰. All rights reserved.
 * File name : Application.java
 * Created on : 2011-12-19 下午02:20:20
 * Creator : 韩峰
 */
package com.naqi.conn;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.naqi.service.IProcessService;
import com.naqi.system.ISystemExtension;
import com.naqi.system.impl.SystemServletContextListener;

/**
 * <pre>
 * Description : TODO
 * &#064;author 韩峰
 * </pre>
 */
public class Application extends IoHandlerAdapter implements ISystemExtension{
	
	private static Logger logger = LoggerFactory.getLogger(Application.class);
	@Resource
	private IProcessService processService;
	
	public void appStart() {
	}

	public void messageReceived(IoSession session, Object message) throws Exception {
		if(!SystemServletContextListener.isStartUp) return;
		
		System.out.println(message);
		if("game_ping".equals(message)){
			Map<String , String> gamePong = new HashMap<String, String>();
			gamePong.put("cmd", "game_pong");
			session.write(JSON.toJSONString(gamePong));
		}else{
			String msg = message.toString();
			@SuppressWarnings("unchecked")
			Map<String, Object> map = JSON.parseObject(msg, Map.class);
			processService.process(map, session);
		}
	}

	@Override
	public void sessionCreated(IoSession conn) throws Exception {
		super.sessionCreated(conn);
	}

	@Override
	public void sessionOpened(IoSession conn) throws Exception {
		logger.debug(" 连接打开 {} ", conn.getRemoteAddress());
		super.sessionOpened(conn);
	}

	public void sessionClosed(IoSession conn) throws Exception {
		logger.debug(" 连接关闭 {}",conn.getAttribute("RemoteAddress"));
	}

	@Override
	public void onShutDown() {
		logger.info("Mina 框架开始关闭...");
	}

	@Override
	public void onStartUp() {
	}
}
