package com.naqi.util;

import javax.servlet.ServletContext;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.naqi.service.IProcessService;
import com.naqi.service.IRoomService;
import com.naqi.service.IUserService;

public class GBeanFactory {
	
	private static ServletContext servletContext;
	private static WebApplicationContext webApplictionContext;
	
	public static Object getBean(String name){
		return webApplictionContext.getBean(name);
	}
	
	public static ServletContext getServletContext() {
		return servletContext;
	}

	public static void setServletContext(ServletContext servletContext) {
		GBeanFactory.servletContext = servletContext;
		GBeanFactory.webApplictionContext = WebApplicationContextUtils.getWebApplicationContext(servletContext);
	}
	//用户服务
	public static IUserService getUserService(){
		return (IUserService) getBean("userService");
	}

	public static IRoomService getRoomService() {
		return (IRoomService) getBean("roomService");
	}

	public static IProcessService getProcessService() {
		return (IProcessService) getBean("processService");
	}
	
}
