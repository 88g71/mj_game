package com.naqi.conn;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

/**
 * 编码器
 * 
 * @author hanfeng
 * 
 */
public class ClientEncoder extends ProtocolEncoderAdapter {

	@Override
	public void encode(IoSession arg0, Object arg1, ProtocolEncoderOutput arg2) throws Exception {
		byte[] body = (byte[]) arg1;
		IoBuffer buffer = IoBuffer.allocate(body.length, false);
//		IoBuffer buffer = IoBuffer.allocate(body.length + 4, false);
//		short length = (short) body.length;
		
//		buffer.putShort(length);
		buffer.put(body);
		buffer.flip();
		arg2.write(buffer);
		buffer.free();
	}

}
