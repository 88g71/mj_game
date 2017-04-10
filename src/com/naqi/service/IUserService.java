package com.naqi.service;

import java.util.List;
import java.util.Map;

import org.apache.mina.core.session.IoSession;

import com.naqi.mj.model.RoomTable;
import com.naqi.model.User;

public interface IUserService {
	
	public static final int PARAM_TYPE_ACCOUNT = 1;
	public static final int PARAM_TYPE_MACHINE_CODE = 2;
	public static final int PARAM_TYPE_WX_OPEN_ID = 3;
	/**
	 * 获取用户
	 * @param type
	 * @param code
	 * @return
	 */
	public User getUserById(int id) ;
	/**
	/**
	 * 用户登录
	 * @param user
	 */
	public void initUser(User user,String token) ;
	/**
	 * 用户登出
	 * @param user
	 */
	public void loginOut(User user);
	/**
	 * 根据token获取用户
	 * @param token
	 * @return
	 */
	public User getUserByToken(String token);
	/**
	 * 是否在线
	 * @param userId
	 * @return
	 */
	public boolean isOnlineUser(int userId);
	/**
	 * 给桌子发消息
	 * @param string
	 * @param newUserData
	 * @param id 除了某个id
	 */
	public void sendMsg2GameTable(String cmd,
			Map<String, Object> newUserData, RoomTable roomTable );
	
	public boolean isTokenValid(String token);
	
	public void broacastInRoom(String cmd, Object data,
			int sender, boolean sendSender,RoomTable roomTable);
	
	public void sendMsg(int userId, String string, Object object);
	public boolean isOnline(int userId);
	public String createToken(User user, int i);
	public void login(User user, String token, IoSession session);
	
}
