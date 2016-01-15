package com.mars.masterserver.test;

import com.mars.masterserver.bootstrap.Server.TCPClientBootstrap;
import com.mars.masterserver.bootstrap.Server.UDPClientBootstrap;
import com.mars.masterserver.config.InitConfig;
import com.mars.masterserver.config.Settings;
import com.mars.masterserver.core.HandlerDispatcher;
import com.mars.masterserver.net.MainMasterServerIntializer;
import com.mars.masterserver.net.decoder.MsgProtocol;
import com.mars.masterserver.net.handler.MainMasterServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * Created by Administrator on 2015/12/23.
 */
public class TestServer {

	public static void main(String[] args) throws Exception{
		// Configure SSL.
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		ServerBootstrap b_main = new ServerBootstrap();
		b_main.group(bossGroup,workerGroup)
				.channel(NioServerSocketChannel.class)
				.handler(new LoggingHandler(LogLevel.INFO))
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
						pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
						pipeline.addLast(new ProtobufDecoder(MsgProtocol.MsgRequest.getDefaultInstance()));
						pipeline.addLast(new ProtobufEncoder());
						pipeline.addLast(new MainMasterServerHandler());
//						pipeline.addLast(new TestServerHandler());
					}
				});
		ChannelFuture f_main = b_main.bind(InitConfig.PORT_MAIN);
		f_main.channel().closeFuture();
	}

}
