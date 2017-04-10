package com.naqi.mj.service.dq.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.naqi.mj.model.GameSeat;
import com.naqi.mj.model.TingPai;
import com.naqi.mj.service.dq.ITingExtension;
@Service(value = "Hu7Pairs")
public class Hu7Pairs implements ITingExtension{

	public boolean check(GameSeat gameSeat ,int pai){
		Map<Integer,Integer> countMap = gameSeat.getCountMap();
		if(gameSeat.getHolds().size() == 13){
	        int danPai = -1;
	        //对子数量
	        int pairCount = 0;
	        //四个数量
	        int fourCount = 0;
	        
	        int score = 10;
	        String pattern =  "7pairs";
	        
	        for(int k : countMap.keySet()){
	            int c = countMap.get(k);
	            if( c == 2 || c == 3){
	                pairCount++;
	            }
	            else if(c == 4){
	                pairCount += 2;
	                fourCount ++;
	            }

	            if(c == 1 || c == 3){
	                //如果已经有单牌了，表示不止一张单牌，并没有下叫。直接闪
	                if(danPai >= 0){
	                    break;
	                }
	                danPai = k;
	                if(c == 3){
	                	if(fourCount == 1){
	                		pattern = "7pairs1";
	                		score = 20;
	                	}else if(fourCount >= 2){
	                		pattern = "7pairs2";
	                		score = 40;
	                	}
	                }
	            }
	        }

	        //检查是否有6对 并且单牌是不是目标牌
	        if(pairCount == 6 && danPai == pai){
	        	TingPai tingPai = gameSeat.getTingPai();
	        	tingPai.setPai(pai);
	        	tingPai.setPattern(pattern);
	        	tingPai.setScore(score);
	        	tingPai.setTing(true);
	        	return true;
	        }
	    }
		return false;
	}
}
