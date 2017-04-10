package com.naqi.service;

public interface IConfigService {
	/**
	 * 获取版本号
	 * @return
	 */
	public String getVersion();
	/**
	 * 获取中心服务器http地址
	 * @return
	 */
	public String getAppWeb();
	/**
	 * 加密字节
	 * @return
	 */
	public String getAccountPriKey();
	/**
	 * 获取系统参数    返回数字
	 * @param paramKey
	 * @return
	 */
	public int getIntSystemParam(String paramKey);
	/**
	 * 刷新系统参数
	 */
	public void refreshSystemParam();
	
	public String getRoomPriKey() ;
}
