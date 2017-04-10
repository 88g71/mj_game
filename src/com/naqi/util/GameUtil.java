package com.naqi.util;

import java.util.Random;

public class GameUtil {
	/**
	 * 获取随机数
	 * @param min
	 * @param max
	 * @return
	 */
	public static int getRandomInt(int min , int max){
		int randomInt = 0;
		Random rd = new Random();
		randomInt = rd.nextInt(max-min)+min;
		return randomInt;
	}
	
}
