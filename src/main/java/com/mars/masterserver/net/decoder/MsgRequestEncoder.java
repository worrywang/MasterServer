package com.mars.masterserver.net.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by Administrator on 2016/1/6.
 */
public class MsgRequestEncoder extends MessageToByteEncoder<MsgProtocol.MsgRequest> {
	@Override
	protected void encode(ChannelHandlerContext ctx, MsgProtocol.MsgRequest msg, ByteBuf out) throws Exception {
		System.out.println("MsgRequestEncoder...");
		out.writeInt(msg.toByteArray().length);
		out.writeBytes(msg.toByteArray());
	}
}
