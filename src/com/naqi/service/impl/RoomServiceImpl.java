package com.naqi.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gxlu.mercury.extension.core.MercuryExtensionPlatform;
import com.naqi.mj.model.GameRoom;
import com.naqi.mj.model.RoomTable;
import com.naqi.mj.model.TableSeat;
import com.naqi.model.User;
import com.naqi.service.IConfigService;
import com.naqi.service.IGameExtension;
import com.naqi.service.IRoomService;
import com.naqi.service.IUserService;
import com.naqi.service.dto.CreateRoomResult;
import com.naqi.util.UUIDGenerator;
@Service(value = "roomService")
public class RoomServiceImpl implements IRoomService{
	private static Logger logger = LoggerFactory.getLogger(RoomServiceImpl.class);
	
	//游戏房间
	private List<GameRoom> gameRooms = null;
	//处理器数量匹配
	private int roomNum = 10;
	@Resource
	private IConfigService configService;
	@Resource
	private IUserService userService;
	private IGameExtension[] gameExtensions;
	
	@Override
	public RoomTable getUserGameTable(User user) {
		GameRoom gameRoom = getGameRoom(user.getRoomid());
		RoomTable roomTable = gameRoom.getGameTable(user.getRoomid());
		return roomTable;
	}
	
	@Override
	public TableSeat getUserSeat(RoomTable roomTable, User user) {
		List<TableSeat> list = roomTable.getTableSeats();
		for (TableSeat seat : list) {
			if(seat != null){
				if(seat.getUserId() == user.getId()){
					return seat;
				}
			}
		}
		return null;
	}
	
	
	public TableSeat getUserSeat(RoomTable roomTable, int userId) {
		List<TableSeat> list = roomTable.getTableSeats();
		for (TableSeat seat : list) {
			if(seat != null){
				if(seat.getUserId() == userId){
					return seat;
				}
			}
		}
		return null;
	}
//	public void test(GameRoom gameRoom){
//		if(gameRoom.size() > 0){
//			return;
//		}
//		User user = userService.getUserById(9);
//		
//		createTable(user, "{\"type\":\"xzdd\",\"difen\":0,\"zimo\":0,\"jiangdui\":false,\"huansanzhang\":false,\"zuidafanshu\":1,\"jushuxuanze\":0,\"dianganghua\":0,\"menqing\":false,\"tiandihu\":false}",3, 100000);
//		
//		logger.debug("房间服务测试....");
//	}
	
	@Override
	public CreateRoomResult createTable(User user, String conf ,int gems, int tableId) {
		CreateRoomResult result = new CreateRoomResult();
		RoomTable roomTable = getRoomTable(tableId);
		if(roomTable != null){
			result.setErrcode(1);
		}else{
			roomTable = new RoomTable(conf);
			roomTable.setId(tableId);
			roomTable.setUuid(UUIDGenerator.generatorUUID());
			roomTable.setNumOfTurns(0);
			roomTable.setCreateTime(System.currentTimeMillis());
			roomTable.setNextButton(0);
			roomTable.setCreator(user.getId());
			user.setRoomid(tableId);
			addTable2Room(tableId, roomTable);
		}	
		return result;
	}
	/**
	 * 获取游戏桌子
	 * @param tableId
	 * @return
	 */
	public RoomTable getRoomTable(int tableId) {
		GameRoom gameRoom = getGameRoom(tableId);
		RoomTable roomTable = gameRoom.getGameTable(tableId);
		return roomTable;
	}

	/**
	 * 创建新桌子
	 * @param tableId
	 * @param roomTable
	 */
	private void addTable2Room(int tableId, RoomTable roomTable) {
		GameRoom gameRoom = getGameRoom(tableId);
		gameRoom.putGameTable(roomTable);
		
	}
	@Override
	public void init(){
		gameRooms = new ArrayList<GameRoom>();
		for (int i = 0; i < 10; i++) {
			gameRooms.add(new GameRoom());
		}
	}
	/**
	 * 获得相应房间
	 * @param tableId
	 * @return
	 */
	private GameRoom getGameRoom(int tableId){
		GameRoom gameRoom = gameRooms.get(tableId % roomNum);
		return gameRoom;
	}
	

	@Override
	public void gameTick(int index) {
		GameRoom gameRoom = getGameRoom(index);
		for (int tableId : gameRoom.getRoomTableMap().keySet()) {
			RoomTable roomTable = gameRoom.getRoomTableMap().get(tableId);
			for (IGameExtension extension :getGameExtensions() ) {
				extension.gameTick(roomTable.getGame());
			}
		}
	}
	
	public IGameExtension[] getGameExtensions(){
		if(this.gameExtensions != null){
			return gameExtensions;
		}
		Object[] extensionArray = MercuryExtensionPlatform.getExtensionInstance(IGameExtension.class.getName());
		if(extensionArray != null){
			this.gameExtensions = new IGameExtension[extensionArray.length];
			for (int i = 0 ; i < extensionArray.length ; i++) {
				this.gameExtensions[i] = (IGameExtension) extensionArray[i];
			}
		}
		return gameExtensions;
	}

	@Override
	public int enterTable(User user, int tableId) {
		RoomTable roomTable = getRoomTable(tableId);
		
		if(roomTable != null){
			if( getUserSeat(roomTable, user.getId()) != null) return 0;
			for(TableSeat tableSeat : roomTable.getTableSeats()){
				if(tableSeat.getUserId() <= 0){
					tableSeat.setUserId(user.getId());
					tableSeat.setUserName(user.getName());
					user.setRoomid(tableId);
					return 0;
				}
			}	
		}
		return 1;
	}

	@Override
	public void setReady(int userId, boolean ready) {
		User user = userService.getUserById(userId);
		RoomTable roomTable = getUserGameTable(user);
		if(roomTable == null){
			return;
		}
		TableSeat tableSeat = getUserSeat(roomTable, user);
			if(tableSeat == null){
				return;
			}
		tableSeat.setReady(ready);
	}

	private RoomTable getRoomTableByUserId(int userId) {
		User user = userService.getUserById(userId);
		if(user != null){
			return getUserGameTable(user);
		}
		return null;
	}
	
}
