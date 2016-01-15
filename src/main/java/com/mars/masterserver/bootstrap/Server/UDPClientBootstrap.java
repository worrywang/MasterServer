package com.mars.masterserver.bootstrap.Server;

import com.mars.masterserver.config.InitConfig;
import com.mars.masterserver.net.handler.MasterServerHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.ssl.SslContext;

/**
 * Created by Administrator on 2015/12/9.
 */
public class UDPClientBootstrap implements IClientBootstrap {
	public ChannelFuture createBootstrap(SslContext sslCtx,EventLoopGroup... eventLoopGroups) throws Exception{
		int len = eventLoopGroups.length;
		if(len>=1){
			Bootstrap b_upd = new Bootstrap();
			b_upd.group(eventLoopGroups[0])
					.channel(NioDatagramChannel.class)
					.option(ChannelOption.SO_BROADCAST,true)
					.handler(new MasterServerHandler());
			ChannelFuture f=b_upd.bind(InitConfig.UDP_PORT);
//			f.channel().closeFuture();
			return f;
		}
		return null;
	}
}
