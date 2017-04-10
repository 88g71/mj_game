package com.naqi.util;

import java.util.UUID;

import org.apache.log4j.Logger;

public class UUIDGenerator {
	private static Logger logger = Logger.getLogger(UUIDGenerator.class);
	/**
	 * 生成全球唯一ID（32位字符串）
	 * @return
	 */
	public static String generatorUUID() {
		return UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
	}
	
	/**
	 * 生成全球唯一ID（带前缀的）
	 * @param before 字符串前缀
	 * @return
	 */
	public static String generatorUUID(String before) {
		return before + generatorUUID();
	}
	
	public static void main(String[] args) {
		long a = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++)
			logger.debug(generatorUUID("20110926"));
		logger.debug((System.currentTimeMillis() - a));
	}
	
}
