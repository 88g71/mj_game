package com.naqi.mj.service.dq.impl;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.naqi.mj.model.GameSeat;
import com.naqi.mj.model.TingPai;
import com.naqi.mj.service.dq.ITingExtension;
@Service(value = "JiaHu")
public class JiaHu implements ITingExtension{
	private static Logger logger = LoggerFactory.getLogger(JiaHu.class);
	@Override
	public boolean check(GameSeat gameSeat,int pai) {
		logger.info("JiaHu  用户名{}   牌 {}",gameSeat.getTableSeat().getUserName() , gameSeat.getHoldsPai());
		Map<Integer,Integer> countMap = gameSeat.getCountMap();
//		List<Integer> holds = gameSeat.getHolds();
		logger.info("检测前--》玩家：{}，牌：{}",gameSeat.getTableSeat().getUserName(),gameSeat.getHoldsPai());
		//先加上要胡的牌
		if(pai != -1){
			gameSeat.addholds(pai);
			gameSeat.addPaiCount(pai, 1);
		}
		
		boolean ret = false;
		
		for(int k : countMap.keySet()){
			int c = countMap.get(k);
			if(c < 2){
				continue;
			}
			//如果当前牌大于等于２，则将它选为将牌
			countMap.put(k, c-2);
			//逐个判定剩下的牌是否满足　３Ｎ规则,一个牌会有以下几种情况
			//1、0张，则不做任何处理
			//2、2张，则只可能是与其它牌形成匹配关系
			//3、3张，则可能是单张形成 A-2,A-1,A  A-1,A,A+1  A,A+1,A+2，也可能是直接成为一坎
			//4、4张，则只可能是一坎+单张
			ret = CheckSingleUtils.checkSingle(gameSeat , pai);
			gameSeat.addPaiCount(k,2);
			if(ret){
				TingPai tingPai = gameSeat.getTingPai();
				tingPai.setPai(pai);
				if(tingPai.getType() == TingPai.TYPE_PI_HU){
					tingPai.setPattern("pi_hu");
					tingPai.setScore(2);
				}else if(tingPai.getType() == TingPai.TYPE_JIA_ZI){
					tingPai.setPattern("jia_zi");
					tingPai.setScore(4);
				}
				tingPai.setTing(true);
				break;
			}
			gameSeat.getTingPai().setType(TingPai.TYPE_PI_HU);
		}
		if(pai != -1){
			gameSeat.rePaiCount(pai,1);
			gameSeat.removeHoldsLast();
		}
		logger.info("检测后--》玩家：{}，牌：{}",gameSeat.getTableSeat().getUserName(),gameSeat.getHoldsPai());
		return ret;
	}
	
	
}
