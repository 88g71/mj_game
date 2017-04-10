package com.naqi.service;

import java.util.Map;

public interface IProcessExtension {
	/**
	 * 执行任务
	 * @param msg
	 */
	public boolean process(Map<String, Object> msg);

}
