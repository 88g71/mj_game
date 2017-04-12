package com.naqi.mj.service.dq.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.naqi.mj.model.GameSeat;
import com.naqi.mj.model.TingPai;
import com.naqi.mj.service.dq.ITingExtension;
@Service(value ="DuiDuiHu")
public class DuiDuiHu implements ITingExtension{
	private static Logger logger = LoggerFactory.getLogger(DuiDuiHu.class);
	private static final int SCORE = 6;
	@Override
	public boolean check(GameSeat gameSeat,int pai) {
		logger.info("duiduihu  用户名{}   牌 {}",gameSeat.getTableSeat().getUserName() , gameSeat.getHoldsPai());
		Map<Integer,Integer> countMap = gameSeat.getCountMap();
	    

	    //检查是否是对对胡  推到胡没有吃所以只需要检查手上的牌
	    //对对胡叫牌有两种情况
	    //1、N坎 + 1张单牌
	    //2、N-1坎 + 两对牌
	    int singleCount = 0;
	    int pairCount = 0;
	    List<Integer> arr = new ArrayList<Integer>();
	    for(int k : countMap.keySet()){
	        int c = countMap.get(k);
	        if(c == 1){
	            singleCount++;
	            arr.add(k);
	        }
	        else if(c == 2){
	            pairCount++;
	            arr.add(k);
	        }
	        else if(c == 3){
	            continue;
	        }
	        else if(c == 4){
	            //手上有4个一样的牌，在推到胡中是胡不了对对胡的 随便加点东西
	            return false;
	        }
	    }

	    if((pairCount == 2 && singleCount == 0) || (pairCount == 0 && singleCount == 1) ){
	        for(int p : arr){
	        	
	        	if(p == pai){
	        		//对对胡1番
		        	TingPai tingPai = gameSeat.getTingPai();
		        	tingPai.setPai(pai);
		        	tingPai.setPattern("duidui");
		        	tingPai.setScore(SCORE);
		        	tingPai.setTing(true);
		        	return true;
	        	}
	            
	        }
	    }
	    return false;
	}

}
