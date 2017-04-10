/*
 * File name : SynchronizerTask.java
 * Created on : 2013-4-5 下午09:42:24
 * Creator : hanfeng
 */
package com.naqi.service.impl;

import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gxlu.mercury.extension.core.MercuryExtensionPlatform;
import com.naqi.service.IProcessExtension;
import com.naqi.service.IRoomService;
import com.naqi.util.GBeanFactory;

/**
 * <pre>
 * Description : 用于同步DB的任务队列
 * @author hanfeng
 * </pre>
 */
public class ProcessTask extends Thread {
	private Logger logger = LoggerFactory.getLogger(ProcessTask.class);
	
	
	//待更新数据缓存表
	private ConcurrentLinkedQueue<Map<String,Object>> msgs = new ConcurrentLinkedQueue<Map<String,Object>>();
	
	private boolean isShutDown = false;
	private IProcessExtension[] processExtensions;
	private IRoomService roomService;
	private int index = -1;
	
	public void setIndex(int index) {
		this.index = index;
	}

	public IProcessExtension[] getProcesser(){
		if(processExtensions != null) {
			return processExtensions;
		}
		Object[] extensionArray = MercuryExtensionPlatform.getExtensionInstance(IProcessExtension.class.getName());
		if(extensionArray !=null && extensionArray.length > 0){
			this.processExtensions = new IProcessExtension[extensionArray.length];
			for (int i = 0 ; i < extensionArray.length ; i ++) {
				processExtensions[i] = (IProcessExtension) extensionArray[i];
			}
		}
		return processExtensions;
	}
	
	@Override
	public void run() {
		while(!isShutDown){
			try{
				//桌子事件
				if(index != -1){
					getRoomService().gameTick(index);
				}
				for (int i = 0; i < 300; i++) {
					Map<String , Object> msg = popMsg();
					if(msg != null){
						try {
							//具体执行任务
							if (getProcesser().length > 0) {
								for (IProcessExtension processExtension : processExtensions) {
									boolean excude = processExtension.process(msg);
									if(excude) break;
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("执行消息任务失败，参数：{}，错误信息：{}",msg.toString(),e.toString());
						}
					}
				}
				Thread.sleep(15);
			}catch(InterruptedException ie){
				logger.error("处理消息任务失败：",ie.getMessage());
			}
				
		}
	}
	
	private Map<String,Object> popMsg() {
        return msgs.poll();
    }
	
	public int getSize(){
		return msgs.size();
	}
	/**
	 * 添加到更新队列
	 * @param cacheElement
	 */
	public void addProcess(Map<String,Object> process,IoSession session){
		process.put("session", session);
		msgs.offer(process);
	}

	public IRoomService getRoomService() {
		if(this.roomService != null) {
			return this.roomService;
		}
		this.roomService = GBeanFactory.getRoomService();
		return roomService;
	}

}

