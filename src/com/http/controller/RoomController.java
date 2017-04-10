package com.http.controller;


import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.naqi.model.User;
import com.naqi.service.IConfigService;
import com.naqi.service.IRoomService;
import com.naqi.service.IUserService;
import com.naqi.service.dto.CreateRoomResult;
import com.naqi.util.MD5;
/**
 * 
 * @author hanfeng
 *
 */
@Controller
@RequestMapping("/room")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RoomController {
	@Resource
	private IConfigService configService;
	@Resource
	private IRoomService roomService;
	@Resource
	private IUserService userService;
	
	@RequestMapping("/create_room")
    @ResponseBody  
    public String createPrivateRoom(HttpServletRequest request) {
		String userIdStr = request.getParameter("userid");
		String sign = request.getParameter("sign");
		String conf = request.getParameter("conf");
		if(userIdStr == null || "".equals(userIdStr)
				||sign == null || "".equals(sign)
				||conf == null || "".equals(conf)){
			
			return ControllerUtils.getSendParma(1,"invalid parameters");
		}
		int userId = Integer.parseInt(userIdStr);
		int tableId = Integer.parseInt(request.getParameter("roomId"));
		int gems = Integer.parseInt(request.getParameter("gems"));

		String md5 = MD5.getMD5(userIdStr + conf + gems + configService.getRoomPriKey());
		if(!md5.equals(sign)){
			return ControllerUtils.getSendParma(1,"sign check failed.");
		}
		User user = userService.getUserById(userId);
		CreateRoomResult result = roomService.createTable(user, conf,gems, tableId);
		return ControllerUtils.getSendParma(result.getErrcode(),"ok");
    }  


	/**
	 * 
	 * @return
	 */
	@RequestMapping("/enter_room")
	@ResponseBody  
	public String enterRoom(HttpServletRequest request) {
		int tableId = Integer.parseInt(request.getParameter("roomId"));
		int userId = Integer.parseInt(request.getParameter("userid"));
		String name =request.getParameter("name");
		String sign = request.getParameter("sign");

		String md5 = MD5.getMD5(userId + name + tableId + configService.getRoomPriKey());
		if(!md5.equals(sign)){
			return ControllerUtils.getSendParma(1,"sign check failed.");
		}
		User user = userService.getUserById(userId);
		int errcode = roomService.enterTable(user, tableId);
		String token = userService.createToken(user,1200000);
		Map<String,Object> reMap = new HashMap<String,Object>();
		reMap.put("token", token);
		return ControllerUtils.getSendParma(errcode, "ok",reMap	);
	}  
	/**
	 * 
	 * @return
	 */
	@RequestMapping("/is_room_runing")
	@ResponseBody  
	public String isRoomRunning(HttpServletRequest request) {
		Map<String,Object> reMap = new HashMap<String,Object>();
		reMap.put("runing", true);
		return ControllerUtils.getSendParma(0, "ok",reMap	);
	}  
	
	
}
