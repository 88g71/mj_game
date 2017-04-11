package com.naqi.mj.model;

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
	private int score;
	private GameSeat gameSeat;
	//-------------------------
	//每局信息
	private boolean ready;
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

	public GameSeat getGameSeat() {
		return gameSeat;
	}

	public void setGameSeat(GameSeat gameSeat) {
		this.gameSeat = gameSeat;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
}
