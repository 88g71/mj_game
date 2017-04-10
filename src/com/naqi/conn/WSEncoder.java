package com.naqi.conn;

import java.nio.ByteOrder;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Random;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

//import com.google.protobuf.Message;
//import com.naqi.center.msg.MessageFactory;


import com.naqi.conn.WSToolKit.WSSessionState;

public class WSEncoder extends ProtocolEncoderAdapter {
	//高低位
	private ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
	public void setByteOrder(ByteOrder byteOrder) {
		this.byteOrder = byteOrder;
	}
	public ByteOrder getByteOrder() {
		return this.byteOrder;
	}
	//字符串字符集
	private CharsetEncoder charsetEncoder = Charset.forName("utf-8")
			.newEncoder();
	public void setCharsetEncoder(CharsetEncoder charsetEncoder) {
		this.charsetEncoder = charsetEncoder;
	}
	//包最大长度
	private int defaultPageSize = 65536;
	public void setDefaultPageSize(int defaultPageSize) {
		this.defaultPageSize = defaultPageSize;
	}
	//加密
	private boolean isMasking = false;
	public void setIsMasking(boolean masking) {
		this.isMasking = masking;
	}
	/**
	 * 编码
	 */
	@Override
	public void encode(IoSession session, Object message, ProtocolEncoderOutput encoderOutput) throws CharacterCodingException {
		//发送数据长度
		IoBuffer buff = IoBuffer.allocate(1024).setAutoExpand(true).setAutoShrink(true);
		//连接状态
		WSSessionState status = WSToolKit.getSessionState(session);
		
		switch (status) {
			case Handshake:
				try {
					buff.putString((String) message, charsetEncoder)
						.flip();
					IoBuffer sendBuffer = buff.slice();
					encoderOutput.write(sendBuffer);
				} catch (CharacterCodingException e) {
					session.closeNow();
				}
				//设置连接状态
				session.setAttribute(WSSessionState.ATTRIBUTE_KEY, WSSessionState.Connected);
				break;
			case Connected:
				if (!session.isConnected() || message == null)
					return;
				//字符串类型
				byte dataType = 1;
				byte[] data = null;
				if (message instanceof String){
					data = ((String) message).getBytes(charsetEncoder.charset());
				}
//				else if (message instanceof Message){
//					int cmd = MessageFactory.getInstance().getMessageCommand(message.getClass());
//					byte[] cmdByte = getInt2Byte(cmd);
//					byte[] msgByte = ((Message) message).toByteArray();
//					data = new byte[cmdByte.length + msgByte.length];
//					int dataIndex = 0;
//					for (int i = 0; i < cmdByte.length; i++,dataIndex++) {
//						data[dataIndex] = cmdByte[i];
//					}
//					for (int i = 0; i < msgByte.length; i++,dataIndex++) {
//						data[dataIndex] = msgByte[i];
//					}
//					//字节数组类型
//					dataType = 2;
//				}
				//加密
				byte[] mask = new byte[4];
				Random random = new Random();
				
				int pageSize = this.defaultPageSize;
				//消息长度
				int remainLength = data.length;			
				int realLength = 0;						
				int dataIndex = 0;
				for( boolean isFirstFrame=true, isFinalFrame = false; !isFinalFrame; buff.clear(), isFirstFrame=false) {
					//头长度
					int headerLeng = 2;
					//长度标示
					int payload = 0;
					if (remainLength > 0 && remainLength <= 125) {
						payload = remainLength;
					} else if (remainLength > 125 && remainLength <=65536) {
						payload = 126;
					} else {
						payload = 127;
					}
					//获取包头长度占用字节
					switch(payload) {
						case 126 : 
							headerLeng += 2;
							break;
							
						case 127 :
							headerLeng += 8;
							break;
							
						default  :
							headerLeng += 0;
							break;
					}
					//加密长度
					headerLeng += isMasking ? 4 : 0;
					//当前消息长度
					realLength = ( pageSize - headerLeng )  >= remainLength ? remainLength : ( pageSize - headerLeng );
					//是否结束包
					isFinalFrame = (remainLength - (pageSize - headerLeng)) < 0;
					//第一个字节
					byte fstByte = (byte)(isFinalFrame ? 0x80 : 0x0);
					fstByte += isFirstFrame ? dataType : 0;
					buff.put(fstByte);
					//第二个字节
					byte sndByte = (byte)(isMasking ? 0x80 : 0);
					sndByte += payload;
					buff.put(sndByte);
					//消息长度字节
					switch(payload) {
						case 126 : 
							buff.putUnsignedShort(realLength);
							break;
							
						case 127 :
							buff.putLong(realLength);
							break;
							
						default  :
							break;
					}
					//解码字节
					if (isMasking) {
						random.nextBytes(mask);
						buff.put(mask);
						for(int loopCount=dataIndex+realLength, i=0; dataIndex<loopCount; dataIndex++,i++){
							buff.put((byte)(data[dataIndex] ^ mask[i%4]));
						}
					}else{
						for(int loopCount=dataIndex+realLength; dataIndex<loopCount; dataIndex++){
							buff.put(data[dataIndex]);
						}
					}
					buff.flip();
					IoBuffer sendBuffer = buff.slice();
					encoderOutput.write(sendBuffer);
					//减掉已发送的消息字节
					remainLength -= (pageSize - headerLeng);
				}
				
				break;
	
			default:
				session.closeNow();
				break;
		}
	}
	
//	private static byte[] getInt2Byte(int i) {
//		 byte[] buf = new byte[4];  
//	        buf[0] = (byte) (i >> 24);  
//	        buf[1] = (byte) (i >> 16);  
//	        buf[2] = (byte) (i >> 8);  
//	        buf[3] = (byte) i;
//	        return buf;  
//	}
}