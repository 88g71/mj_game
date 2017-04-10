package com.http.controller;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class ControllerUtils {
	/**
	 * 包装返回参数
	 * @param errcode
	 * @param errmsg
	 * @return
	 */
	public static String getSendParma(int errcode, String errmsg) {
		Map<String,Object> retMap = new HashMap<String,Object>();
		retMap.put("errcode", errcode);
		retMap.put("errmsg", errmsg);	
		return JSON.toJSONString(retMap);
	}
	/**
	 * 包装返回参数
	 * @param errcode
	 * @param errmsg
	 * @param retMap
	 * @return
	 */
	public static String getSendParma(int errcode, String errmsg ,Map<String,Object> retMap) {
		retMap.put("errcode", errcode);
		retMap.put("errmsg", errmsg);	
		return JSON.toJSONString(retMap);
	}

}
