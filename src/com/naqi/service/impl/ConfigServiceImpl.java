package com.naqi.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.naqi.mapper.SystemParamMapper;
import com.naqi.model.SystemParam;
import com.naqi.service.IConfigService;
@Service
public class ConfigServiceImpl implements IConfigService {

	private Map<String, SystemParam> systemParamMap = new ConcurrentHashMap<String,SystemParam>();
	@Resource
	private SystemParamMapper systemParamMapper;
	
	@Override
	public String getVersion() {
		return "20170313";
	}

	@Override
	public String getAppWeb() {
		return "https://fir.im/";
	}

	@Override
	public String getAccountPriKey() {
		return "^&*#$%()@";
	}

	@Override
	public int getIntSystemParam(String paramKey) {
		SystemParam param = systemParamMap.get(paramKey);
		String value = param.getValue();
		int valueInt = value == null ? 0 : Integer.parseInt(value);
		return valueInt;
	}
	@PostConstruct
	public void initSystemParam(){
		refreshSystemParam();
	}
	/**
	 * 刷新系统参数
	 */
	public void refreshSystemParam() {
		Map<String, SystemParam> newMap = new ConcurrentHashMap<String,SystemParam>();
		List<SystemParam> list = systemParamMapper.findAll();
		if(list != null && list.size() > 0){
			for (SystemParam systemParam : list) {
				newMap.put(systemParam.getParamKey(), systemParam);
			}
		}
		systemParamMap = newMap;
	}
	@Override
	public String getRoomPriKey() {
		// ROOM_PRI_KEY = "~!@#$(*&^%$&";
		return "~!@#$(*&^%$&";
	}
}
