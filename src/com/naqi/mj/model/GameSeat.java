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
public class GameSeat {
	//-------------------------
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
//    //玩家听牌，用于快速判定胡了的番数
//    private Map<Integer,TingPai> tingMap = new HashMap<Integer,TingPai>();
    private TingPai tingPai = new TingPai(0, "", -1, 0);

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
//    private HuPaiInfo huInfo = new HuPaiInfo();
    
    private int lastFangGangSeat = -1;
    //------------------------------------------------------------
    //统计信息
    private int numZiMo = 0;
    private int numDianPao = 0;
    private int numAnGang = 0;
    private int numMingGang = 0;
//-----------------------------------------------------------------
    private TableSeat tableSeat;
    public GameSeat(TableSeat tableSeat){
    	this.setTableSeat(tableSeat);
    }
    /**
     * 减数量
     * @param pai
     * @param reNum 要减去的数量
     * @return
     */
    public void rePaiCount(int pai , int reNum){
    	int num = getPaiCount(pai);
    	num -= reNum;
    	if(num <= 0){
    		countMap.remove(pai);
		}else{
			countMap.put(pai, num);
		}
    }
    /**
     * 减数量
     * @param pai
     * @param addNum 要减去的数量
     * @return
     */
    public void addPaiCount(int pai , int addNum){
    	int num = getPaiCount(pai);
    	num += addNum;
		countMap.put(pai, num);
    }
    
    
	public void removeHoldsLast(){
		holds.remove(holds.size() - 1);
		System.out.println(getIndex() +"--22--" + holds.size());
	}

	public void removeHolds(int index){
		holds.remove(index);
		System.out.println(getIndex() +"--223--" + holds.size());
	}
	
	public void addHolds(int index , int pai){
		if(pai == -1){
			System.out.println();
		}
		
		holds.add(index, pai);
		System.out.println(getIndex() +"--2--" + holds.size());
	}
	public void addholds(int pai){
		if(pai == -1){
			System.out.println();
		}
		holds.add(pai);
		System.out.println(getIndex() +"--1--" + holds.size());
	}

	public List<Integer> getHoldsPai(){
		return this.holds;
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
//	public  HuPaiInfo  getHuInfo() {
//		return huInfo;
//	}
//	public void setHuInfo(HuPaiInfo huInfo) {
//		this.huInfo = huInfo;
//	}
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

//	public Map<Integer,TingPai> getTingMap() {
//		return tingMap;
//	}
//
//	public void setTingMap(Map<Integer,TingPai> tingMap) {
//		this.tingMap = tingMap;
//	}

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

	public TableSeat getTableSeat() {
		return tableSeat;
	}

	public void setTableSeat(TableSeat tableSeat) {
		this.tableSeat = tableSeat;
	}

	public int getIndex() {
		return this.tableSeat.getIndex();
	}
	
	public int getUserId(){
		return this.getTableSeat().getUserId();
	}
	public TingPai getTingPai() {
		return tingPai;
	}
	public void setTingPai(TingPai tingPai) {
		this.tingPai = tingPai;
	}
	public int getHoldLast() {
		return holds.get(holds.size() - 1);
	}
	/**
	 * 重置
	 */
	public void reset(){
		//持有的牌
	    holds.clear();
	    //打出的牌
	    folds.clear();
	    //暗杠的牌
	    angangs.clear();
	    //点杠的牌
	    diangangs.clear();
	    //明杠的牌
	    wangangs.clear();
	    //碰了的牌
	    pengs.clear();
	    //玩家手上的牌的数目，用于快速判定碰杠
	    countMap.clear();
//	    //玩家听牌，用于快速判定胡了的番数
//	    private Map<Integer,TingPai> tingMap = new HashMap<Integer,TingPai>();
	    tingPai = new TingPai(0, "", -1, 0);

	    //是否可以杠
	    canGang = false;
	    //用于记录玩家可以杠的牌
	    gangPai.clear();
	    //是否可以碰
	    canPeng = false;
	    //是否可以胡
	    canHu = false;
	    //是否可以出牌
	    canChuPai = false;
	    //是否胡了
	    hued = false;
	    //
	    actions.clear();

	    //是否是自摸
	    iszimo = false;
	    isGangHu = false;
	    fan = 0;
	    score = 0;
//	    private HuPaiInfo huInfo = new HuPaiInfo();
	    
	    lastFangGangSeat = -1;
	    //------------------------------------------------------------
	    //统计信息
	    numZiMo = 0;
	    numDianPao = 0;
	    numAnGang = 0;
	    numMingGang = 0;
	}
}
