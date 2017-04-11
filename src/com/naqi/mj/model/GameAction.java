package com.naqi.mj.model;
/**
 * 记录游戏动作
 * @author hanFeng
 *
 */
public class GameAction {
	//出牌
	public final static int ACTION_CHUPAI = 1;
	//摸牌
	public final static int ACTION_MOPAI = 2;
	//碰
	public final static int ACTION_PENG = 3;
	//杠
	public final static int ACTION_GANG = 4;
	//胡牌
	public final static int ACTION_HU = 5;
	//自摸
	public final static int ACTION_ZIMO = 6;
	/** 座位*/
	private int seatIndex;
	/** 事件*/
    private int action;
    /** 牌 */
    private int pai;
	public GameAction(int seatIndex, int action, int pai) {
		this.seatIndex=seatIndex;
		this.action=action;
		this.pai=pai;
	}
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
