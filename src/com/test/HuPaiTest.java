package com.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.naqi.mj.model.GameSeat;
import com.naqi.mj.model.RoomTable;
import com.naqi.mj.model.TableSeat;
import com.naqi.mj.service.dq.impl.DuiDuiHu;
import com.naqi.mj.service.dq.impl.Hu7Pairs;
import com.naqi.mj.service.dq.impl.JiaHu;
import com.naqi.mj.service.dq.impl.YiTiaoLong;

public class HuPaiTest {
	public static void main(String[] args) {
		RoomTable roomTable = new RoomTable("{\"playerNum\":4}");
		TableSeat tableSeat = new TableSeat(roomTable, 0);
		GameSeat gameSeat = new GameSeat(tableSeat);
		
		Map<Integer, Integer> countMap = new HashMap<Integer, Integer>();
		countMap.put(0, 3);
		countMap.put(1, 3);
		countMap.put(2, 3);
		countMap.put(20, 1);
		countMap.put(22, 1);
		countMap.put(15, 2);
		gameSeat.setCountMap(countMap);
		
		List<Integer> holds = new ArrayList<>();
		holds.add(0);
		holds.add(0);
		holds.add(0);
		holds.add(1);
		holds.add(1);
		holds.add(1);
		holds.add(2);
		holds.add(2);
		holds.add(2);
		holds.add(27);
		holds.add(28);
		holds.add(15);
		holds.add(15);
		gameSeat.setHolds(holds);
		
		JiaHu jiaHu = new JiaHu();
		boolean canHu = jiaHu.check(gameSeat, 21);
		System.out.println(canHu + " : " + gameSeat.getTingPai().getType());
		DuiDuiHu duiduiHu = new DuiDuiHu();
		canHu = duiduiHu.check(gameSeat, 29);
		System.out.println(canHu);
		Hu7Pairs hu7Pairs = new Hu7Pairs();
		canHu = hu7Pairs.check(gameSeat, 29);
		System.out.println(canHu);
		YiTiaoLong yiTiaoLong = new YiTiaoLong();
		canHu = yiTiaoLong.check(gameSeat, 29);
		System.out.println(canHu);
	}
	
}
