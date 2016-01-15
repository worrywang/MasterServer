package netty.file.invalid;

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
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created by Administrator on 2016/1/11.
 */
public class FileServer {
	public static int FILE_PORT = Integer.parseInt(System.getProperty("port","8090"));
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
//						pipeline.addLast(new ProtobufDecoder(MsgProtocol.MsgRequest.getDefaultInstance()));
//						pipeline.addLast(new ProtobufEncoder());
						pipeline.addLast(new FileServerHandler());
					}
				});
		ChannelFuture f_main = b_main.bind(FILE_PORT);
		f_main.channel().closeFuture();
	}

}
