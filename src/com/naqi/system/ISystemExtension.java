/*
 * File name : ISystemExtension.java
 * Created on : Jun 14, 2012 1:03:43 PM
 * Creator : 韩峰
 */
package com.naqi.system;

/**
 * <pre>
 * Description : 
 * @author 韩峰
 * </pre>
 */
public interface ISystemExtension {
	/**
	 * 系统关机
	 */
	public void onShutDown();
	/**
	 * 系统启动
	 */
	public void onStartUp();
}

