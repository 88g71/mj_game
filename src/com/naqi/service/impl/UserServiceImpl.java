package com.naqi.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.apache.mina.core.session.IoSession;
import org.springframework.stereotype.Service;

import com.naqi.mapper.UserMapper;
import com.naqi.mj.model.RoomTable;
import com.naqi.mj.model.TableSeat;
import com.naqi.model.TokenInfo;
import com.naqi.model.User;
import com.naqi.service.IUserService;
import com.naqi.service.OnlineUser;
import com.naqi.util.MD5;
import com.naqi.util.NetUtil;
@Service(value="userService")
public class UserServiceImpl implements IUserService{
	@Resource
	private UserMapper userMapper;
	//在线用户列表
	private Map<Integer,OnlineUser> onlineUserId = new ConcurrentHashMap<Integer,OnlineUser>();
	private Map<String, TokenInfo> tokenMap = new ConcurrentHashMap<String, TokenInfo>();
	private Map<Integer, User> userMap = new ConcurrentHashMap<Integer, User>();

	@Override
	public User getUserById(int id) {
		User user = userMap.get(id);
		if(user != null) return user;
		user = userMapper.findById(id);
		if(user != null){
			userMap.put(id, user);
		}
		return user;
	}

	@Override
	public void initUser(User user,String token) {
		OnlineUser onlineUser = onlineUserId.get(user.getId());
		if(onlineUser == null){
			onlineUser = new OnlineUser();
		}
		onlineUser.setUserId(user.getId());
		onlineUser.setToken(token);
		onlineUserId.put(user.getId(), onlineUser);
		
		TokenInfo tokenInfo = new TokenInfo();
		tokenInfo.setDate(System.currentTimeMillis());
		tokenInfo.setToken(token);
		tokenInfo.setUserId(user.getId());
		tokenMap.put(token, tokenInfo);
	}

	@Override
	public void loginOut(User user) {
		OnlineUser onlineUser = onlineUserId.get(user.getId());
		onlineUserId.remove(user.getId());
		tokenMap.remove(onlineUser.getToken());
	}
	@Override
	public void login(User user , String token , IoSession session) {
		OnlineUser onlineUser = onlineUserId.get(user.getId());
		if(onlineUser == null){
			onlineUser = new OnlineUser();
		}
		onlineUser.setUserId(user.getId());
		onlineUser.setToken(token);
		onlineUser.setConn(session);
		onlineUserId.put(user.getId(), onlineUser);
	}
	
	@Override
	public User getUserByToken(String token) {
		TokenInfo tokenInfo = tokenMap.get(token);
		User user = tokenInfo == null ? null : getUserById(tokenInfo.getUserId());
		return user;
	}

	@Override
	public boolean isOnlineUser(int userId) {
		OnlineUser onlineUser = onlineUserId.get(userId);
		return onlineUser == null ? false : true;
	}

	@Override
	public void sendMsg2GameTable(String cmd,
			Map<String, Object> newUserData, RoomTable roomTable) {
		for (TableSeat tableSeat : roomTable.getTableSeats()) {
			OnlineUser onlineUser = onlineUserId.get(tableSeat.getUserId());
			if(onlineUser != null){
				IoSession session = onlineUser.getConn();
				if(session != null && session.isConnected()){
					NetUtil.sendMsg(session, onlineUser.getUserId(), cmd,  newUserData);
				}
			}
		}
	}

	@Override
	public void broacastInRoom(String cmd, Object data,
			int sender, boolean sendSender ,RoomTable roomTable) {

	    for(TableSeat tableSeat : roomTable.getTableSeats()){
	        //如果不需要发给发送方，则跳过
	    	int userId = tableSeat.getUserId();
	        if(userId == 0 || (tableSeat.getUserId() == sender && sendSender != true)){
	            continue;
	        }
	        OnlineUser onlineUser = onlineUserId.get(userId);
	        if(onlineUser != null && onlineUser.isOnline()){
	        	if(data!=null){
	        		NetUtil.sendMsg(onlineUser.getConn(), userId, cmd,data);
	        	}else{
	        		NetUtil.sendMsg(onlineUser.getConn(), userId, cmd, 0);
	        	}
	        }
	    }
	}

	@Override
	public void sendMsg(int userId, String cmd, Object retData) {
		OnlineUser onlineUser = onlineUserId.get(userId);
        if(onlineUser != null && onlineUser.isOnline()){
        	NetUtil.sendMsg(onlineUser.getConn(), userId, cmd ,retData);
        }
		
	}


	public boolean isTokenValid(String token){
		TokenInfo info = tokenMap.get(token);
		if(info == null){
			return false;
		}
//		if(info.getDate() + TokenInfo.LIFE_TIME < System.currentTimeMillis()){
//			tokenMap.remove(token);
//			return false;
//		}
		return true;
	}

	@Override
	public boolean isOnline(int userId) {
		OnlineUser onlineUser = onlineUserId.get(userId);
		return onlineUser == null ? false : true;
	}

	@Override
	public String createToken(User user, int lifeTime) {
		String token = MD5.getMD5(user.getId()+"a"+System.currentTimeMillis());
		TokenInfo tokenInfo = new TokenInfo();
		tokenInfo.setDate(lifeTime);
		tokenInfo.setToken(token);
		tokenInfo.setUserId(user.getId());
		tokenMap.put(token, tokenInfo);
		return token;
	}
}
