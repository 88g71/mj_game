package com.naqi.mj.service.dq.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.naqi.mj.model.Game;
import com.naqi.mj.model.GameTick;
import com.naqi.mj.service.dq.ITdhService;
import com.naqi.service.IGameExtension;
@Service(value = "GameTickGuo")
public class GameTickGuo implements IGameExtension{
	@Resource
	private ITdhService tdhService;
	@Override
	public void gameTick(Game game) {
		if(game != null && game.getGameTick() != null
				&& game.getGameTick().getExcudeTime() < System.currentTimeMillis()){
			GameTick gameTick = game.getGameTick();
			//删除游戏事件
			game.setGameTick(null);
			if(gameTick.getAction() == GameTick.ACTION_GUO){
				tdhService.guoTick(game,gameTick);
			}
		}
	}
	

}
