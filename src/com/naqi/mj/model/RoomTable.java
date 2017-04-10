package com.naqi.mj.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class RoomTable {
	private int id;
	private String uuid;
	private int creator;
	private long createTime;
	private int numOfTurns;
	private int nextButton;
	private String conf;
	private GameConfig gameConfig;
	private int playerNum;
	private int numOfGames;
	private List<TableSeat> tableSeats = new ArrayList<TableSeat>();
	private Game game;
	
	public RoomTable(String conf){
		Map<String,Object> confMap = JSON.parseObject(conf);
		this.gameConfig = new GameConfig(confMap);
		int playerNum = this.gameConfig.getPlayerNum();
		this.playerNum = playerNum;
		for (int i = 0; i < playerNum; i++) {
			tableSeats.add(new TableSeat(this,i));
		}
		this.conf = conf;
		this.game = new Game(this);
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public long getCreateTime() {
		return createTime;
	}
	public void setCreateTime(long createTime) {
		this.createTime = createTime;
	}
	public int getNumOfTurns() {
		return numOfTurns;
	}
	public void setNumOfTurns(int numOfTurns) {
		this.numOfTurns = numOfTurns;
	}
	public int getNextButton() {
		return nextButton;
	}
	public void setNextButton(int nextButton) {
		this.nextButton = nextButton;
	}
	public String getConf() {
		return conf;
	}
	public void setConf(String conf) {
		this.conf = conf;
	}
	public GameConfig getGameConfig() {
		return gameConfig;
	}
	public void setGameConfig(GameConfig gameConfig) {
		this.gameConfig = gameConfig;
	}
	public int getPlayerNum() {
		return playerNum;
	}
	public void setPlayerNum(int playerNum) {
		this.playerNum = playerNum;
	}
	public Game getGame() {
		return game;
	}
	public void setGame(Game game) {
		this.game = game;
	}

	public List<TableSeat> getTableSeats() {
		return tableSeats;
	}

	public void setTableSeats(List<TableSeat> tableSeats) {
		this.tableSeats = tableSeats;
	}

	public TableSeat getSeatByIndex(int seatIndex) {
		if(seatIndex < this.playerNum){
			return tableSeats.get(seatIndex);
		}
		return null;
	}

	public int getNumOfGames() {
		return numOfGames;
	}

	public void setNumOfGames(int numOfGames) {
		this.numOfGames = numOfGames;
	}

	public int getCreator() {
		return creator;
	}

	public void setCreator(int creator) {
		this.creator = creator;
	}
	
	
}
