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
		Map<Integer,Integer> countMap = gameSeat.getCountMap();
		List<Integer> holds = gameSeat.getHolds();
		logger.info("检测前--》玩家：{}，牌：{}",gameSeat.getTableSeat().getUserName(),holds);
		//先加上要胡的牌
		if(pai != -1){
			holds.add(pai);
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
			ret = checkSingle(gameSeat , pai);
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
		}
		if(pai != -1){
			gameSeat.rePaiCount(pai,1);
			holds.remove(holds.size() -1);
		}
		logger.info("检测后--》玩家：{}，牌：{}",gameSeat.getTableSeat().getUserName(),holds);
		return ret;
	}
	
	public boolean checkSingle(GameSeat gameSeat , int pai){
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
		System.out.println(gameSeat.getCountMap().toString());
		//如果没有找到剩余牌，则表示匹配成功了
		if(selected == -1){
			return true;
		}
		//否则，进行匹配
		if(c == 3){
			//直接作为一坎
			gameSeat.getCountMap().put(selected, 0);
			boolean ret = checkSingle(gameSeat,pai);
			//立即恢复对数据的修改
			gameSeat.getCountMap().put(selected, c);
			if(ret){
				return true;
			}
		}
		else if(c == 4){
			//直接作为一坎
			gameSeat.getCountMap().put(selected, 1);
			boolean ret = checkSingle(gameSeat,pai);
			//立即恢复对数据的修改
			gameSeat.getCountMap().put(selected, c);
			//如果作为一坎能够把牌匹配完，直接返回TRUE。
			if(ret == true){
				return true;
			}
		}
		
		//按单牌处理
		return matchSingle(gameSeat,selected ,pai);
	}
	//单牌处理
	public boolean matchSingle(GameSeat gameSeat, int selected , int chupai){
		TingPai tingPai = gameSeat.getTingPai();
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
			//判断是不是夹胡－－－－－－-------------
			if(MjUtils.getMJType(selected) == 3||
					(MjUtils.getMJType(selected) == MjUtils.getMJType(chupai)
					&& ((selected -1 == chupai) 
							|| (v == 2 && selected == chupai)
							|| (v == 8 && selected - 2 == chupai)))){
				tingPai.setType(TingPai.TYPE_JIA_ZI);
			}
			//----------------------------------
			countMap.put(selected - 2, gameSeat.getPaiCount(selected - 2) - 1);
			countMap.put(selected - 1, gameSeat.getPaiCount(selected - 1) - 1);
			countMap.put(selected, gameSeat.getPaiCount(selected) - 1);
			boolean ret = checkSingle(gameSeat,chupai);
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
			//判断是不是夹胡－－－－－－-------------
			if( MjUtils.getMJType(selected) == 3||
					((selected == chupai) 
					||(MjUtils.getMJType(selected) == MjUtils.getMJType(chupai)
					&& (v == 1 && chupai == selected + 1
					||v == 7 && chupai == selected -1)))){
				tingPai.setType(TingPai.TYPE_JIA_ZI);
			}
			//----------------------------------
			countMap.put(selected - 1, gameSeat.getPaiCount(selected - 1) - 1);
			countMap.put(selected, gameSeat.getPaiCount(selected) - 1);
			countMap.put(selected + 1 , gameSeat.getPaiCount(selected + 1) - 1);
			boolean ret = checkSingle(gameSeat,chupai);
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
			//判断是不是夹胡－－－－－－-------------
			if(MjUtils.getMJType(selected) == 3||
					(MjUtils.getMJType(selected) == MjUtils.getMJType(chupai)
					&& ((selected +1 == chupai) 
					|| (v == 6 && selected == chupai)
					|| (v == 0 && selected + 2 == chupai)
					))){
				tingPai.setType(TingPai.TYPE_JIA_ZI);
			}
			//----------------------------------
			countMap.put(selected, gameSeat.getPaiCount(selected) - 1);
			countMap.put(selected + 1 , gameSeat.getPaiCount(selected + 1) - 1);
			countMap.put(selected + 2, gameSeat.getPaiCount(selected + 2) - 1);
			boolean ret = checkSingle(gameSeat,chupai);
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
