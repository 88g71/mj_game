package com.naqi.mj.model;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 游戏房间，桌子容器
 * @author hanfeng
 *
 */
public class GameRoom {
	private static Logger logger = LoggerFactory.getLogger(GameRoom.class);
	private Map<Integer, RoomTable> gameTableMap = new ConcurrentHashMap<Integer, RoomTable>();
	private Lock lock = new ReentrantLock();
	private int gameTableNum = 0;
	
	public RoomTable getGameTable(int id){
		return gameTableMap.get(id);
	}
	/**
	 * 增加桌子
	 * @param gameTable
	 */
	public void putGameTable(RoomTable gameTable){
		if(gameTable == null) return;
		try {
			lock.lock();
			gameTableMap.put(gameTable.getId(), gameTable);
			gameTableNum ++;
		} catch (Exception e) {
			logger.error("创建桌子失败  桌子id:{},",gameTable.getId(),e.toString());
		}finally{
			lock.unlock();
		}
	}
	
	public boolean removeGameTable(int id){
		boolean ret = false;
		try{
			lock.lock();
			RoomTable gameTable = gameTableMap.remove(id);
			if(gameTable != null) {
				ret = true;
				gameTableNum --;
			}
		}catch(Exception e){
			logger.error("删除桌子失败  桌子id:{},",id,e.toString());
		}finally{
			lock.unlock();
		}
		return ret;
	}
	
	public int size(){
		return gameTableNum;
	}
	public Map<Integer, RoomTable> getRoomTableMap() {
		return gameTableMap;
	}
}
