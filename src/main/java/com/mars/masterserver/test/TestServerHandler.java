package com.mars.masterserver.test;

import com.mars.masterserver.net.decoder.MsgProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by Administrator on 2016/1/4.
 */
public class TestServerHandler extends ChannelInboundHandlerAdapter{

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		super.channelActive(ctx);
		System.out.println("channelActive[" + ctx.channel().remoteAddress() + "] : is active......");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//		super.channelInactive(ctx);
		System.out.println("channelInactive[" + ctx.channel().remoteAddress() + "] : is inactive......");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//		super.channelRead(ctx, msg);
		System.out.println("channelRead[" + ctx.channel().remoteAddress() + "] : is reading......");
		ByteBuf byteBuf = (ByteBuf) msg;
		byte[] a = new byte[byteBuf.readableBytes()];
		byteBuf.readBytes(a);
		for(byte b:a){
			System.out.print(b);
		}
		System.out.println();
		System.out.println(String.valueOf(msg));

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//		super.exceptionCaught(ctx, cause);
		System.out.println("exceptionCaught[" + ctx.channel().remoteAddress() + "] : throws an exception......");
		cause.printStackTrace();
	}
}
