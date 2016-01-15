package com.mars.masterserver.net.decoder;

import com.mars.masterserver.config.Settings;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Created by Administrator on 2016/1/6.
 */
public class MsgResponseDecoder extends ByteToMessageDecoder{

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf inputBuf, List<Object> out) throws Exception {
		System.out.println("MsgResponseDecoder...");
		for(;;) {
			if (inputBuf.readableBytes() < Settings.HEAD_LENGTH) {  //HEAD_LENGTH表示头长度的字节数,int类型，4byte.
				return;
			}
			inputBuf.markReaderIndex();          //标记一下当前的readIndex的位置
			int dataLength = inputBuf.readInt();       // 读取传送过来的消息的长度。ByteBuf 的readInt()方法会让readIndex增加4
			if (dataLength < 0) { // 读到的消息体长度为0，这是不应该出现的情况，这里出现这情况，关闭连接。
				ctx.close();
			}

			if (inputBuf.readableBytes() < dataLength) { //读到的消息体长度如果小于我们传送过来的消息长度，则resetReaderIndex. 这个配合markReaderIndex使用的。把readIndex重置到mark的地方
				inputBuf.resetReaderIndex();
				return;
			}
			System.out.println("MsgResponseDecoder current dataLength:" + dataLength + " ; ");
			byte[] body = new byte[dataLength];  // 把传送过来的数据，取出来，读取到对应的byte数组中
			inputBuf.readBytes(body);  //

			Object o = MsgProtocol.MsgResponse.parseFrom(body);  //将byte数据转化为我们需要的对象
			out.add(o);
		}
	}
}
