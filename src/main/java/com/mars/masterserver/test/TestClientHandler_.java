package com.mars.masterserver.test;

import com.mars.masterserver.net.decoder.MsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by Administrator on 2016/1/5.
 */
public class TestClientHandler_ extends ChannelInboundHandlerAdapter {

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("[" + ctx.channel().remoteAddress() + "] : throw an exception......");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("[" + ctx.channel().remoteAddress() + "] : is inactive......");

	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("[" + ctx.channel().remoteAddress() + "] : is active......");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object object) throws Exception {
		System.out.println("TestClientHandler--channelRead0: is reading...");
		MsgProtocol.MsgResponse msg = (MsgProtocol.MsgResponse)object;
//		String id = msg.getId();
//		MsgProtocol.Content content = msg.getContent();
//		System.out.println("[content]: id=" + id + "; body=" + content.getBody());
	}
}
