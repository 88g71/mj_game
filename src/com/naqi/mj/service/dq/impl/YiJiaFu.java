package com.naqi.mj.service.dq.impl;

import org.springframework.stereotype.Service;

import com.naqi.mj.model.Game;
import com.naqi.mj.model.GameSeat;
import com.naqi.mj.model.HuPaiInfo;
import com.naqi.mj.model.SeatAction;
import com.naqi.mj.model.TableSeat;
import com.naqi.mj.service.dq.ICalculateExtension;
@Service(value = "YiJiaFu")
public class YiJiaFu implements ICalculateExtension{

	@Override
	public void calculateResult(Game game) {
		
		if(game.getConf().getCalculate() != 0) return;
		
		int baseScore = game.getConf().getBaseScore();
	    for(TableSeat tableSeat: game.getTableSeats()){
	    	GameSeat  gameSeat = tableSeat.getGameSeat();
	    	//计算杠
            int additonalscore = 0;
            for( SeatAction action : gameSeat.getActions()){
                if(action.getType() == SeatAction.TYPE_AN_GANG 
                		|| action.getType() == SeatAction.TYPE_WAN_GANG 
                		|| action.getType() == SeatAction.TYPE_DIAN_GANG ){
                        int acscore = action.getScore();
                        additonalscore += action.getTargets().size() * acscore * baseScore;
                        gameSeat.setScore(gameSeat.getScore() + additonalscore);
                        //扣掉目标方的分
                        for(int targetIndex : action.getTargets()){
                        	GameSeat targetSeat = game.getGameSeat(targetIndex);
                            int score = targetSeat.getScore();
                            score -= acscore * baseScore;
                            targetSeat.setScore(score);
                        }                   
                }
            }
            gameSeat.setNumAnGang(gameSeat.getAngangs().size());
            gameSeat.setNumMingGang(gameSeat.getWangangs().size() + gameSeat.getDiangangs().size());
	    }
	    
	    
	    
	    //进行胡牌结算
        HuPaiInfo huPaiInfo = game.getHuInfo();
        //统计自己的番子和分数
        int fan = huPaiInfo.getFan();
        
        //杠上花+1番
        if(huPaiInfo.getAction() == HuPaiInfo.ACTION_GANG_HUA 
        		|| huPaiInfo.getAction() == HuPaiInfo.ACTION_DIAN_GANG_HUA 
        		|| huPaiInfo.getAction() == HuPaiInfo.ACTION_QIANG_GANG_HU ){
            fan += 1;
        }
        if(huPaiInfo.isZimo()){
           fan += 1;
        }
        //和牌的玩家才加这个分
        int score = computeFanScore(game,fan,huPaiInfo.getScore());
        
        GameSeat gameSeat= game.getGameSeat(huPaiInfo.getSeatIndex());
        
        if(huPaiInfo.isZimo()){
            //收所有人的钱
            gameSeat.setScore(gameSeat.getScore() + score * (game.getPlayerNum()-1));
            for(TableSeat otherTableSeat : game.getTableSeats()){
            	GameSeat otherSeat = otherTableSeat.getGameSeat();
            	if(otherSeat != gameSeat){
            		otherSeat.setScore(otherSeat.getScore()- score);
            	}
            	
            }
            gameSeat.setNumZiMo(gameSeat.getNumZiMo()+1);
        }
        else{
            //收放炮者的钱
            gameSeat.setScore(gameSeat.getScore()+ score); ;
            GameSeat targetSeat = game.getTableSeats().get(huPaiInfo.getTarget()).getGameSeat();
            targetSeat.setScore(targetSeat.getScore() - score);
            gameSeat.setNumDianPao(gameSeat.getNumDianPao() + 1);
        }
        
        huPaiInfo.setFan(fan);
		
	}
	
	/**
	 * 翻数计算
	 * @param game
	 * @param fan
	 * @return
	 */
	private int computeFanScore(Game game,int fan ,int score){
		int maxFan = game.getConf().getMaxFan();
	    if(fan > maxFan){
	        fan = maxFan;
	    }
	    return (1 << fan) * score;
	}
}
