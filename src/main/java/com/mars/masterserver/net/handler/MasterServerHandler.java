package com.mars.masterserver.net.handler;

import com.mars.masterserver.config.Settings;
import com.mars.masterserver.core.HandlerDispatcher;
import com.mars.masterserver.core.domain.GameRequest;
import com.mars.masterserver.net.decoder.MsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;

/**
 * Created by Administrator on 2015/12/4.
 * 消息协议接收分发器
 */
public class MasterServerHandler extends SimpleChannelInboundHandler<Object>{
	public Logger logger = Logger.getLogger(this.getClass());
	private HandlerDispatcher handlerDispatcher;


	public MasterServerHandler(){
		//TODO: 添加服务
		handlerDispatcher = HandlerDispatcher.getInstance();
	}


	/**
	 * 取代netty3.0中的channelConnected()
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		super.channelActive(ctx);
		handlerDispatcher.addChannel(ctx.channel());
		handlerDispatcher.getMessageQueueHandler().addMessageQueue(ctx.channel()); //存入对应客户端获取的消息队列
		logger.info("["+ctx.channel().remoteAddress()+"] : is active......");
		System.out.println("["+ctx.channel().remoteAddress()+"] : is active......");

	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object s) throws Exception {
		System.out.println("channelRead0[" + ctx.channel().remoteAddress() + "] : is reading......");
		switch (Settings.currentSerializationType){
			case PROTOBUF:
				if(s instanceof MsgProtocol.MsgRequest){
					MsgProtocol.Head head = ((MsgProtocol.MsgRequest)s).getHead();
					MsgProtocol.Content content = ((MsgProtocol.MsgRequest)s).getContent();
					System.out.println("[content]: id="+head.getSrcID()+"; body="+content.getMsgList().size());

					GameRequest gameRequest = ProtocolConverMsgUtil.convertGameRequest(ctx.channel(), (MsgProtocol.MsgRequest) s);
					//头文件处理
					handlerDispatcher.headMsgHandler(head, ctx.channel());
					//通知
					handlerDispatcher.getMessageQueueHandler().addMessage(gameRequest);
				}
				break;
			default:
				break;
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//		super.channelReadComplete(ctx);
		System.out.println("["+ctx.channel().remoteAddress()+"] :this is channelReadComplete-------------");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("[" + ctx.channel().remoteAddress() + "] 出异常……");
//		cause.printStackTrace();
		handlerDispatcher.removeChannelInfo(ctx.channel());
		ctx.close();
	}

	/**
	 *取代netty3.0中的channelDisconnected(),客户端主动关闭连接
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("[" + ctx.channel().remoteAddress() + "] : is inactive......");
		handlerDispatcher.removeChannelInfo(ctx.channel());
		System.out.println("[" + ctx.channel().remoteAddress() + "] : is inactive......");
		ctx.close();
	}

}
