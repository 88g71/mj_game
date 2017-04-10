package com.naqi.process;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.mina.core.session.IoSession;
import org.springframework.stereotype.Service;

import com.naqi.service.IUserService;
import com.naqi.mj.model.RoomTable;
import com.naqi.mj.service.dq.ITdhService;
import com.naqi.model.User;
import com.naqi.msg.MsgConstant;
import com.naqi.service.IProcessExtension;
import com.naqi.service.IRoomService;
@Service(value="ReadyProcess")
public class ReadyProcess implements IProcessExtension{
	private static final String CMD = "ready";
	private static final String CMD_RE = "user_ready_push";
	@Resource
	private IUserService userService;
	@Resource
	private IRoomService roomService;
	@Resource
	private ITdhService tdhService;
	
	@Override
	public boolean process(Map<String, Object> msg) {
		if(CMD.equals(msg.get(MsgConstant.MSG_KEY_CMD))){
			IoSession session = (IoSession) msg.get(MsgConstant.MSG_KEY_SESSION);
			User user = (User) session.getAttribute(MsgConstant.SESSION_KEY_USER);
			if(user != null){
				RoomTable roomTable = roomService.getUserGameTable(user);
				tdhService.setReady(roomTable,user.getId());
				Map<String,Object> reMap = new HashMap<String,Object>();
				reMap.put("userid", user.getId());
				reMap.put("ready", true);
				userService.broacastInRoom(CMD_RE,reMap,user.getId(),true,roomTable);
			}
			return true;
		}
		return false;
	}

}
