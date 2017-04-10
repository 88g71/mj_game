package com.naqi.mj.service.dq;

import com.naqi.mj.model.GameSeat;

public interface ITingExtension {
	/**
	 *  检测胡牌
	 * @param gameSeat
	 */
	public boolean check(GameSeat gameSeat ,int pai);
}
