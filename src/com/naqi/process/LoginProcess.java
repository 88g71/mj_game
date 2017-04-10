package com.naqi.process;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.mina.core.session.IoSession;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.naqi.service.IUserService;
import com.naqi.mj.model.RoomTable;
import com.naqi.mj.model.TableSeat;
import com.naqi.mj.service.dq.ITdhService;
import com.naqi.model.User;
import com.naqi.msg.MsgConstant;
import com.naqi.service.IConfigService;
import com.naqi.service.IProcessExtension;
import com.naqi.service.IRoomService;
import com.naqi.util.MD5;
import com.naqi.util.NetUtil;
@Service(value="LoginProcess")
public class LoginProcess implements IProcessExtension{
	private static final String CMD = "login";
	private static final String CMD_RE = "login_result";
	@Resource
	private IUserService userService;
	@Resource
	private IRoomService roomService;
	@Resource
	private IConfigService configService;
	@Resource
	private ITdhService tdhService;
	
	@Override
	public boolean process(Map<String, Object> allMsg) {
		if(CMD.equals(allMsg.get(MsgConstant.MSG_KEY_CMD))){
			IoSession session = (IoSession) allMsg.get(MsgConstant.MSG_KEY_SESSION);
			if(session == null || !session.isConnected()) return true;
			User user = (User) session.getAttribute(MsgConstant.SESSION_KEY_USER);
			if(user!=null){
				return true;
			}
			Map<String, Object> msg = (Map<String, Object>) allMsg.get(MsgConstant.MSG_KEY_DATA);
			String token = msg.get("token") == null ? "":msg.get("token").toString();
			String roomId = msg.get("roomid") == null ? "":msg.get("roomid").toString();
			long time = Long.parseLong(msg.get("time") == null ? "":msg.get("time").toString());
			String sign = msg.get("sign") == null ? "":msg.get("sign").toString();
			Map<String,Object> loginRet = new HashMap<String,Object>();
			//检查参数合法性
			if("".equals(token)|| "".equals(roomId) || time == 0){
				loginRet.put("errcode", 1);
				NetUtil.sendMsg(session, 0,CMD_RE, loginRet);
				return true;
			}
//			//检查参数是否被篡改
			String md5 = MD5.getMD5(roomId + token + time + configService.getRoomPriKey());
//			if(!md5.equals(sign)){
//				NetUtil.sendMsg(session, 0, CMD_RE,2, "login failed. invalid sign!");
//				return true;
//			}
			//检查token是否有效
			if(!userService.isTokenValid(token)){
				loginRet.put("errcode", 3);
				NetUtil.sendMsg(session, 0, CMD_RE,loginRet);
				return true;
			}
			//检查房间合法性
			user = userService.getUserByToken(token);
			RoomTable gameTable = roomService.getUserGameTable(user);

			userService.login(user, token , session);
			session.setAttribute(MsgConstant.SESSION_KEY_USER, user);
			session.setAttribute(MsgConstant.SESSION_KEY_GAME_TABLE,gameTable);

			TableSeat tableSeat = roomService.getUserSeat(gameTable,user);
			tableSeat.setIp(session.getRemoteAddress().toString());

			Map<String,Object> newUserData = new HashMap<String,Object>();
			List<Map<String,Object>> seatsListData = new ArrayList<Map<String,Object>>();
			List<TableSeat> gameSeatsList = gameTable.getTableSeats();
			for (int i = 0 ; i < gameSeatsList.size() ; i++) {
				Map<String,Object> seatsData = new HashMap<String,Object>();
				TableSeat currentSeat = gameSeatsList.get(i);
				boolean online = userService.isOnlineUser(currentSeat.getUserId());
				seatsData.put("userid", currentSeat.getUserId());
				seatsData.put("ip", currentSeat.getIp());
				seatsData.put("score", currentSeat.getUserScore());
				seatsData.put("name", currentSeat.getUserName());
				seatsData.put("online",online);
				seatsData.put("ready", currentSeat.isReady());
				seatsData.put("seatindex", i);
				if(currentSeat.getUserId() == user.getId()){
					newUserData = seatsData;
				}
				seatsListData.add(seatsData);
			}
			Map<String , Object> retMap = new HashMap<String,Object>();
			retMap.put("roomid", gameTable.getId());
			retMap.put("conf", gameTable.getConf());
			retMap.put("numofgames", gameTable.getNumOfTurns());
			retMap.put("seats", seatsListData);
			retMap.put("errcode", 0);
			NetUtil.sendMsg(session, user.getId(), CMD_RE, retMap);

			//通知其它客户端
			userService.sendMsg2GameTable("new_user_comes_push",newUserData,gameTable);	
			tdhService.setReady(gameTable, user.getId());
			NetUtil.sendMsg(session, user.getId(),"login_finished");
			
			return true;
		}
		return false;
	}

}
