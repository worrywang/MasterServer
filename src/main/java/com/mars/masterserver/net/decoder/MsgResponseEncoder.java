package com.mars.masterserver.net.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 编码器
 * Created by Administrator on 2016/1/5.
 */
public class MsgResponseEncoder extends MessageToByteEncoder<MsgProtocol.MsgResponse> {
	@Override
	protected  void encode(ChannelHandlerContext ctx, MsgProtocol.MsgResponse msg, ByteBuf out) throws Exception {

		System.out.println("MsgResponseEncoder...");
		System.out.println("MsgResponseEncoder-encode(): [id]:"+msg.getId()+" ; [content.body]: "+msg.getContent().getBody());
//		ByteBuf bufferContent = Unpooled.buffer();
//		bufferContent.writeInt(msg.toByteArray().length).writeBytes(msg.toByteArray());
//		out.writeBytes(bufferContent);
		out.writeInt(msg.toByteArray().length);
		out.writeBytes(msg.toByteArray());
	}


}
