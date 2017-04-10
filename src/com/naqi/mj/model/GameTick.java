package com.naqi.mj.model;

public class GameTick {
	public static final int ACTION_CHU_PAI = 0;
	public static final int ACTION_GUO = 1;
	
	private long excudeTime;
	private int action;
	private GameSeat gameSeat;
	private Game game;
	
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public GameSeat getGameSeat() {
		return gameSeat;
	}
	public void setGameSeat(GameSeat gameSeat) {
		this.gameSeat = gameSeat;
	}
	public Game getGame() {
		return game;
	}
	public void setGame(Game game) {
		this.game = game;
	}
	public long getExcudeTime() {
		return excudeTime;
	}
	public void setExcudeTime(long excudeTime) {
		this.excudeTime = excudeTime;
	}
	
}
