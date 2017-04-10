package com.naqi.conn;

import java.io.UnsupportedEncodingException;
import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.naqi.msg.MsgConstant;
import com.naqi.util.MinaUtil;


/**
 * 解码器
 * 
 * @author hanfeng
 * 
 */
public class ClientDecoder extends CumulativeProtocolDecoder {

	private static Logger logger = Logger.getLogger(ClientDecoder.class);
	
	@Override
	protected boolean doDecode(IoSession session, IoBuffer iobuffer, ProtocolDecoderOutput protocolDecoderOutput) throws Exception {
		if (!iobuffer.hasRemaining()){return false;}
		iobuffer.order(ByteOrder.LITTLE_ENDIAN);
		iobuffer.mark();
		try {
			logger.debug("接受到消息，开始解码:" + session.getRemoteAddress());
			
			byte firestByte = iobuffer.get();
			if(firestByte == 71){
				iobuffer.reset();
				byte[] inbytes = iobuffer.array();
				String m = new String(inbytes,Charset.forName("ISO8859-1"));
				String ret = getSecWebSocketAccept(m);
				session.write(ret.getBytes());
				WSToolKit.getSessionState(session);
				return true;
			}else if (iobuffer.remaining() >= MsgConstant.RECEIVE_HEAD_LEN) {
				logger.debug("消息长度小于消息头，返回，长度:" + iobuffer.remaining());
				/**
				 * 帧类型
				 * | 0      | Continuation Frame                
				 *-+--------+-----------------------------------
				 * | 1      | Text Frame                        a
				 *-+--------+-----------------------------------
				 * | 2      | Binary Frame                      
				 *-+--------+-----------------------------------
				 * | 8      | Connection Close Frame            
				 * -+--------+----------------------------------
				 * | 9      | Ping Frame                        
				 * -+--------+----------------------------------
				 * | 10     | Pong Frame                        
				 */
				int opcode = firestByte&15;
				if(opcode == 8){
					MinaUtil.closeSessionAndLog(session, iobuffer, "webSocket 关闭  opcode = 8!!!" );
					return true;
				}
				//第二个字节
				byte secondByte = iobuffer.get();
				//数据的长度
				int dataLength = secondByte&0x7F;
				if(dataLength == 126 && iobuffer.remaining() > MsgConstant.SHORT_DATA_LEN){
					dataLength = iobuffer.getShort();
				}else if(dataLength == 127 && iobuffer.remaining() > MsgConstant.INT_DATA_LEN){
					dataLength = iobuffer.getInt();
				}
					
				byte[] maskingKeys = null;
				//负数  mask == 1
				byte[] dataBytes = new byte[dataLength];
				if(secondByte < 0 && iobuffer.remaining() >= dataLength + MsgConstant.MASK_KEY_LEN){
					maskingKeys = new byte[4];
					iobuffer.get(maskingKeys);
					iobuffer.get(dataBytes);
					for (int i = 0 ; i < dataBytes.length ; i++) {
						dataBytes[i] = (byte) (dataBytes[i] ^ maskingKeys[i%4]);
					}
					return true;
				}else if(secondByte >= 0 && iobuffer.remaining() >= dataLength){
					iobuffer.get(dataBytes);
					String message = new String(dataBytes);
					session.write(message);
					return true;
				}else{
					iobuffer.reset();
					return false;
				}
			}
			iobuffer.reset();
			return false;
		} catch (Exception e) {
			logger.error("GameDecoder.doDecode()，断掉连接", e);
			e.printStackTrace();
			return false;
		}
	}
	
	public static String getSecWebSocketKey(String req) {
		Pattern p = Pattern.compile("^(Sec-WebSocket-Key:).+",
				Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
		Matcher m = p.matcher(req);
		if (m.find()) {
			String foundstring = m.group();
			return foundstring.split(":")[1].trim();
		} else {
			return null;
		}

	}

	

	public static String base64Encode(byte[] input) {
		return new String(Base64Util.encode(input));
	}
	public static String getSecWebSocketAccept(String key) {
		String secKey = getSecWebSocketKey(key);

		String guid = "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
		secKey += guid;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(secKey.getBytes("utf-8"), 0, secKey.length());
			byte[] sha1Hash = md.digest();
			secKey = base64Encode(sha1Hash);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		String rtn = "HTTP/1.1 101 Switching Protocols\r\nUpgrade: websocket\r\nConnection: Upgrade\r\nSec-WebSocket-Accept: "
				+ secKey + "\r\n\r\n";
		return rtn;
	}
	
	public static String decode(byte[] receivedDataBuffer)
			throws UnsupportedEncodingException {
		String result = null;

		// 计算非空位置
		int lastStation = receivedDataBuffer.length - 1;
		
		// 利用掩码对org-data进行异或
		int frame_masking_key = 1;
		for (int i = 6; i <= lastStation; i++) {
			frame_masking_key = i % 4;
			frame_masking_key = frame_masking_key == 0 ? 4 : frame_masking_key;
			frame_masking_key = frame_masking_key == 1 ? 5 : frame_masking_key;
			receivedDataBuffer[i] = (byte) (receivedDataBuffer[i] ^ receivedDataBuffer[frame_masking_key]);
		}

		result = new String(receivedDataBuffer, 6, lastStation - 5, "UTF-8");

		return result;

	}
	
}
