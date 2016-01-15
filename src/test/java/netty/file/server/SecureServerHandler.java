package netty.file.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import netty.file.model.SecureModel;

/**
 * 对客户端上传数据进行有效性验证
 * Created by Administrator on 2016/1/15.
 */
public class SecureServerHandler extends ChannelInboundHandlerAdapter{

	static final ChannelGroup channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

	public SecureServerHandler(){
		System.out.println("SecureServerHandler");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//		super.channelRead(ctx, msg);
		System.out.println("SecureServerHandler. this is channel read....");
		if(msg instanceof SecureModel){
			SecureModel secureModel = (SecureModel)msg;
			if(secureModel.getToken()!=null){
				//todo: 验证token是否存在，并且token对应的ip和ctx里面源ip是否一致
				if(true){
					channels.add(ctx.channel());
					secureModel.setAutoSuccess(true);
					ctx.writeAndFlush(secureModel);
					return;
				}
			}
			secureModel.setAutoSuccess(false);
			ctx.writeAndFlush(secureModel);
			ctx.close();
		}else{
			if(!channels.contains(ctx.channel())){
				SecureModel secureModel = new SecureModel();
				secureModel.setAutoSuccess(false);
				ctx.writeAndFlush(secureModel);
				ctx.close();
			}else{
				ctx.fireChannelRead(msg); //继续执行
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
