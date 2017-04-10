package com.naqi.mj.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 游戏座位
 * @author hanfeng
 *
 */
public class TableSeat {
	//基本信息
	private int userId;
	private String userIcon;
	private String userName;
	private int userScore;
	private String ip;
	private int index;			//位置
	private boolean online;		//在线
	private RoomTable roomTable;
	
	private GameSeat gameSeat;
	//-------------------------
	//每局信息
	private boolean ready;
    //持有的牌
    private List<Integer> holds = new ArrayList<Integer>();
    //打出的牌
    private List<Integer> folds = new ArrayList<Integer>();
    //暗杠的牌
    private List<Integer> angangs = new ArrayList<Integer>();
    //点杠的牌
    private List<Integer> diangangs = new ArrayList<Integer>();
    //明杠的牌
    private List<Integer> wangangs = new ArrayList<Integer>();
    //碰了的牌
    private List<Integer> pengs = new ArrayList<Integer>();
    //玩家手上的牌的数目，用于快速判定碰杠
    private Map<Integer,Integer> countMap = new HashMap<Integer,Integer>();
    //玩家听牌，用于快速判定胡了的番数
    private Map<Integer,TingPai> tingMap = new HashMap<Integer,TingPai>();
    //data.pattern = "";

    //是否可以杠
    private boolean canGang = false;
    //用于记录玩家可以杠的牌
    private List<Integer> gangPai = new ArrayList<Integer>();
    //是否可以碰
    private boolean canPeng = false;
    //是否可以胡
    private boolean canHu = false;
    //是否可以出牌
    private boolean canChuPai = false;
    //是否胡了
    private boolean hued = false;
    //
    private List<SeatAction> actions = new ArrayList<SeatAction>();

    //是否是自摸
    private boolean iszimo = false;
    private boolean isGangHu = false;
    private int fan = 0;
    private int score = 0;
    private List<HuPaiInfo> huInfo = new ArrayList<HuPaiInfo>();
    
    private int lastFangGangSeat = -1;
    //------------------------------------------------------------
    //统计信息
    private int numZiMo = 0;
    private int numDianPao = 0;
    private int numAnGang = 0;
    private int numMingGang = 0;
//-----------------------------------------------------------------
    public TableSeat(RoomTable roomTable,int index){
    	this.roomTable = roomTable;
    	this.index = index;
    }
	
	public RoomTable getRoomTable() {
		return roomTable;
	}

	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getUserIcon() {
		return userIcon;
	}
	public void setUserIcon(String userIcon) {
		this.userIcon = userIcon;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getUserScore() {
		return userScore;
	}
	public void setUserScore(int userScore) {
		this.userScore = userScore;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public boolean isOnline() {
		return online;
	}
	public void setOnline(boolean online) {
		this.online = online;
	}
	public boolean isReady() {
		return ready;
	}
	public void setReady(boolean ready) {
		this.ready = ready;
	}
	public List<Integer> getHolds() {
		return holds;
	}
	public void setHolds(List<Integer> holds) {
		this.holds = holds;
	}
	public List<Integer> getFolds() {
		return folds;
	}
	public void setFolds(List<Integer> folds) {
		this.folds = folds;
	}
	public List<Integer> getAngangs() {
		return angangs;
	}
	public void setAngangs(List<Integer> angangs) {
		this.angangs = angangs;
	}
	public List<Integer> getDiangangs() {
		return diangangs;
	}
	public void setDiangangs(List<Integer> diangangs) {
		this.diangangs = diangangs;
	}
	public List<Integer> getPengs() {
		return pengs;
	}
	public void setPengs(List<Integer> pengs) {
		this.pengs = pengs;
	}
	public Map<Integer, Integer> getCountMap() {
		return countMap;
	}
	/**
	 * 获取同一花色牌几张
	 * @param pai
	 * @return
	 */
	public int getPaiCount(int pai) {
		Integer num = countMap.get(pai);
		return num == null ? 0 : num;
	}
	public void setCountMap(Map<Integer, Integer> countMap) {
		this.countMap = countMap;
	}
	public boolean isCanGang() {
		return canGang;
	}
	public void setCanGang(boolean canGang) {
		this.canGang = canGang;
	}
	public List<Integer> getGangPai() {
		return gangPai;
	}
	public void setGangPai(List<Integer> gangPai) {
		this.gangPai = gangPai;
	}
	public boolean isCanPeng() {
		return canPeng;
	}
	public void setCanPeng(boolean canPeng) {
		this.canPeng = canPeng;
	}
	public boolean isCanHu() {
		return canHu;
	}
	public void setCanHu(boolean canHu) {
		this.canHu = canHu;
	}
	public boolean isCanChuPai() {
		return canChuPai;
	}
	public void setCanChuPai(boolean canChuPai) {
		this.canChuPai = canChuPai;
	}
	public boolean isHued() {
		return hued;
	}
	public void setHued(boolean hued) {
		this.hued = hued;
	}
	public boolean isIszimo() {
		return iszimo;
	}
	public void setIszimo(boolean iszimo) {
		this.iszimo = iszimo;
	}
	public boolean isGangHu() {
		return isGangHu;
	}
	public void setGangHu(boolean isGangHu) {
		this.isGangHu = isGangHu;
	}
	public int getFan() {
		return fan;
	}
	public void setFan(int fan) {
		this.fan = fan;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public List<HuPaiInfo> getHuInfo() {
		return huInfo;
	}
	public void setHuInfo(List<HuPaiInfo> huInfo) {
		this.huInfo = huInfo;
	}
	public int getLastFangGangSeat() {
		return lastFangGangSeat;
	}
	public void setLastFangGangSeat(int lastFangGangSeat) {
		this.lastFangGangSeat = lastFangGangSeat;
	}
	public int getNumZiMo() {
		return numZiMo;
	}
	public void setNumZiMo(int numZiMo) {
		this.numZiMo = numZiMo;
	}
	public int getNumDianPao() {
		return numDianPao;
	}
	public void setNumDianPao(int numDianPao) {
		this.numDianPao = numDianPao;
	}
	public int getNumAnGang() {
		return numAnGang;
	}
	public void setNumAnGang(int numAnGang) {
		this.numAnGang = numAnGang;
	}
	public int getNumMingGang() {
		return numMingGang;
	}
	public void setNumMingGang(int numMingGang) {
		this.numMingGang = numMingGang;
	}

	public Map<Integer,TingPai> getTingMap() {
		return tingMap;
	}

	public void setTingMap(Map<Integer,TingPai> tingMap) {
		this.tingMap = tingMap;
	}

	public List<Integer> getWangangs() {
		return wangangs;
	}

	public void setWangangs(List<Integer> wangangs) {
		this.wangangs = wangangs;
	}

	public List<SeatAction> getActions() {
		return actions;
	}

	public void setActions(List<SeatAction> actions) {
		this.actions = actions;
	}

	public GameSeat getGameSeat() {
		return gameSeat;
	}

	public void setGameSeat(GameSeat gameSeat) {
		this.gameSeat = gameSeat;
	}
}
