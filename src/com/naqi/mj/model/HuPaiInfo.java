package com.naqi.mj.model;

public class HuPaiInfo {
	//杠花
	public static final int ACTION_GANG_HUA = 1;
	//点杠花
	public static final int ACTION_DIAN_GANG_HUA = 2;
	//强杠花
	public static final int ACTION_QIANG_GANG_HU = 3;
	//被抢杠花
	public static final int ACTION_BEI_QIANG_GANG = 4;
	//自摸
	public static final int ACTION_ZI_MO = 5;
	//
	public static final int ACTION_TYPE_HU = 6;
	public static final int ACTION_TYP_GANG_PAO_HU = 7;
	public static final int ACTION_TYPE_GANG_PAO = 8;
	public static final int ACTION_TYPE_FANG_PAO = 9;
	
	private int seatIndex;
	private int pai = -1;			//胡牌
	private boolean huPai;			//是否胡牌
	private int action;				//胡牌类型
	private boolean zimo;			//自摸
	private int target = -1;		//点炮手
	private int fan = 1;				//翻倍
	private boolean gangHu;			//杠花
	private boolean qiangGangHu;	//抢杠
	private String pattern;			//说明
	private int score;				//分数

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public boolean isZimo() {
		return zimo;
	}

	public void setZimo(boolean zimo) {
		this.zimo = zimo;
	}

	public int getTarget() {
		return target;
	}

	public void setTarget(int target) {
		this.target = target;
	}

	public int getFan() {
		return fan;
	}

	public void setFan(int fan) {
		this.fan = fan;
	}

	public int getPai() {
		return pai;
	}

	public void setPai(int pai) {
		this.pai = pai;
	}

	public boolean isGangHu() {
		return gangHu;
	}

	public void setGangHu(boolean gangHu) {
		this.gangHu = gangHu;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public boolean isHuPai() {
		return huPai;
	}

	public void setHuPai(boolean huPai) {
		this.huPai = huPai;
	}

	public boolean isQiangGangHu() {
		return qiangGangHu;
	}

	public void setQiangGangHu(boolean qiangGangHu) {
		this.qiangGangHu = qiangGangHu;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getSeatIndex() {
		return seatIndex;
	}

	public void setSeatIndex(int seatIndex) {
		this.seatIndex = seatIndex;
	}

}
