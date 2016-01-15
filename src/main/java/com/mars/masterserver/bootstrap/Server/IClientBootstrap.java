package com.mars.masterserver.bootstrap.Server;

import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.handler.ssl.SslContext;

/**
 * Created by Administrator on 2015/12/9.
 */
public interface IClientBootstrap {
	ChannelFuture createBootstrap(SslContext sslCtx,EventLoopGroup ...eventLoopGroups)throws Exception;
}
