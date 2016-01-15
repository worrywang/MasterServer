package netty.file.invalid;

import com.mars.masterserver.config.InitConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;

/**
 * Created by Administrator on 2016/1/11.
 */
public class FileClient {
	public static void main(String[] args) throws Exception{
		EventLoopGroup group = new NioEventLoopGroup();
		final Bootstrap b = new Bootstrap();
		b.group(group)
				.channel(NioSocketChannel.class)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ChannelPipeline pipeline = ch.pipeline();
						pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
						pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
//						pipeline.addLast(new ProtobufDecoder(MsgProtocol.MsgRequest.getDefaultInstance()));
//						pipeline.addLast(new ProtobufEncoder());
						pipeline.addLast(new FileClientHandler());
					}
				});
		ChannelFuture f = b.connect(InitConfig.HOST, FileServer.FILE_PORT).sync();
		try {
			f.sync().channel();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			f.channel().closeFuture();
		}
	}
}
