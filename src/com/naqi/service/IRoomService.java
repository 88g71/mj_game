package com.naqi.service;

import com.naqi.mj.model.RoomTable;
import com.naqi.mj.model.TableSeat;
import com.naqi.model.User;
import com.naqi.service.dto.CreateRoomResult;

public interface IRoomService {
	
	/**
	 * 根据用户获得桌子
	 * @param user
	 * @return
	 */
	public RoomTable getUserGameTable(User user);
	/**
	 * 获取桌子位置
	 * @param roomTable
	 * @param user
	 * @return
	 */
	public TableSeat getUserSeat(RoomTable roomTable, User user);
	/**
	 * 创建房间
	 * @param user
	 * @param roomConf
	 * @return
	 */
	public CreateRoomResult createTable(User user, String conf, int gems, int tableId) ;
	/**
	 * 桌子事件
	 * @param index
	 */
	public void gameTick(int index);
	/**
	 * 根据table id获取桌子
	 * @param tableId
	 * @return
	 */
	public RoomTable getRoomTable(int tableId);
	/**
	 * 加入桌子
	 * @param user
	 * @param roomId
	 * @return
	 */
	public int enterTable(User user,int roomId);
	/**
	 * 准备
	 * @param userId
	 * @param ready
	 */
	public void setReady(int userId, boolean ready);
	/**
	 * 初始化
	 */
	public void init();
	
}
