package com.naqi.mj.service.dq.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.gxlu.mercury.extension.core.MercuryExtensionPlatform;
import com.naqi.mj.model.Game;
import com.naqi.mj.model.GameAction;
import com.naqi.mj.model.GameSeat;
import com.naqi.mj.model.GameTick;
import com.naqi.mj.model.HuPaiInfo;
import com.naqi.mj.model.IGameConstant;
import com.naqi.mj.model.QiangGangContext;
import com.naqi.mj.model.RoomTable;
import com.naqi.mj.model.SeatAction;
import com.naqi.mj.model.TableSeat;
import com.naqi.mj.model.TingPai;
import com.naqi.mj.service.dq.ICalculateExtension;
import com.naqi.mj.service.dq.ITdhService;
import com.naqi.mj.service.dq.ITingExtension;
import com.naqi.model.User;
import com.naqi.service.IRoomService;
import com.naqi.service.IUserService;

@Service(value = "TdhService")
public class TdhServiceImpl implements ITdhService {
	private static Logger logger = LoggerFactory.getLogger(TdhServiceImpl.class);
	@Resource
	private IUserService userService;
	@Resource
	private IRoomService roomService;
	/* 
	 * 洗牌
	 */
	private void shuffle(Game game) {
	    
	    List<Integer> mahjongs = game.getMahjongs();

	    //筒 (0 ~ 8 表示筒子
	    for(int i = 0; i < 9; ++i){
	        for(int c = 0; c < 4; ++c){
	            mahjongs.add(i);
	        }
	    }

	    //条 9 ~ 17表示条子
	    for(int i = 9; i < 18; ++i){
	    	for(int c = 0; c < 4; ++c){
	            mahjongs.add(i);
	        }
	    }

	    //万
	    //条 18 ~ 26表示万
	    for(int i = 18; i < 27; ++i){
	    	for(int c = 0; c < 4; ++c){
	            mahjongs.add(i);
	        }
	    }
	    //中发白 27~30
	    for(int i = 27; i < 30; ++i){
	    	for(int c = 0; c < 4; ++c){
	    		mahjongs.add(i);
	    	}
	    }

	    Collections.shuffle(mahjongs);
	}
	
	/**
	 * 摸牌
	 * @param game
	 * @param seatIndex
	 * @return
	 */
	private int mopai(Game game,int seatIndex) {
	    if(game.getCurrentIndex() == game.getMahjongs().size()){
	        return -1;
	    }
	    GameSeat gameSeat = game.getGameSeat(seatIndex);
	    List<Integer> holds = gameSeat.getHolds();
	    int pai = game.getMahjongs().get(game.getCurrentIndex());
	    holds.add(pai);

	    //统计牌的数目 ，用于快速判定（空间换时间）
	    gameSeat.addPaiCount(pai, 1);
	    game.addCurrentIndex();
	    return pai;
	}
	/**
	 * 发牌
	 * @param game
	 */
	private void deal(Game game){
	    //强制清0
	    game.setCurrentIndex(0);
	    GameSeat gameSeat = game.getGameSeat(game.getZhuangIndex());
	    int seatIndex = gameSeat.getIndex();
	    int playerNum = game.getPlayerNum();
	    //每人13张 一共 13*4 ＝ 52张 庄家多一张 53张
	    int zhuaNum = playerNum * 13;
	    for(int i = 0; i < zhuaNum; ++i){
	        mopai(game,seatIndex);
	        seatIndex ++;
	        seatIndex %= playerNum;
	    }
	    //庄家多摸最后一张
	    mopai(game,game.getZhuangIndex());
	    //当前轮设置为庄家
	    game.setTurn(game.getZhuangIndex());
	}
	
	/**
	 * 检测碰
	 * @param game
	 * @param gameSeat
	 * @param targetPai
	 */
	private void checkCanPeng(Game game ,GameSeat gameSeat ,int targetPai) {
	    int count = gameSeat.getPaiCount(targetPai);
	    if(count >= 2){
	        gameSeat.setCanPeng(true);
	    }
	}

	/**
	 * 检测点杠
	 * @param game
	 * @param gameSeat
	 * @param targetPai
	 */
	private void checkCanDianGang(Game game, GameSeat gameSeat,int targetPai){
	    //检查玩家手上的牌
	    int count = gameSeat.getPaiCount(targetPai);
	    if(count >= 3){
	    	gameSeat.setCanGang(true);
	        gameSeat.getGangPai().add(targetPai);
	    }
	}

	/**
	 * 检测暗杠
	 * @param game
	 * @param gameSeat
	 */
	private void checkCanAnGang(Game game,GameSeat gameSeat){
//	    //如果没有牌了，则不能再杠
//	    if(game.mahjongs.length <= game.currentIndex){
//	        return;
//	    }
		Map<Integer,Integer> countMap = gameSeat.getCountMap();
	    for(int pai : countMap.keySet()){
            int c = countMap.get(pai);
            if(c == 4){
                gameSeat.setCanGang(true);
                gameSeat.getGangPai().add(pai);
            }
	    }
	}

	/**
	 * 检测弯杠（自己摸了已经碰掉的牌）
	 * @param game
	 * @param gameSeat
	 * @param targetPai
	 */
	private void checkCanWanGang(Game game,GameSeat gameSeat,int targetPai){
//	    //如果没有牌了，则不能再杠
//	    if(game.mahjongs.length <= game.currentIndex){
//	        return;
//	    }

	    //从碰过的牌中选
	    for(int i = 0; i < gameSeat.getPengs().size(); ++i){
	        int pai = gameSeat.getPengs().get(i);
	        if(pai == targetPai){
	            gameSeat.setCanGang(true);
	            gameSeat.getGangPai().add(pai);
	        }
	    }
	}

	/**
	 * 检测胡牌
	 * @param game
	 * @param gameSeat
	 * @param targetPai
	 */
	private void checkCanHu(Game game,GameSeat gameSeat,int targetPai) {
		Object[] extensionArray = MercuryExtensionPlatform.getExtensionInstance(ITingExtension.class.getName());
		if (extensionArray != null && extensionArray.length > 0) {
			for (Object obj : extensionArray) {
				try{
					ITingExtension extension = (ITingExtension) obj;
					boolean canHu = extension.check(gameSeat, targetPai);
					if(canHu){
						gameSeat.setCanHu(true);
					}
				}catch(Exception e){
					logger.error("shutDown error method",e);
				}
			}
		}
	}

	/**
	 * 删除某个玩家动作（碰，杠，胡...)
	 * @param gameSeat
	 */
	private void clearAllOptions(GameSeat gameSeat){
		gameSeat.setCanPeng(false);
		gameSeat.setCanGang(false);
		gameSeat.getGangPai().clear();
		gameSeat.setCanHu(false);
		gameSeat.setLastFangGangSeat(-1);    
	}
	/**
	 * 删除桌子全部动作
	 * @param game
	 */
	private void clearAllOptions(Game game){
		for(TableSeat tableSeat : game.getTableSeats()){
            clearAllOptions(tableSeat.getGameSeat());
        }
	}

	/**
	 * 是否有动作
	 * @param gameSeat
	 * @return
	 */
	private boolean hasOperations(GameSeat gameSeat){
	    if(gameSeat.isCanGang() || gameSeat.isCanPeng() || gameSeat.isCanHu()){
	        return true;
	    }
	    return false;
	}

	/**
	 * 发送动作
	 * @param game
	 * @param gameSeat
	 * @param pai
	 */
	private void sendOperations(Game game,GameSeat gameSeat,int pai) {
	    if(hasOperations(gameSeat)){
	        if(pai == -1){
	            pai = gameSeat.getHolds().get(gameSeat.getHolds().size() - 1);
	        }
	        
	        Map<String, Object> retData = new HashMap<String, Object>();
	        retData.put("pai", pai);
	        retData.put("hu", gameSeat.isCanHu());
	        retData.put("peng", gameSeat.isCanPeng());
	        retData.put("gang", gameSeat.isCanGang());
	        retData.put("gangpai", gameSeat.getGangPai());
	        retData.put("si" ,gameSeat.getIndex());

	        //如果可以有操作，则进行操作
	        userService.sendMsg(gameSeat.getTableSeat().getUserId(),"game_action_push",retData);
	    }
	    else{
	    	userService.sendMsg(gameSeat.getTableSeat().getUserId(),"game_action_push",null);
	    }
	}

	/**
	 * 轮到下一个玩家
	 * @param game
	 */
	private void moveToNextUser(Game game){
		game.moveToNext();
	}
	/**
	 * 轮到指定玩家
	 * @param game
	 * @param nextIndex
	 */
	private void moveToNextUser(Game game , int nextIndex){
		game.setTurn(nextIndex);
	}

	/**
	 * 摸牌
	 * @param game
	 */
	public void doUserMoPai(Game game){
	    game.setChuPai(-1);
	    GameSeat turnSeat = game.getGameSeat(game.getTurn());
	    int pai = mopai(game,game.getTurn());
	    //牌摸完了，结束
	    if(pai == -1){
	        doGameOver(game,turnSeat.getUserId(),false);
	        return;
	    }
	    else{
	        int numOfMJ = game.getMahjongs().size() - game.getCurrentIndex();
	        userService.broacastInRoom("mj_count_push",numOfMJ,turnSeat.getUserId(),true ,game.getRoomTable() );
	    }

	    recordGameAction(game,game.getTurn(),GameAction.ACTION_MOPAI,pai);

	    //通知前端新摸的牌
	    userService.sendMsg(turnSeat.getTableSeat().getUserId(),"game_mopai_push",pai);
	    //检查是否可以暗杠或者胡
	    //检查胡，直杠，弯杠
        checkCanAnGang(game,turnSeat);    
	    
	    //如果未胡牌，或者摸起来的牌可以杠，才检查弯杠
	    if(turnSeat.getHolds().get(turnSeat.getHolds().size()-1) == pai){
	        checkCanWanGang(game,turnSeat,pai);    
	    }
	    

	    //检查看是否可以胡
	    checkCanHu(game,turnSeat,pai);

	    //广播通知玩家出牌方
	    turnSeat.setCanChuPai(true);
	    userService.broacastInRoom("game_chupai_push",turnSeat.getUserId(),turnSeat.getUserId(),true,game.getRoomTable());

	    //通知玩家做对应操作
	    sendOperations(game,turnSeat,game.getChuPai());
	}

	/**
	 * 结算
	 * @param game
	 */
	private void calculateResult(Game game){
		Object[] extensionArray = MercuryExtensionPlatform.getExtensionInstance(ICalculateExtension.class.getName());
		if (extensionArray != null && extensionArray.length > 0) {
			for (Object obj : extensionArray) {
				try{
					ICalculateExtension extension = (ICalculateExtension) obj;
					extension.calculateResult(game);
				}catch(Exception e){
					logger.error(e.toString());
					e.toString();
				}
			}
		}
	}

	/**
	 * 
	 * @param game
	 * @param turnSeat
	 * @param seatData
	 * @param pai
	 * @return
	 */
	private boolean checkCanQiangGang(Game game,GameSeat turnSeat,GameSeat seatData,int pai){
	    boolean hasActions = false;
	    for(TableSeat tableSeat :game.getTableSeats()){
	    	GameSeat cur = tableSeat.getGameSeat();
	        //杠牌者不检查
	        if(seatData == cur){
	            continue;
	        }
	        checkCanHu(game,cur,pai);
	        if(cur.isCanHu()){
	            sendOperations(game,cur,pai);
	            hasActions = true;
	        }
	    }
	    if(hasActions){
	        
	    	QiangGangContext qiangGangContext = new QiangGangContext();
	        qiangGangContext.setTurnSeat(turnSeat);
	        qiangGangContext.setSeatData(seatData);
	        qiangGangContext.setPai(pai);
	        qiangGangContext.setValid(true);
	        game.setQiangGangContext(qiangGangContext);
	    }
	    else{
	        game.setQiangGangContext(null);
	    }
	    return game.getQiangGangContext() != null;
	}
	
	/**
	 * 通知结果
	 * @param game
	 * @param userId
	 * @param isEnd
	 * @param result
	 */
	private void noticeResult(Game game, int userId,boolean isEnd,List<Map<String,Object>> result){
        List<Map<String,Object>> endinfos = new ArrayList<Map<String,Object>>();
        if(isEnd){
            for(int i = 0; i < game.getPlayerNum(); ++i){
                TableSeat rs = game.getGameSeat(i).getTableSeat();
                Map<String,Object> endinfo = new HashMap<String, Object>();
                endinfo.put("numzimo", rs.getNumZiMo());
                endinfo.put("numdianpao", rs.getNumDianPao());
                endinfo.put("numangang", rs.getNumAnGang());
                endinfos.add(endinfo);
            }   
        }
        Map<String,Object> retData = new HashMap<String,Object>();
        retData.put("results", result);
        retData.put("endinfo", endinfos);
        userService.broacastInRoom("game_over_push",retData,userId,true,game.getRoomTable());
        
	}
	
	/**
	 * 游戏结束
	 * @param game
	 * @param userId
	 * @param forceEnd
	 */
	public void doGameOver(Game game,int userId ,boolean forceEnd){
	    List<Map<String,Object>> results = new ArrayList<Map<String,Object>>();
	    int[] dbresult = new int[]{0,0,0,0};

	    if(game != null){
	        if(!forceEnd){
	            calculateResult(game);    
	        }
	        int i = 0;
	        HuPaiInfo huInfo = game.getHuInfo();
	        for(TableSeat tableSeat : game.getTableSeats()){
	        	GameSeat gameSeat = tableSeat.getGameSeat();
	        	
	        	tableSeat.setReady(false);
	            tableSeat.setScore(tableSeat.getScore() +gameSeat.getScore());
	            tableSeat.setNumZiMo(tableSeat.getNumZiMo()+gameSeat.getNumZiMo());
	            tableSeat.setNumDianPao(tableSeat.getNumDianPao() + gameSeat.getNumDianPao());
	            tableSeat.setNumAnGang(tableSeat.getNumAnGang() + gameSeat.getNumAnGang());
	            tableSeat.setNumMingGang(tableSeat.getNumMingGang() + gameSeat.getNumMingGang());

	            Map<String,Object> userRT = new HashMap<String,Object>();
	            userRT.put("userId", tableSeat.getUserId());
	            userRT.put("actions", gameSeat.getActions());
	            userRT.put("pengs", gameSeat.getPengs());
	            userRT.put("wangangs", gameSeat.getWangangs());
	            userRT.put("diangangs", gameSeat.getDiangangs());
	            userRT.put("angangs", gameSeat.getAngangs());
	            userRT.put("holds", gameSeat.getHolds());
	            userRT.put("fan", gameSeat.getFan());
	            userRT.put("score", gameSeat.getScore());
	            userRT.put("totalscore", tableSeat.getScore());
	            userRT.put("huinfo",huInfo );
	            
	            results.add(userRT);


	            dbresult[i] = gameSeat.getScore();
	            i++;
	        }

	        int old = game.getZhuangIndex();
	        if(huInfo.getSeatIndex() != old){
	        	game.setZhuangIndex((old + 1)%game.getPlayerNum());
	        }
	    }
	    boolean isEnd = (game.getRoomTable().getNumOfGames() >= game.getRoomTable().getGameConfig().getMaxQuan());
        noticeResult(game, userId,isEnd,results);
	}
	
	/**
	 * 记录位置事件
	 * @param game
	 * @param gameSeat
	 * @param type
	 * @param target
	 * @return
	 */
	public SeatAction recordUserAction(Game game,GameSeat gameSeat,int type,int... target){
		SeatAction seatAction = new SeatAction();
		seatAction.setType(type);
		if(target != null && target.length > 0){
			for (int targetIndex : target) {
				seatAction.getTargets().add(targetIndex);
			}
		}else{
			for(TableSeat tableSeat : game.getTableSeats()){
	            GameSeat s = tableSeat.getGameSeat();
	            if(gameSeat != s){
	                seatAction.getTargets().add(s.getIndex());
	            }
	        }   
		}
		gameSeat.getActions().add(seatAction);
		return seatAction;
	}
	
	/**
	 * 记录游戏事件
	 * @param game
	 * @param si
	 * @param action
	 * @param pai
	 */
	public void recordGameAction(Game game,int si,int action,int pai){
		GameAction gm = new GameAction(si,action,pai);
		game.getActionList().add(gm);
	}
	
	
	/**
	 * 准备
	 * @param roomTable
	 * @param userId
	 */
	@Override
	public void setReady(RoomTable roomTable, int userId){
		roomService.setReady(userId,true);
		for(int i = 0; i < roomTable.getTableSeats().size(); ++i){
			 TableSeat s = roomTable.getTableSeats().get(i);
             if(!s.isReady() || !userService.isOnline(s.getUserId())){
                 return;
             }
		}
		begin(roomTable);
	}
	
	//开始新的一局
	/**
	 * 开新一局
	 * @param roomTable
	 */
	private void begin(RoomTable roomTable) {
	    List<TableSeat> seats = roomTable.getTableSeats();
	    Game game = roomTable.getGame();
	    game.reset();
	    roomTable.setNumOfGames(roomTable.getNumOfGames() + 1);

	    for(TableSeat tableSeat : seats){
	    	GameSeat gameSeat = tableSeat.getGameSeat();
	    	if(gameSeat == null) {
	    		gameSeat = new GameSeat(tableSeat);
	    		tableSeat.setGameSeat(gameSeat);
	    	}
	        gameSeat.reset();
	        
	    }
	    //洗牌
	    shuffle(game);
	    //发牌
	    deal(game);

	    

	    int numOfMJ = game.getMahjongs().size() - game.getCurrentIndex();

	    for(int i = 0; i < seats.size(); ++i){
	        //开局时，通知前端必要的数据
	        GameSeat s = seats.get(i).getGameSeat();
	        //通知玩家手牌
	        userService.sendMsg(s.getTableSeat().getUserId(),"game_holds_push",s.getHolds());
	        //通知还剩多少张牌
	        userService.sendMsg(s.getUserId(),"mj_count_push",numOfMJ);
	        //通知还剩多少局
	        userService.sendMsg(s.getUserId(),"game_num_push",roomTable.getNumOfGames());
	        //通知游戏开始
	        userService.sendMsg(s.getUserId(),"game_begin_push",game.getZhuangIndex());

	    }
	}
	
	/**
	 * 出牌
	 * @param roomTable
	 * @param gameSeat
	 * @param pai
	 */
	@Override
	public void chuPai(RoomTable roomTable ,User user,int pai){
		GameSeat gameSeat = roomService.getUserSeat(roomTable, user).getGameSeat();
	    Game game = roomTable.getGame();
	    int seatIndex = gameSeat.getTableSeat().getIndex();
	    //如果不该他出，则忽略
	    if(game.getTurn() != seatIndex){
	        logger.info("不是出牌时候出牌：房间号：{},位置：{}",roomTable.getUuid(),seatIndex);
	        return;
	    }

	    if(hasOperations(gameSeat)){
	    	logger.info("plz guo before you chupai.");
	        return;
	    }
	    
	    //从此人牌中扣除
	    int index = gameSeat.getHolds().indexOf(pai);
	    if(index == -1){
	    	logger.info("holds:{} , pai:{}" , gameSeat.getHolds(),pai);
	        return;
	    }
	    
	    gameSeat.setCanChuPai(false);
	    game.setChupaiCnt(game.getChupaiCnt() + 1);
	    
	    gameSeat.getHolds().remove(index);
	    gameSeat.rePaiCount(pai, 1);
	    game.setChuPai(pai);
	    
	    
	    recordGameAction(game,seatIndex,GameAction.ACTION_CHUPAI,pai);
	    Map<String, Object> retChuPaiMap = new HashMap<String,Object>();
	    retChuPaiMap.put("userId", gameSeat.getTableSeat().getUserId());
	    retChuPaiMap.put("pai", pai);
	    userService.broacastInRoom("game_chupai_notify_push",retChuPaiMap,gameSeat.getUserId(),true,roomTable);
	    
	    //检查是否有人要胡，要碰 要杠
	    boolean hasActions = false;
	    for(TableSeat tableSeat : roomTable.getTableSeats()){
	        //玩家自己不检查
	        if(game.getTurn() == tableSeat.getIndex()){
	            continue;
	        }
	        GameSeat ddd = tableSeat.getGameSeat();
	        //未胡牌的才检查杠和碰
            checkCanPeng(game,ddd,pai);
            checkCanDianGang(game,ddd,pai);            

	        checkCanHu(game,ddd,pai);

	        if(hasOperations(ddd)){
	            sendOperations(game,ddd,game.getChuPai());
	            hasActions = true;    
	        }
	    }
	    
	    //如果没有人有操作，则向下一家发牌，并通知他出牌
	    if(!hasActions){
	    	GameTick gameTick = new GameTick();
	    	gameTick.setAction(GameTick.ACTION_GUO);
	    	gameTick.setExcudeTime(System.currentTimeMillis() + 500);
	    	gameTick.setGameSeat(gameSeat);
	    	game.setGameTick(gameTick);
	    }
	}
	@Override
	public void guoTick(Game game,GameTick gameTick) {
		GameSeat gameSeat = gameTick.getGameSeat();
		Map<String,Object> reMap = new HashMap<String,Object>();
		reMap.put("userId", gameSeat.getUserId());
		reMap.put("pai", game.getChuPai());
		userService.broacastInRoom("guo_notify_push",reMap,gameSeat.getUserId(),true,game.getRoomTable());
		gameSeat.getFolds().add(game.getChuPai());
		game.setChuPai(-1);
		moveToNextUser(game);
		doUserMoPai(game);    
	}

	/**
	 * 碰牌
	 * @param roomTable
	 * @param tableSeat
	 */
	@Override
	public void peng(RoomTable roomTable,TableSeat tableSeat){
	    GameSeat seatData = tableSeat.getGameSeat() ;
	    if(seatData == null){
	        logger.info("can't find user game data.");
	        return;
	    }

	    Game game = roomTable.getGame();

	    //如果是他出的牌，则忽略
	    if(game.getTurn() == tableSeat.getIndex()){
	    	logger.info("it's your turn.");
	        return;
	    }

	    //如果没有碰的机会，则不能再碰
	    if(!seatData.isCanPeng()){
	        logger.info("seatData.peng == false");
	        return;
	    }

	    //如果有人可以胡牌，则需要等待
	    int i = game.getTurn();
	    while(true){
	        i = (i + 1)%roomTable.getPlayerNum();
	        if(i == game.getTurn()){
	            break;
	        }
	        else{
	            GameSeat ddd = game.getGameSeat(i);
	            if(ddd.isCanHu() && i != seatData.getIndex()){
	                return;    
	            }
	        }
	    }


	    clearAllOptions(game);

	    //验证手上的牌的数目
	    int pai = game.getChuPai();
	    int c = seatData.getCountMap().get(pai);
	    if( c < 2){
	        logger.error("pai:" + pai + ",count:" + c);
	        return;
	    }

	    for(int k = 0; k < 2; ++k){
	        int index = seatData.getHolds().indexOf(pai);
	        if(index == -1){
	            logger.error("can't find mj.");
	            return;
	        }
	        seatData.getHolds().remove(index);
	        seatData.getCountMap().put(pai,seatData.getPaiCount(pai) -1);
	    }
	    seatData.getPengs().add(pai);
	    game.setChuPai(-1);

	    recordGameAction(game,seatData.getTableSeat().getIndex(),GameAction.ACTION_PENG,pai);

	    Map<String,Object> pengRet = new HashMap<String,Object>();
	    pengRet.put("userid", seatData.getTableSeat().getUserId());
	    pengRet.put("pai", pai);
	    
	    //广播通知其它玩家
	    userService.broacastInRoom("peng_notify_push",pengRet,seatData.getTableSeat().getUserId(),true,roomTable);

	    //碰的玩家打牌
	    moveToNextUser(game,seatData.getIndex());
	    
	    //广播通知玩家出牌方
	    seatData.setCanChuPai(true);
	    userService.broacastInRoom("game_chupai_push",seatData.getTableSeat().getUserId(),seatData.getTableSeat().getUserId(),true,roomTable);
	};
	
	/**
	 * 是否在游戏中
	 * @param roomTable
	 * @return
	 */
	@Override
	public boolean isPlaying(RoomTable roomTable){
	    Game game = roomTable.getGame();
	    if(game.getState() == IGameConstant.GAME_STATE_IDLE){
	        return false;
	    }
	    return true;
	}
	
	/**
	 * 杠牌
	 * @param game
	 * @param turnSeat
	 * @param seatData
	 * @param gangtype
	 * @param numOfCnt
	 * @param pai
	 */
	@Override
	public void doGang(Game game,GameSeat turnSeat,GameSeat seatData,int gangtype,int numOfCnt,int pai){
	    int seatIndex = seatData.getIndex();
	    int gameTurn = turnSeat.getIndex();
	    
	    if(gangtype == IGameConstant.GANG_WAN_GANG){
	        int idx = seatData.getPengs().indexOf(pai);
	        if(idx >= 0){
	            seatData.getPengs().remove(idx);
	        }
	        
	    }
	    //进行碰牌处理
	    //扣掉手上的牌
	    //从此人牌中扣除
	    for(int i = 0; i < numOfCnt; ++i){
	        int index = seatData.getHolds().indexOf(pai);
	        if(index == -1){
	            logger.debug("can't find mj.");
	            return;
	        }
	        seatData.getHolds().remove(index);
	        seatData.rePaiCount(pai,1);
	    }

	    recordGameAction(game,seatData.getIndex(),GameAction.ACTION_GANG,pai);

	    //记录下玩家的杠牌
	    if(gangtype == IGameConstant.GANG_AN_GANG){
	        seatData.getAngangs().add(pai);
	        SeatAction ac = recordUserAction(game,seatData,SeatAction.TYPE_AN_GANG);
	        ac.setScore( game.getConf().getBaseScore()*2);
	    }
	    else if(gangtype == IGameConstant.GANG_DIAN_GANG){
	        seatData.getDiangangs().add(pai);
	        SeatAction ac = recordUserAction(game,seatData,SeatAction.TYPE_DIAN_GANG,gameTurn);
	        ac.setScore(game.getConf().getBaseScore()*2);
	        GameSeat fs = turnSeat;
	        recordUserAction(game,fs,SeatAction.TYPE_FANG_GANG,seatIndex);
	    }
	    else if(gangtype == IGameConstant.GANG_WAN_GANG){
	        seatData.getWangangs().add(pai);
            recordUserAction(game,seatData,SeatAction.TYP_ZHUAN_SHOU_GANG);

	    }

	    Map<String, Object> gangReMap = new HashMap<String,Object>();
	    gangReMap.put("userid", seatData.getUserId());
	    //通知其他玩家，有人杠了牌
	    Map<String, Object> gangRet = new HashMap<String,Object>();
	    userService.broacastInRoom("gang_notify_push",gangRet,seatData.getUserId(),true,game.getRoomTable());

	}
	
	/**
	 * 杠
	 * @param roomTable
	 * @param seatData
	 * @param pai
	 */
	@Override
	public void gang(RoomTable roomTable,TableSeat tableSeat ,int pai){
		GameSeat seatData = tableSeat.getGameSeat();
	    int seatIndex = seatData.getIndex();
	    Game game = roomTable.getGame();

	    //如果没有杠的机会，则不能再杠
	    if(!seatData.isCanGang()) {
	        logger.error("seatData.gang == false");
	        return;
	    }
	    
	    int numOfCnt = seatData.getPaiCount(pai);

	    if(seatData.getGangPai().indexOf(pai) == -1){
	        logger.error("the given pai can't be ganged.");
	        return;   
	    }
	    
	    //如果有人可以胡牌，则需要等待
	    int i = game.getTurn();
	    while(true){
	        i = (i + 1)%4;
	        if(i == game.getTurn()){
	            break;
	        }
	        else{
	        	GameSeat ddd = game.getGameSeat(i);
	            if(ddd.isCanHu() && i != seatIndex){
	                return;    
	            }
	        }
	    }

	    

	    int gangtype = -1;
	    //弯杠 去掉碰牌
	    if(numOfCnt == 1){
	        gangtype = IGameConstant.GANG_WAN_GANG;
	    }
	    else if(numOfCnt == 3){
	        gangtype = IGameConstant.GANG_DIAN_GANG;
	    }
	    else if(numOfCnt == 4){
	        gangtype = IGameConstant.GANG_AN_GANG;
	    }
	    else{
	        logger.debug("invalid pai count.");
	        return;
	    }
	    
	    game.setChuPai(-1);
	    clearAllOptions(game);
	    seatData.setCanChuPai(false);
	    
	    userService.broacastInRoom("hangang_notify_push",seatIndex,seatData.getUserId(),true,roomTable);
	    
	    //如果是弯杠，则需要检查是否可以抢杠
	    GameSeat turnSeat = game.getGameSeat(game.getTurn());
	    if(numOfCnt == 1){
	        boolean canQiangGang = checkCanQiangGang(game,turnSeat,seatData,pai);
	        if(canQiangGang){
	            return;
	        }
	    }
	    
	    doGang(game,turnSeat,seatData,gangtype,numOfCnt,pai);
	}
	
	/**
	 * 胡牌
	 * @param roomTable
	 * @param tableSeat
	 */
	@Override
	public void hu(RoomTable roomTable , TableSeat tableSeat){
	    GameSeat seatData = tableSeat.getGameSeat();
	    if(seatData == null){
	        logger.error("can't find user game data.");
	        return;
	    }

	    int seatIndex = seatData.getIndex();
	    Game game = roomTable.getGame();

	    //不能胡牌
	    if(!seatData.isCanHu()){
	        logger.debug("invalid request.");
	        return;
	    }

	    //标记为和牌
	    seatData.setHued(true);
	    int hupai = game.getChuPai();
	    if( hupai == -1 ){
	    	hupai = seatData.getHolds().get(seatData.getHolds().size() - 1);
	    }
	    boolean isZimo = false;

	    GameSeat turnSeat = game.getGameSeat(game.getTurn());
	    
	    HuPaiInfo huPaiInfo = new HuPaiInfo();
	    game.setHuInfo(huPaiInfo);
	    huPaiInfo.setHuPai(true);
	    int fan = 0;
	    
//	    QiangGangContext qiangGangContext = game.getQiangGangContext();
//	    if(qiangGangContext != null){
//	        hupai = qiangGangContext.getPai();
//	        GameSeat gangSeat = qiangGangContext.getSeatData();
//	        huPaiInfo.setZimo(false);
//	        huPaiInfo.setAction(HuPaiInfo.ACTION_QIANG_GANG_HU);
//	        huPaiInfo.setQiangGangHu(true);
//	        huPaiInfo.setTarget(gangSeat.getIndex());
//	        huPaiInfo.setPai(hupai);
//	        
//	        recordGameAction(game,seatIndex,GameAction.ACTION_HU,hupai);
//	        game.getQiangGangContext().setValid(false);
//	        
//	        int idx = gangSeat.getHolds().indexOf(hupai);
//	        if(idx != -1){
//	            gangSeat.getHolds().remove(idx);
//	            gangSeat.rePaiCount(hupai, 1);
//	            userService.sendMsg(gangSeat.getUserId(),"game_holds_push",gangSeat.getHolds());
//	        }
//	        
//	        HuPaiInfo gangHupaiInfo = gangSeat.getHuInfo();
//	        gangHupaiInfo.setAction(HuPaiInfo.ACTION_BEI_QIANG_GANG);
//	        gangHupaiInfo.setTarget(seatIndex);
//	    }else 
	    if(game.getChuPai() == -1){
	        hupai = seatData.getHolds().get(seatData.getHolds().size() - 1);
	        seatData.rePaiCount(hupai,1);
	        huPaiInfo.setPai(hupai);
	        if(huPaiInfo.isGangHu()){
	            if(turnSeat.getLastFangGangSeat() == seatIndex){
	                huPaiInfo.setAction(HuPaiInfo.ACTION_GANG_HUA);
	                huPaiInfo.setZimo(true);
	                fan ++;
	            }
	            else{
	                huPaiInfo.setAction(HuPaiInfo.ACTION_DIAN_GANG_HUA);
	                huPaiInfo.setZimo(true);
	                huPaiInfo.setTarget(turnSeat.getLastFangGangSeat());
	            }
	        }
	        else{
	            huPaiInfo.setAction(HuPaiInfo.ACTION_ZI_MO);
	            huPaiInfo.setZimo(true);
	        }

	        isZimo = true;
	        recordGameAction(game,seatIndex,GameAction.ACTION_ZIMO,hupai);
	    }
	    else{
	        huPaiInfo.setPai(hupai);
	        
	        if(turnSeat.getLastFangGangSeat() >= 0 ){
	        	huPaiInfo.setAction(HuPaiInfo.ACTION_TYP_GANG_PAO_HU);
	        }else{
	        	huPaiInfo.setAction(HuPaiInfo.ACTION_TYPE_HU);
	        }   
	        
	        huPaiInfo.setZimo(false);
	        huPaiInfo.setTarget(game.getTurn());

	        recordGameAction(game,seatIndex,GameAction.ACTION_HU,hupai);
	    }

	    //保存番数
	    TingPai ti = seatData.getTingPai();
	    huPaiInfo.setFan(ti.getFan() + fan);
	    huPaiInfo.setPattern(ti.getPattern());
	    huPaiInfo.setZimo(isZimo);
	    clearAllOptions(seatData);

	    //通知前端，有人和牌了
	    Map<String, Object> huReMap = new HashMap<String,Object>();
	    huReMap.put("seatindex", seatIndex);
	    huReMap.put("iszimo", isZimo);
	    huReMap.put("hupai", hupai);
	    
	    userService.broacastInRoom("hu_push",huReMap,seatData.getUserId(),true,roomTable);
	    //清空所有非胡牌操作
	    for(TableSeat tSeat : game.getTableSeats()){
	        GameSeat ddd = tSeat.getGameSeat();
	        ddd.setCanPeng(false);
	        ddd.setCanGang(false);
	        ddd.setCanChuPai(false);
	        sendOperations(game,ddd,hupai);
	    }
	    
	    doGameOver(game,seatData.getUserId(),false);
	}
	/**
	 * 过
	 * @param roomTable
	 * @param tableSeat
	 */
	@Override
	public void guo(RoomTable roomTable,TableSeat tableSeat){
	    GameSeat seatData = tableSeat.getGameSeat();
	    if(seatData == null){
	        logger.error("can't find user game data.");
	        return;
	    }

	    int seatIndex = seatData.getIndex();
	    Game game = roomTable.getGame();

	    //如果玩家没有对应的操作，则也认为是非法消息
	    if((seatData.isCanGang() || seatData.isCanPeng() || seatData.isCanHu()) == false){
	        logger.error("no need guo.");
	        return;
	    }

	    //如果是玩家自己的轮子，不是接牌，则不需要额外操作
	    boolean doNothing = game.getChuPai() == -1 && game.getTurn() == seatIndex;

	    userService.sendMsg(seatData.getUserId(),"guo_result",null);
	    clearAllOptions(seatData);
	    
	    if(doNothing){
	    	return;
	    }
	    
	    //如果还有人可以操作，则等待
	    for(int i = 0; i < game.getPlayerNum(); ++i){
	        GameSeat ddd = game.getGameSeat(i);
	        if(hasOperations(ddd)){
	            return;
	        }
	    }

	    //如果是已打出的牌，则需要通知。
	    if(game.getChuPai() >= 0){
	        int uid = game.getGameSeat(game.getTurn()).getUserId();
	        Map<String,Object> guoRetMap = new HashMap<String,Object>();
	        guoRetMap.put("userId", uid);
	        guoRetMap.put("pai", game.getChuPai());
	        userService.broacastInRoom("guo_notify_push",guoRetMap,seatData.getUserId(),true,roomTable);
	        seatData.getFolds().add(game.getChuPai());
	        game.setChuPai(-1);
	    }
	    //清除所有的操作
	    clearAllOptions(game);
	    
        //下家摸牌
        moveToNextUser(game);
        doUserMoPai(game);   
	}

}