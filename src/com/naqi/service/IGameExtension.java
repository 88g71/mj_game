package com.naqi.service;

import com.naqi.mj.model.Game;

public interface IGameExtension {
	/**
	 * 事件
	 * @param game
	 */
	public void gameTick(Game game);
}
