package com.mars.masterserver.test;

import com.mars.masterserver.core.domain.GameRequest;
import com.mars.masterserver.net.decoder.MsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by Administrator on 2015/12/23.
 */
public class TestClientHandler extends SimpleChannelInboundHandler<MsgProtocol.MsgResponse> {
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, MsgProtocol.MsgResponse msg) throws Exception {
		System.out.println("TestClientHandler--channelRead0: is reading...");
		MsgProtocol.Head head = msg.getHead();
		MsgProtocol.Content content = msg.getContent();
		System.out.println("[content]: id=" + head.toString() + "; body=" + content.getMsgList());
	}

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
}
