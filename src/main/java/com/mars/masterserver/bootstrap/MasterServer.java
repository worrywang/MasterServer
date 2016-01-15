package com.mars.masterserver.bootstrap;

import com.mars.masterserver.bootstrap.Server.TCPClientBootstrap;
import com.mars.masterserver.bootstrap.Server.UDPClientBootstrap;
import com.mars.masterserver.config.InitConfig;
import com.mars.masterserver.config.Settings;
import com.mars.masterserver.core.HandlerDispatcher;
import com.mars.masterserver.net.MainMasterServerIntializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

/**
 * Created by Administrator on 2015/12/3.
 */
public class MasterServer {

	private static HandlerDispatcher handlerDispatcher = HandlerDispatcher.getInstance();
	private static Thread t;

	public static void main(String[] args) throws Exception{
		// Configure SSL.
		final SslContext sslCtx;
		if (InitConfig.SSL) {
			SelfSignedCertificate ssc = new SelfSignedCertificate();
			sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
		} else {
			sslCtx = null;
		}

		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		//创建监听端口
		createMainServerBootstrap(bossGroup, workerGroup, sslCtx);
		createServerBootstrap(bossGroup, workerGroup, sslCtx);
		//开启后台逻辑处理
		createHandlerDispatcher();
	}


	private static void createHandlerDispatcher(){
		t = new Thread(handlerDispatcher);
		handlerDispatcher.init();
		t.start();
	}

	/**
	 * 监听主端口，用来与逻辑引擎通信
	 * @param bossGroup
	 * @param workerGroup
	 * @param sslCtx
	 * @throws Exception
	 */
	private static void createMainServerBootstrap(EventLoopGroup bossGroup,EventLoopGroup workerGroup,SslContext sslCtx) throws Exception{
			ServerBootstrap b_main = new ServerBootstrap();
			b_main.group(bossGroup,workerGroup)
					.channel(NioServerSocketChannel.class)
					.handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new MainMasterServerIntializer(sslCtx));
			ChannelFuture f_main = b_main.bind(InitConfig.PORT_MAIN);
			f_main.channel().closeFuture();
	}

	/**
	 * 监听客户端通信端口，主要用来与控制端通信
	 * @param bossGroup
	 * @param workerGroup
	 * @param sslCtx
	 * @throws Exception
	 */
	private  static void createServerBootstrap(EventLoopGroup bossGroup,EventLoopGroup workerGroup,SslContext sslCtx) throws Exception{
		//todo: 换成监听多端口
		switch (Settings.currentProtocol){
			case ALL:
				ChannelFuture f_tcp = new TCPClientBootstrap().createBootstrap(sslCtx, bossGroup, workerGroup);
				f_tcp.channel().closeFuture();
				ChannelFuture f_udp = new UDPClientBootstrap().createBootstrap(sslCtx, bossGroup);
				f_udp.channel().closeFuture();
				break;
			case TCP:
				f_tcp = new TCPClientBootstrap().createBootstrap(sslCtx, bossGroup, workerGroup);
				f_tcp.channel().closeFuture();
				break;
			case UDP:
				//todo: 添加UDP协议时
				f_udp = new UDPClientBootstrap().createBootstrap(sslCtx, bossGroup);
				f_udp.channel().closeFuture();
				break;
			default:

				break;
		}
	}

}
