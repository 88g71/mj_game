package com.naqi.mj.model;

import java.util.ArrayList;
import java.util.List;

public class SeatAction {
	
	public static final int TYPE_FANG_GANG = 1;
	public static final int TYPE_AN_GANG = 2;
	public static final int TYPE_WAN_GANG = 3;
	public static final int TYPE_DIAN_GANG = 4;
	public static final int TYP_ZHUAN_SHOU_GANG = 5;
	//事件类型
	private int type;
	//目标席位
	private List<Integer> targets = new ArrayList<Integer>();
	//分数
	private int score;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public List<Integer> getTargets() {
		return targets;
	}
	public void setTargets(List<Integer> targets) {
		this.targets = targets;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
}
