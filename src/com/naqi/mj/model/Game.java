package com.naqi.mj.model;

import java.util.ArrayList;
import java.util.List;

public class Game {
	//游戏桌子
	private RoomTable roomTable;
	//----游戏相关------------
	//牌
	private List<Integer> mahjongs = new ArrayList<Integer>();
	//麻将牌当前索引
	private int currentIndex;
	//庄家位置
	private int zhuangIndex;
	//当前轮到哪家
	private int turn;
	private int chuPai = -1;
	private int chupaiCnt;
	private int state;
	private List<GameAction> actionList = new ArrayList<GameAction>();
	private QiangGangContext qiangGangContext;
	
	private GameTick gameTick;
	
	private HuPaiInfo huInfo;
	
	
	public Game(RoomTable roomTable){
		this.roomTable = roomTable;
	}
	public RoomTable getRoomTable() {
		return roomTable;
	}
	/**
	 * 移到下家
	 */
	public void moveToNext(){
		this.turn++;
		this.turn %= this.roomTable.getPlayerNum();
	}
	

	public List<Integer> getMahjongs() {
		return mahjongs;
	}

	public void setMahjongs(List<Integer> mahjongs) {
		this.mahjongs = mahjongs;
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setCurrentIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	public void addCurrentIndex() {
		this.currentIndex++;
	}

	public int getZhuangIndex() {
		return zhuangIndex;
	}

	public void setZhuangIndex(int zhuangIndex) {
		this.zhuangIndex = zhuangIndex;
	}

	public int getPlayerNum() {
		return roomTable.getPlayerNum();
	}

	public int getTurn() {
		return turn;
	}

	public void setTurn(int turn) {
		this.turn = turn;
	}
	public int getChuPai() {
		return chuPai;
	}
	public void setChuPai(int chuPai) {
		this.chuPai = chuPai;
	}
	public GameSeat getGameSeat(int seatIndex) {
		TableSeat tableSeat = this.roomTable.getSeatByIndex(seatIndex);
		if(tableSeat != null ){
			return tableSeat.getGameSeat();
		}
		return null;
	}
	public List<TableSeat> getTableSeats() {
		return this.roomTable.getTableSeats();
	}
	public GameConfig getConf() {
		return this.roomTable.getGameConfig();
	}
	public void reset() {
		this.zhuangIndex = roomTable.getNextButton();
        this.mahjongs.clear();
        this.currentIndex = 0;
        this.turn = zhuangIndex;
        this.chuPai = -1;
        this.state = IGameConstant.GAME_STATE_IDLE;
        this.actionList.clear();
        this.chupaiCnt =0;
	}
	public int getChupaiCnt() {
		return chupaiCnt;
	}
	public void setChupaiCnt(int chupaiCnt) {
		this.chupaiCnt = chupaiCnt;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public List<GameAction> getActionList() {
		return actionList;
	}
	public void setActionList(List<GameAction> actionList) {
		this.actionList = actionList;
	}
	public QiangGangContext getQiangGangContext() {
		return qiangGangContext;
	}
	public void setQiangGangContext(QiangGangContext qiangGangContext) {
		this.qiangGangContext = qiangGangContext;
	}
	public GameTick getGameTick() {
		return gameTick;
	}
	public void setGameTick(GameTick gameTick) {
		this.gameTick = gameTick;
	}
	public HuPaiInfo getHuInfo() {
		return huInfo;
	}
	public void setHuInfo(HuPaiInfo huInfo) {
		this.huInfo = huInfo;
	}
	
}
