package com.naqi.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

public class NetUtil {
	private static Logger logger = LoggerFactory.getLogger(NetUtil.class);
	
	public static void sendMsg(IoSession session,int userId,String cmd){
		String param = getSendParma(cmd );
		logger.debug("返回消息  用户id:{},消息号:{} ,消息:{}",new String[]{userId+"", param , cmd});
		session.write(param);
	}

	public static void sendMsg(IoSession session,int userId,String cmd ,Object data){
		String param = getSendParma(cmd, data);
		logger.debug("返回消息  用户id:{},消息号:{} ,消息:{}",new String[]{userId+"", param , cmd});
		session.write(param);
	}
	
	/**
	 * 包装返回参数
	 * @param errcode
	 * @param errmsg
	 * @return
	 */
	public static String getSendParma(String cmd) {
		Map<String,Object> retMap = new HashMap<String,Object>();
		return getSendParma(cmd, retMap);
	}
	
	/**
	 * 包装返回参数
	 * @param errcode
	 * @param errmsg
	 * @param retMap
	 * @return
	 */
	public static String getSendParma(String cmd ,Object data) {
		Map<String,Object> retMap = new HashMap<String,Object>();
		retMap.put("data", data);
		return getSendParma(cmd, retMap);
	}
	/**
	 * 包装返回参数
	 * @param errcode
	 * @param errmsg
	 * @param retMap
	 * @return
	 */
	public static String getSendParma(String cmd ,Map<String,Object> retMap) {
		retMap.put("cmd", cmd);
		return JSON.toJSONString(retMap);
	}
}
