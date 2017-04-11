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
@Service(value ="YiTiaoLong")
public class YiTiaoLong implements ITingExtension{
	private static Logger logger = LoggerFactory.getLogger(YiTiaoLong.class);
	
	public boolean check(GameSeat gameSeat ,int pai){
		List<Integer> holds = gameSeat.getHolds();
		List<Integer> tempHolds = new ArrayList<>(holds);
		logger.info("检测前--》玩家：{}，牌：{}",gameSeat.getTableSeat().getUserName(),holds);
		//先加上要胡的牌
		if(pai != -1){
			holds.add(pai);
			gameSeat.addPaiCount(pai, 1);
		}
		
		Map<Integer,Integer> countMap = gameSeat.getCountMap();
		int type = MjUtils.getMJType(pai);
		
		boolean ret = true;
		if(type != 3){
			int baseNum = type * 9;
			for(int i = 0 ; i < 9 ; i ++){
				int curPai = i+baseNum;
				int paiNum = gameSeat.getPaiCount(curPai);
				if(paiNum <= 0) {
					ret = false;
					break;
				}
			}
			if(ret){
				ret = false;
				for(int i = 0 ; i < 9 ; i ++){
					int curPai = i+baseNum;
					gameSeat.rePaiCount(curPai, 1);
					holds.remove(holds.indexOf(curPai));
				}
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
					ret = checkSingle(gameSeat);
					countMap.put(k, countMap.get(k)+2);
					if(ret){
						TingPai tingPai = gameSeat.getTingPai();
						tingPai.setPai(pai);
						tingPai.setPattern("yi_tiao_long");
						tingPai.setScore(10);
						tingPai.setTing(true);
						break;
					}
				}
				for(int i = 0 ; i < 9 ; i ++){
					int curPai = i+baseNum;
					gameSeat.addPaiCount(curPai, 1);
					holds.add(holds.indexOf(curPai));
				}
			}
		}else{
			ret = false;
		}
		if(pai != -1){
			gameSeat.rePaiCount(pai,1);
		}
		holds = tempHolds;
		logger.info("检测后--》玩家：{}，牌：{}",gameSeat.getTableSeat().getUserName(),holds);
		return ret;
	}
	
	public boolean checkSingle(GameSeat gameSeat){
		List<Integer> holds = gameSeat.getHolds();
		
		//选择的牌
		int selected = -1;
		int c = 0;
		for(int holdPai : holds){
			c = gameSeat.getPaiCount(holdPai);
			if(c != 0){
				selected = holdPai;
				break;
			}
		}
		//如果没有找到剩余牌，则表示匹配成功了
		if(selected == -1){
			return true;
		}
		//否则，进行匹配
		if(c == 3){
			//直接作为一坎
			gameSeat.getCountMap().put(selected, 0);
			boolean ret = checkSingle(gameSeat);
			//立即恢复对数据的修改
			gameSeat.addPaiCount(selected, c);
			if(ret){
				return true;
			}
		}
		else if(c == 4){
			//直接作为一坎
			gameSeat.getCountMap().put(selected, 1);
			boolean ret = checkSingle(gameSeat);
			//立即恢复对数据的修改
			gameSeat.addPaiCount(selected, c);
			//如果作为一坎能够把牌匹配完，直接返回TRUE。
			if(ret == true){
				return true;
			}
		}
		
		//按单牌处理
		return matchSingle(gameSeat,selected );
	}
	//单牌处理
	public boolean matchSingle(GameSeat gameSeat, int selected ){
		//分开匹配 A-2,A-1,A
		boolean matched = true;
		int v = selected % 9;
		if(v < 2){
			matched = false;
		}
		else{
			for(int i = 0; i < 3; ++i){
				int t = selected - 2 + i;
				int cc = gameSeat.getPaiCount(t);
				if(cc == 0){
					matched = false;
					break;
				}
			}		
		}
		
		Map<Integer,Integer> countMap = gameSeat.getCountMap();
		//匹配成功，扣除相应数值
		if(matched){
			countMap.put(selected - 2, gameSeat.getPaiCount(selected - 2) - 1);
			countMap.put(selected - 1, gameSeat.getPaiCount(selected - 1) - 1);
			countMap.put(selected, gameSeat.getPaiCount(selected) - 1);
			boolean ret = checkSingle(gameSeat);
			countMap.put(selected - 2, gameSeat.getPaiCount(selected - 2) + 1);
			countMap.put(selected - 1, gameSeat.getPaiCount(selected - 1) + 1);
			countMap.put(selected, gameSeat.getPaiCount(selected) + 1);
			if(ret == true){
				return true;
			}		
		}

		//分开匹配 A-1,A,A + 1
		matched = true;
		if(v < 1 || v > 7){
			matched = false;
		}
		else{
			for(int i = 0; i < 3; ++i){
				int t = selected - 1 + i;
				int cc = gameSeat.getPaiCount(t);
				if(cc == 0){
					matched = false;
					break;
				}
			}		
		}

		//匹配成功，扣除相应数值
		if(matched){
			countMap.put(selected - 1, gameSeat.getPaiCount(selected - 1) - 1);
			countMap.put(selected, gameSeat.getPaiCount(selected) - 1);
			countMap.put(selected + 1 , gameSeat.getPaiCount(selected + 1) - 1);
			boolean ret = checkSingle(gameSeat);
			countMap.put(selected - 1, gameSeat.getPaiCount(selected - 1) + 1);
			countMap.put(selected, gameSeat.getPaiCount(selected) + 1);
			countMap.put(selected + 1, gameSeat.getPaiCount(selected + 1) + 1);
			
			if(ret == true){
				return true;
			}		
		}
		
		
		//分开匹配 A,A+1,A + 2
		matched = true;
		if(v > 6){
			matched = false;
		}
		else{
			for(int i = 0; i < 3; ++i){
				int t = selected + i;
				int cc = gameSeat.getPaiCount(t);
				if(cc == 0){
					matched = false;
					break;
				}
			}		
		}

		//匹配成功，扣除相应数值
		if(matched){
			countMap.put(selected, gameSeat.getPaiCount(selected) - 1);
			countMap.put(selected + 1 , gameSeat.getPaiCount(selected + 1) - 1);
			countMap.put(selected + 2, gameSeat.getPaiCount(selected + 2) - 1);
			boolean ret = checkSingle(gameSeat);
			countMap.put(selected, gameSeat.getPaiCount(selected) + 1);
			countMap.put(selected + 1, gameSeat.getPaiCount(selected + 1) + 1);
			countMap.put(selected + 2, gameSeat.getPaiCount(selected + 2) + 1);
			if(ret == true){
				return true;
			}		
		}
		return false;
	}
}
