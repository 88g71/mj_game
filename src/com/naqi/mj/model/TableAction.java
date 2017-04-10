package com.naqi.mj.model;

public class TableAction {
	public final static int ACTION_CHUPAI = 1;
	public final static int ACTION_MOPAI = 2;
	public final static int ACTION_PENG = 3;
	public final static int ACTION_GANG = 4;
	public final static int ACTION_HU = 5;
	public final static int ACTION_ZIMO = 6;
	
	private int seatIndex;
    private int action;
    private int pai;
	public int getSeatIndex() {
		return seatIndex;
	}
	public void setSeatIndex(int seatIndex) {
		this.seatIndex = seatIndex;
	}
	public int getPai() {
		return pai;
	}
	public void setPai(int pai) {
		this.pai = pai;
	}
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
    
    
}
