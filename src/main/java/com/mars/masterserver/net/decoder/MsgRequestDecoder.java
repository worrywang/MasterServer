package com.mars.masterserver.net.decoder;

import com.mars.masterserver.config.Settings;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.apache.log4j.Logger;

import java.nio.ByteOrder;
import java.util.List;

/**
 * 解码器
 * Created by Administrator on 2016/1/5.
 */
public class MsgRequestDecoder extends ByteToMessageDecoder {
	private  static final Logger logger = Logger.getLogger(MsgRequestDecoder.class);
	private byte ZeroByteCount = 0;
	private ByteBuf bytes;
	private final ByteOrder endianOrder = ByteOrder.LITTLE_ENDIAN;
	private long secondTime = 0;
	private int reveCount= 0;

	/**
	 * 重新组装字节数组
	 * @param inputBuf
	 * @return
	 */
	ByteBuf bytesAction(ByteBuf inputBuf){
		ByteBuf bufferLen = Unpooled.buffer();
		if(bytes !=null){
			bufferLen.writeBytes(bytes);
			bytes = null;
		}
		bufferLen.writeBytes(inputBuf);
		return bufferLen;
	}

	/**
	 * 留存无法读取的byte等待下一次接收的数据包
	 * @param inputBuf 数据包
	 * @param startI 起始位置
	 * @param lenI 结束位置
	 */
	void bytesAction(ByteBuf inputBuf,int startI,int lenI){
		if(lenI-startI>0){
			bytes = Unpooled.buffer();
			bytes.writeBytes(inputBuf,startI,lenI);
		}
	}

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf inputBuf, List<Object> out) throws Exception {
		System.out.println("MsgRequestDecoder...");
		for(;;) {
			if (inputBuf.readableBytes() < Settings.HEAD_LENGTH) {  //这个HEAD_LENGTH是我们用于表示头长度的字节数。  由于上面我们传的是一个int类型的值，所以这里HEAD_LENGTH的值为4.
				return;
			}
			inputBuf.markReaderIndex();          //我们标记一下当前的readIndex的位置
			int dataLength = inputBuf.readInt();       // 读取传送过来的消息的长度。ByteBuf 的readInt()方法会让他的readIndex增加4
			if (dataLength < 0) { // 我们读到的消息体长度为0，这是不应该出现的情况，这里出现这情况，关闭连接。
				ctx.close();
			}

			if (inputBuf.readableBytes() < dataLength) { //读到的消息体长度如果小于我们传送过来的消息长度，则resetReaderIndex. 这个配合markReaderIndex使用的。把readIndex重置到mark的地方
				inputBuf.resetReaderIndex();
				return;
			}
			System.out.println("MsgRequestDecoder current dataLength:" + dataLength + " ; ");
			byte[] body = new byte[dataLength];  //  嗯，这时候，我们读到的长度，满足我们的要求了，把传送过来的数据，取出来
			inputBuf.readBytes(body);  //

			Object o = MsgProtocol.MsgRequest.parseFrom(body);  //将byte数据转化为我们需要的对象。伪代码，用什么序列化，自行选择
			out.add(o);
		}
	}
}
