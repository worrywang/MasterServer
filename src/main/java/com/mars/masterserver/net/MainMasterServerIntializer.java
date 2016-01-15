package com.mars.masterserver.net;

import com.mars.masterserver.config.Settings;
import com.mars.masterserver.net.decoder.FrameUtils;
import com.mars.masterserver.net.handler.MainMasterServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;

/**
 * Created by Administrator on 2015/12/5.
 */
public class MainMasterServerIntializer extends ChannelInitializer<SocketChannel> {

	private final SslContext sslCtx;
	public MainMasterServerIntializer(SslContext sslCtx){
		this.sslCtx = sslCtx;
	}


	@Override
	protected void initChannel(SocketChannel socketChannel) throws Exception {
		ChannelPipeline pipeline = socketChannel.pipeline();
		if(sslCtx!=null) {
			pipeline.addLast(sslCtx.newHandler(socketChannel.alloc()));
		}
		// On top of the SSL handler, add the text line codec.
		FrameUtils.setDecoderAndEncoder(pipeline, Settings.currentFrameType,Settings.currentSerializationType);
		// and then business logic.
		pipeline.addLast(new MainMasterServerHandler());
	}
}
