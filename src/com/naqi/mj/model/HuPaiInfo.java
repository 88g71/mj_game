package com.naqi.mj.model;

public class HuPaiInfo {
	public static final int ACTION_GANG_HUA = 1;
	public static final int ACTION_DIAN_GANG_HUA = 2;
	public static final int ACTION_QIANG_GANG_HU = 3;
	public static final int ACTION_BEI_QIANG_GANG = 4;
	public static final int ACTION_ZI_MO = 5;
	public static final int ACTION_TYPE_HU = 6;
	public static final int ACTION_TYP_GANG_PAO_HU = 7;
	public static final int ACTION_TYPE_GANG_PAO = 8;
	public static final int ACTION_TYPE_FANG_PAO = 9;
	
	private int pai;
	private boolean huPai;
	private int action;
	private boolean zimo;
	private int target;
	private int fan;
	private boolean gangHu;
	private boolean qiangGangHu;
	private String pattern;
	private int score;
	private int numofgen;

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

	public int getNumofgen() {
		return numofgen;
	}

	public void setNumofgen(int numofgen) {
		this.numofgen = numofgen;
	}

}
