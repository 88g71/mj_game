package com.naqi.service;

import java.util.Map;

import org.apache.mina.core.session.IoSession;

public interface IProcessService {
	/**
	 * 堆加队列任务
	 * @param map
	 * @param session
	 */
	public void process(Map<String,Object> map ,IoSession session);
	/**
	 * 初始化
	 */
	public void init();
}
