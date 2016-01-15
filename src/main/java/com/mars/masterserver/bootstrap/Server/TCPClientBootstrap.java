package com.mars.masterserver.bootstrap.Server;

import com.mars.masterserver.config.InitConfig;
import com.mars.masterserver.net.MasterServerIntializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;

/**
 * Created by Administrator on 2015/12/9.
 */
public class TCPClientBootstrap implements IClientBootstrap {
	public ChannelFuture createBootstrap(SslContext sslCtx,EventLoopGroup... eventLoopGroups) throws Exception{
		int len = eventLoopGroups.length;
		if(len>=2){
			ServerBootstrap b = new ServerBootstrap();
			b.group(eventLoopGroups[0], eventLoopGroups[1])
					.channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new MasterServerIntializer(sslCtx));
			ChannelFuture f_main = b.bind(InitConfig.TCP_PORT);
			return f_main;
		}
		return null;
	}
}
