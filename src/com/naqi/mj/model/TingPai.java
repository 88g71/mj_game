package com.naqi.mj.model;

public class TingPai {
	public static final int TYPE_PI_HU = 0;
	public static final int TYPE_JIA_ZI = 1;
	
	private int fan;
	private String pattern;
	private int pai;
	private int score;
	private int type; 
	private boolean isTing;
	
	public TingPai(int fan , String pattern , int pai , int score){
		this.fan = fan;
		this.pattern = pattern;
		this.setPai(pai);
		this.setScore(score);
	}
	
	public int getFan() {
		return fan;
	}
	public void setFan(int fan) {
		this.fan = fan;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public int getPai() {
		return pai;
	}

	public void setPai(int pai) {
		this.pai = pai;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isTing() {
		return isTing;
	}

	public void setTing(boolean isTing) {
		this.isTing = isTing;
	}
}
