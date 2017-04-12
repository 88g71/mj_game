package com.naqi.test;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PeiPai {
	private static Logger logger = LoggerFactory.getLogger(PeiPai.class);
	public static void pei(List<Integer> mahjongs) {
		logger.debug("配牌----------》"+mahjongs.size() +"," + mahjongs);
		List<Integer> temp = new ArrayList<>();
		temp.addAll(mahjongs);
		mahjongs.clear();
		int[] a = new int[]{0,0,0,0,2,3,4,5,5,5,6,7,8}; 
		int[] b = new int[]{9,9,9,9,10,11,12,13,13,13,14,15,16}; 
		int[] c = new int[]{17,17,17,18,19,20,21,21,21,22,23,24,25}; 
		int[] d = new int[]{1,2,3,3,4,4,6,8,10,11,23,24,25};
		for (int i = 0; i < 13; i++) {
			temp.remove(temp.indexOf(d[i]));
			mahjongs.add(0,d[i]);
			temp.remove(temp.indexOf(c[i]));
			mahjongs.add(0,c[i]);
			temp.remove(temp.indexOf(b[i]));
			mahjongs.add(0,b[i]);
			temp.remove(temp.indexOf(a[i]));
			mahjongs.add(0,a[i]);
		}
		mahjongs.addAll(temp);
		logger.debug("配牌----------》"+mahjongs.size() +"," + mahjongs);
		
	}
	
}
