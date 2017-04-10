package com.naqi.process;

import java.util.Map;

import javax.annotation.Resource;

import org.apache.mina.core.session.IoSession;
import org.springframework.stereotype.Service;

import com.naqi.mj.model.RoomTable;
import com.naqi.mj.service.dq.ITdhService;
import com.naqi.model.User;
import com.naqi.msg.MsgConstant;
import com.naqi.service.IProcessExtension;
import com.naqi.service.IRoomService;
@Service(value="PengProcess")
public class PengProcess implements IProcessExtension{
	private static final String CMD = "peng";
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
				tdhService.peng(roomTable,roomService.getUserSeat(roomTable, user));
			}
			return true;
		}
		return false;
	}

}
