/*
 * File name : Synchronizer.java
 * Created on : 2013-4-5 下午09:41:55
 * Creator : hanfeng
 */
package com.naqi.service.impl;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.naqi.mj.model.RoomTable;
import com.naqi.msg.MsgConstant;
import com.naqi.service.IProcessService;


/**
 * <pre>
 * Description : 任务队列
 * @author hanfeng
 * </pre>
 */
@Service(value="processService")
public class ProcessServiceImpl implements IProcessService{
	
	private Logger logger = LoggerFactory.getLogger(ProcessServiceImpl.class);

	private ProcessTask[] taskList = new ProcessTask[10];
	private ProcessTask loginProcess = null;
	@Override
	public void init(){
		for (int i = 0; i < taskList.length; i++) {
			taskList[i] = new ProcessTask();
			taskList[i].setName("process_task_"+i);
			taskList[i].setIndex(i);
			taskList[i].start();
		}
		
		loginProcess = new ProcessTask();
		loginProcess.setName("login_process");
		loginProcess.start();
		logger.info("启动任务");
	}
	//堆加到队列中
	public void process(Map<String,Object> map ,IoSession session){
		RoomTable roomTable = (RoomTable) session.getAttribute(MsgConstant.SESSION_KEY_GAME_TABLE);
		String cmd = (String) map.get("cmd");
		if(cmd == null ) return;
		if("login".equals(cmd)){
			loginProcess.addProcess(map, session);
		}else{
			int index = Math.abs(roomTable.getId())%taskList.length;
			taskList[index].addProcess( map ,session);
		}
	}
	
}
