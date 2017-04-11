package com.naqi.mj.model;

public class QiangGangContext {
	private GameSeat turnSeat;	//
    private GameSeat seatData;	//
    private int pai;			//牌
    private boolean isValid;	//
    
	public GameSeat getTurnSeat() {
		return turnSeat;
	}
	public void setTurnSeat(GameSeat turnSeat) {
		this.turnSeat = turnSeat;
	}
	public GameSeat getSeatData() {
		return seatData;
	}
	public void setSeatData(GameSeat seatData) {
		this.seatData = seatData;
	}
	public int getPai() {
		return pai;
	}
	public void setPai(int pai) {
		this.pai = pai;
	}
	public boolean isValid() {
		return isValid;
	}
	public void setValid(boolean isValid) {
		this.isValid = isValid;
	}
}
