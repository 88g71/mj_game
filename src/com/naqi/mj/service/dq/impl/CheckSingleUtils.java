package com.naqi.mj.service.dq.impl;

import java.util.List;
import java.util.Map;

import com.naqi.mj.model.GameSeat;
import com.naqi.mj.model.TingPai;

public class CheckSingleUtils {

	public static boolean checkSingle(GameSeat gameSeat , int pai){
//		List<Integer> holds = gameSeat.getHolds();
		
		//选择的牌
		int selected = -1;
		int c = 0;
		for(int holdPai : gameSeat.getHoldsPai()){
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
	public static boolean matchSingle(GameSeat gameSeat, int selected , int chupai){
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
