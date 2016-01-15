package netty.Protobuf;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2015/12/11.
 */
public class WorldClockClient {

	public static String HOST = "127.0.0.1";
	public static int PORT = 9999;
	static final List<String> CITIES = Arrays.asList(System.getProperty(
			"cities", "Asia/Seoul,Europe/Berlin,America/Los_Angeles").split(","));
	/**
	 * 初始化Bootstrap
	 * @return
	 */
	public static void main(String[] args) throws Exception{
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group).channel(NioSocketChannel.class);
		b.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
				pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
				//decode
//				pipeline.addLast(new ProtobufVarint32FrameDecoder());
				pipeline.addLast(new ProtobufDecoder(WorldClockProtocol.LocalTimes.getDefaultInstance()));
				//encode
//				pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
				pipeline.addLast(new ProtobufEncoder());
				pipeline.addLast("handler", new WorldClockClientHandler());
			}
		});
		Channel channel = b.connect(HOST, PORT).sync().channel();
		WorldClockClientHandler handler = channel.pipeline().get(WorldClockClientHandler.class);
		List<String> response = handler.getLocalTimes(CITIES);
		channel.close();

		for(int i=0;i<CITIES.size();i++){
			System.out.format("%28s: %s%n",CITIES.get(i),response.get(i));
		}
	}


}
