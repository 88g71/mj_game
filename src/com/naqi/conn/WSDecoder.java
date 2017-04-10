package com.naqi.conn;

import java.nio.ByteOrder;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.StringTokenizer;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.future.IoFutureListener;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.apache.mina.util.Base64;

import com.naqi.conn.WSToolKit.WSSessionState;

public class WSDecoder extends CumulativeProtocolDecoder {
	private final static String REQUEST_CONTEXT_KEY = "__REQUEST_DATA_CONTEXT";
	private final static String END_TAG = "\r\n";
	
	private  enum FrameType {
		Text,Binary,Control;
	}
	
	private class RequestDataContext {
		
		private IoBuffer _tmp;
		private CharsetDecoder _charsetDecoder;
		private FrameType _frameType;
		
		RequestDataContext(String charset) {
			this._tmp = IoBuffer.allocate(512).setAutoExpand(true);
			
			this._charsetDecoder = Charset.forName("utf-8").newDecoder();
		}
		
		public FrameType getFrameType() {
			return this._frameType;
		}
		
		public String getDataAsString() {
			try {
				_tmp.flip();
				return _tmp.getString(_charsetDecoder);
			} catch (CharacterCodingException e) {
				return null;
			}
		}
		
		public byte[] getDataAsArray() {
			_tmp.flip();
			
			byte[] data = new byte[_tmp.remaining()];
			_tmp.get(data);
			
			return data;
		}
		
		void append(byte[] data) {
			this._tmp.put(data);
		}
		
		void setFrameType(FrameType _frameType) {
			this._frameType = _frameType;
		}
	}

	private ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;

	public void setByteOrder(ByteOrder byteOrder) {
		this.byteOrder = byteOrder;
	}

	private CharsetDecoder charsetDecoder = Charset.forName("utf-8")
			.newDecoder();

	public void setCharsetDecoder(CharsetDecoder charsetDecoder) {
		this.charsetDecoder = charsetDecoder;
	}

	@Override
	protected boolean doDecode(IoSession session, IoBuffer in,
			ProtocolDecoderOutput decoderOutput)
			throws CharacterCodingException, NoSuchAlgorithmException,Exception {
		in.mark();

		if (!in.hasRemaining())
			return false;

		WSSessionState state = WSToolKit.getSessionState(session);

		switch (state) {
			case Handshake:
				doHandshake(session, in);
				break;
	
			case Connected:
				if (in.remaining() < 2){
					in.reset();
					return false;
				}
	
				in.mark().order(this.byteOrder);
	
				byte fstByte = in.get();
	
				int opCode = fstByte & 0xf;
				switch (opCode) {
					case 0x0:
					case 0x1:
					case 0x2:
						boolean isFinalFrame = fstByte < 0;
						boolean isRsvColZero = (fstByte & 0x70) == 0;
						if (!isRsvColZero) {
							closeConnection(session, in);
							break;
						}
		
						byte secByte = in.get();
						boolean isMasking = secByte < 0;
		
						int dataLength = 0;
						byte payload = (byte) (secByte & 0x7f);
						if (payload == 126)
							dataLength = in.getShort();
						else if (payload == 127)
							dataLength = (int) in.getLong();
						else
							dataLength = payload;
		
						if (in.remaining() < (isMasking ? dataLength + 4 : dataLength)) {
							in.reset();
							return false;
						}
		
						byte[] mask = new byte[4];
						byte[] data = new byte[dataLength];
		
						if (isMasking)
							in.get(mask);
		
						in.get(data);
						
						for( int i=0, maskLength=mask.length, looplimit=data.length; i<looplimit; i++ ) 
							data[i] = (byte)(data[i] ^ mask[i % maskLength]);
						
						RequestDataContext context = (RequestDataContext) session
								.getAttribute(REQUEST_CONTEXT_KEY);
						if (context == null) {
							context = new RequestDataContext(charsetDecoder.charset()
									.name());
							context.setFrameType((opCode == 0x1) ? FrameType.Text
									: FrameType.Binary);
		
							session.setAttribute(REQUEST_CONTEXT_KEY, context);
						}
						context.append(data);
		
						if (isFinalFrame) {
							context = (RequestDataContext) session.removeAttribute(REQUEST_CONTEXT_KEY);
							if (context.getFrameType() == FrameType.Text){
								decoderOutput.write(context.getDataAsString());
							}else{
								byte[] dataBytes = context.getDataAsArray();
//								byte[] cmdBytes = new byte[]{dataBytes[0],dataBytes[1],dataBytes[2],dataBytes[3]};
//								byte[] protoBytes = new byte[dataBytes.length - 4];
//								for (int i = 4 , index = 0; i < dataBytes.length; i++ , index++) {
//									protoBytes[index] = dataBytes[i];
//								}
//								Message msg = MessageFactory.getInstance().createMessage(byte2int(cmdBytes), protoBytes);
								
								String msg = new String(dataBytes, Charset.forName("utf-8"));
								decoderOutput.write(msg);
							}
							return true;
						}
						else{
							in.reset();							
							return false;
						}
					case 0x3:
					case 0x4:
					case 0x5:
					case 0x6:
					case 0x7:
						break;
		
					case 0x8:
						closeConnection(session, in);
						break;
						
					case 0x9:
					case 0xA:
					default:
						closeConnection(session, in);
						break;
				}
	
				break;
	
			default:
				closeConnection(session, in);
				break;
		}
		return true;
	}
	public static void main(String[] args) {
		byte2int(null);
	}
	public static int byte2int(byte[] res) {   
		int targets = (res[3] & 0xff) | ((res[2] << 8) & 0xff00) // | 表示安位或   
		| ((res[1] << 24) >>> 8) | (res[0] << 24);   
		return targets;   
	}

	private void doHandshake(IoSession session, IoBuffer in)
			throws Exception {
		String handshakeMessage = in.getString(charsetDecoder);
		String[] msgColumns = splitHandshakeMessage(handshakeMessage);

		String requestURI = msgColumns[0];
		String httpVersion = requestURI.substring(
				requestURI.lastIndexOf("/") + 1, requestURI.length());

		String upgradeCol = WSToolKit.getMessageColumnValue(msgColumns, "Upgrade:");
		String connectionCol = WSToolKit.getMessageColumnValue(msgColumns, "Connection:");
		String secWsProtocolCol = WSToolKit.getMessageColumnValue(msgColumns,
				"Sec-WebSocket-Protocol:");
		String secWskeyCol = WSToolKit.getMessageColumnValue(msgColumns,
				"Sec-WebSocket-Key:");
		String wsVersionCol = WSToolKit.getMessageColumnValue(msgColumns,
				"Sec-WebSocket-Version:");

		
		boolean hasWebsocket = contains(upgradeCol, "websocket");
		boolean hasUpgrade = contains(connectionCol, "upgrade");
		boolean isGetMethod = "GET".equalsIgnoreCase(WSToolKit.subString(requestURI, 1,
				" "));

		boolean isSecWsKeyNull = secWskeyCol == null || secWskeyCol.isEmpty();
		boolean isValidVersion = "13".equals(wsVersionCol);
		boolean isValidHttpVer = Float.parseFloat(httpVersion) >= 1.1F;

		if (!hasWebsocket || !hasUpgrade || !isGetMethod)
			throw new Exception("Invalid websocket request!");

		if (isSecWsKeyNull || !isValidVersion || !isValidHttpVer)
			throw new Exception("Invalid websocket request!");

		String secWsAccept = getSecWebsocketAccept(secWskeyCol);
		String response = getResponseData(upgradeCol, connectionCol,
				secWsAccept, secWsProtocolCol);

		initRequestContext(session, msgColumns);
		session.write(response);
	}

	private String[] splitHandshakeMessage(String handshakeMessage) {
		StringTokenizer st = new StringTokenizer(handshakeMessage, END_TAG);
		String[] result = new String[st.countTokens()];

		for (int i = 0; st.hasMoreTokens(); i++)
			result[i] = st.nextToken();

		return result;
	}

	private boolean contains(String src, String target) {
		if (src == null || src.isEmpty())
			return false;
		else
			return src.toLowerCase().contains(target);
	}

	private String getSecWebsocketAccept(String secWebsocketkey)
			throws NoSuchAlgorithmException {

		StringBuilder srcBuilder = new StringBuilder();
		srcBuilder.append(secWebsocketkey);
		srcBuilder.append("258EAFA5-E914-47DA-95CA-C5AB0DC85B11");

		MessageDigest md = MessageDigest.getInstance("SHA-1");

		md.update(srcBuilder.toString().getBytes(charsetDecoder.charset()));
		byte[] ciphertext = md.digest();

		String result = new String(Base64.encodeBase64(ciphertext),
				charsetDecoder.charset());
		return result;
	}

	private String getResponseData(String upgrade, String connection,
			String secWsAccept, String secWsProtocol) {

		StringBuilder result = new StringBuilder();

		result.append("HTTP/1.1 101 Switching Protocols\r\n");
		result.append("Upgrade:").append(upgrade).append(END_TAG);
		result.append("Connection:").append(connection).append(END_TAG);
		result.append("Sec-WebSocket-Accept:").append(secWsAccept)
				.append(END_TAG);

		if (secWsProtocol != null && !"".equals(secWsProtocol))
			result.append("Sec-WebSocket-Protocol:").append(secWsProtocol)
					.append(END_TAG);

		result.append(END_TAG);

		return result.toString();
	}

	private void closeConnection(IoSession session, IoBuffer buffer) {
		buffer.free();
		session.closeNow();
	}

	@SuppressWarnings("unused")
	private void closeConnection(IoSession session, String errorMsg) {
		session.write(errorMsg).addListener(
				new IoFutureListener<WriteFuture>() {
					@Override
					public void operationComplete(WriteFuture future) {
						future.getSession().closeNow();
					}
				});
	}

	private void initRequestContext(IoSession session, String[] data) {
		session.setAttribute("__SESSION_CONTEXT", data);
	}
}