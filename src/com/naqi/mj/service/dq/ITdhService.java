package com.naqi.mj.service.dq;

import com.naqi.mj.model.Game;
import com.naqi.mj.model.GameSeat;
import com.naqi.mj.model.GameTick;
import com.naqi.mj.model.RoomTable;
import com.naqi.mj.model.TableSeat;
import com.naqi.model.User;

public interface ITdhService {
	/**
	 * 过
	 * @param roomTable
	 * @param tableSeat
	 */
	public void guo(RoomTable roomTable, TableSeat tableSeat);
	/**
	 * 准备
	 * @param roomTable
	 * @param userId
	 */
	public void setReady(RoomTable roomTable, int userId);
	/**
	 * 出牌
	 * @param roomTable
	 * @param gameSeat
	 * @param pai
	 */
	public void chuPai(RoomTable roomTable,User user, int pai);
	/**
	 * 碰牌
	 * @param roomTable
	 * @param tableSeat
	 */
	public void peng(RoomTable roomTable, TableSeat tableSeat);
	/**
	 * 是否游戏中
	 * @param roomTable
	 * @return
	 */
	public boolean isPlaying(RoomTable roomTable);
	/**
	 * 杠牌
	 * @param game
	 * @param turnSeat
	 * @param seatData
	 * @param gangtype
	 * @param numOfCnt
	 * @param pai
	 */
	public void doGang(Game game, GameSeat turnSeat, GameSeat seatData, int gangtype,
			int numOfCnt, int pai);
	/**
	 * 胡牌
	 * @param roomTable
	 * @param tableSeat
	 */
	public void hu(RoomTable roomTable, TableSeat tableSeat);
	/**
	 * 过操作
	 * @param game
	 * @param gameTick
	 */
	public void guoTick(Game game, GameTick gameTick);
	/**
	 * 杠牌
	 * @param roomTable
	 * @param seatData
	 * @param pai
	 */
	public void gang(RoomTable roomTable, TableSeat tableSeat, int pai);

}