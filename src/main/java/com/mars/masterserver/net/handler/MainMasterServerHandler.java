package com.mars.masterserver.net.handler;

import com.mars.masterserver.config.Settings;
import com.mars.masterserver.core.HandlerDispatcher;
import com.mars.masterserver.core.domain.GameRequest;
import com.mars.masterserver.core.domain.MessageQueue;
import com.mars.masterserver.net.decoder.MsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.log4j.Logger;

/**
 * Created by Administrator on 2015/12/5.
 */
public class MainMasterServerHandler extends SimpleChannelInboundHandler<Object> {
	public Logger logger = Logger.getLogger(this.getClass());
	private HandlerDispatcher handlerDispatcher;

	public MainMasterServerHandler(){
		handlerDispatcher = HandlerDispatcher.getInstance();
	}


	/**
	 * 取代netty3.0中的channelConnected()
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		handlerDispatcher.addMainChannel(ctx.channel());//存入channel
		handlerDispatcher.getMainMessageQueueHandler().addMessageQueue(ctx.channel()); //存入对应客户端获取的消息队列
		logger.info("channelActive[" + ctx.channel().remoteAddress() + "] : is active......");
		System.out.println("channelActive[" + ctx.channel().remoteAddress() + "] : is active......");
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object s) throws Exception {
//		ByteBuf in = (ByteBuf) s;
//		System.out.println("channelRead0[" + ctx.channel().remoteAddress() + "] : is reading......" + handlerDispatcher.getMain_channels().size());
//		////添加消息
//		System.out.println("channelRead0["+ctx.channel().remoteAddress()+"] msg : ......mainport"+s);
//		System.out.println("channelRead0["+ctx.channel().remoteAddress()+"] msg : ......mainport"+in.toString());
		System.out.println("channelRead0[" + ctx.channel().remoteAddress() + "] : is reading......");

		switch (Settings.currentSerializationType){
			case PROTOBUF:
				if(s instanceof MsgProtocol.MsgRequest){
					MsgProtocol.Head head = ((MsgProtocol.MsgRequest)s).getHead();
					MsgProtocol.Content content = ((MsgProtocol.MsgRequest)s).getContent();
					System.out.println("[content]: id="+head.getSrcID()+"; body="+content.getMsgList().size());

					GameRequest gameRequest = ProtocolConverMsgUtil.convertGameRequest(ctx.channel(),(MsgProtocol.MsgRequest)s);
					//头文件处理
					handlerDispatcher.headMsgHandler(head,ctx.channel());
					//添加消息到处理队列
					handlerDispatcher.getMainMessageQueueHandler().addMessage(gameRequest);

//					//todo: test
//					MsgProtocol.MsgResponse.Builder builder = MsgProtocol.MsgResponse.newBuilder();
//					MsgProtocol.Head.Builder head_builder = MsgProtocol.Head.newBuilder();
//					MsgProtocol.Content.Builder content_builder = MsgProtocol.Content.newBuilder();
//					head_builder.setSrcID("id_" + head.getSrcID());
//					builder.setHead(head_builder.build());
//					content_builder.setMsg(1, MsgProtocol.Msg.newBuilder().build());
//					builder.setContent(content_builder);
//					MsgProtocol.MsgResponse response = builder.build();
//					int len = response.toByteArray().length;
//					System.out.println("mainmasterserverhandler: response length : "+len);
////					byte[] result = new byte[4+len];
////					result[0] = (byte)((len >> 24) & 0xFF);
////					result[1] = (byte)((len >> 16) & 0xFF);
////					result[2] = (byte)((len >> 8) & 0xFF);
////					result[3] = (byte)(len & 0xFF);
//
////					ctx.channel().write(result);
////					ctx.channel().write(builder.build().toByteArray());
////					ctx.channel().flush();
//					ctx.channel().writeAndFlush(builder.build());
				}else{
					logger.error("resquest data not a protobuf type...");
				}


				break;
			default:
					break;
		}
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//		System.out.println("["+ctx.channel().remoteAddress()+"] :this is channelReadComplete-------------");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error("[" + ctx.channel().remoteAddress()+"] has an exception……");
//		cause.printStackTrace();
		handlerDispatcher.removeMainChannelInfo(ctx.channel());
		ctx.close();
	}

	/**
	 *取代netty3.0中的channelDisconnected()，客户端主动关闭连接
	 * @param ctx
	 * @throws Exception
	 */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		logger.info("[" + ctx.channel().remoteAddress() + "] : is inactive......");
		handlerDispatcher.removeMainChannelInfo(ctx.channel());
		System.out.println("[" + ctx.channel().remoteAddress() + "] : is inactive......");
		ctx.close();
	}
//
//	@Override
//	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
////		super.channelRegistered(ctx);
//		System.out.println("this is channel registered-------------");
//	}
//
//	@Override
//	public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
////		super.channelUnregistered(ctx);
//		System.out.println("this is channel unregistered-------------");
//	}
}
