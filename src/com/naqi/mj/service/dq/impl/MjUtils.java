package com.naqi.mj.service.dq.impl;

public class MjUtils {
	/**
	 * 获取牌类型
	 * @param pai
	 * @return
	 */
	public static int getMJType(int pai){
	    if(pai >= 0 && pai < 9){
	        //筒
	        return 0;
	    }
	    else if(pai >= 9 && pai < 18){
	        //条
	        return 1;
	    }
	    else if(pai >= 18 && pai < 27){
	        //万
	        return 2;
	    }
	    //中发白27 28 29
	    return 3;
	}	
}
