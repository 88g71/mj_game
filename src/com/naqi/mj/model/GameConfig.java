package com.naqi.mj.model;

import java.util.Map;

public class GameConfig {
	private static final String BASE_SCORE = "baseScore";
	private static final String MAX_FAN = "maxFan";
	private static final String PLAYER_NUM = "playerNum";
	private static final String MAX_QUAN = "maxQuan";
	
	
	private static final int DEFAULT_PLAYER_NUM = 4;
	
	
	private Map<String, Object> confMap;
	
	public GameConfig(Map<String, Object> confMap) {
		this.confMap = confMap;
	}
	
	public int getBaseScore(){
		return getIntValue(BASE_SCORE);
	}

	public int getPlayerNum(){
		int playerNum = getIntValue(PLAYER_NUM); 
		return  playerNum == 0 ? DEFAULT_PLAYER_NUM : playerNum;
	}

	private int getIntValue(String key) {
		Object value =  confMap.get(key);
		return value == null ? 0 : Integer.parseInt(value.toString());
	}

	public int getMaxFan() {
		return getIntValue(MAX_FAN);
	}
	public int getMaxQuan() {
		Object value =  getIntValue(MAX_QUAN);
		return value == null ? 4 : Integer.parseInt(value.toString());
	}
	
}
