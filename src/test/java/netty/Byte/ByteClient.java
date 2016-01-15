package netty.Byte;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Created by Administrator on 2015/12/10.
 */
public class ByteClient {
	public static String HOST = "127.0.0.1";
	public static int PORT = 9999;

	public static Bootstrap bootstrap = getBootstrap();
	public static Channel channel = getChannel(HOST,PORT);
	/**
	 * 初始化Bootstrap
	 * @return
	 */
	public static final Bootstrap getBootstrap(){
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group).channel(NioSocketChannel.class);
		b.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
				pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
				pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
//				pipeline.addLast(new StringDecoder());
//				pipeline.addLast(new StringEncoder());
				pipeline.addLast("handler", new ByteClientHandler());
			}
		});
		b.option(ChannelOption.SO_KEEPALIVE, true);
		return b;
	}

	public static final Channel getChannel(String host,int port){
		Channel channel = null;
		try {
			channel = bootstrap.connect(host, port).sync().channel();
		} catch (Exception e) {
			System.err.println(String.format("连接Server(IP[%s],PORT[%s])失败", host, port));
			return null;
		}
		return channel;
	}

	public static void sendMsg(String msg) throws Exception {
		if(channel!=null){

			ByteBuf byteBuf = Unpooled.buffer(1024);
			byteBuf.writeBytes(msg.getBytes());
			channel.writeAndFlush(byteBuf).sync();
		}else{
			System.err.println("消息发送失败,连接尚未建立!");
		}
	}

	public static void main(String[] args) throws Exception {
		try {
			long t0 = System.nanoTime();
			for (int i = 0; i < 100; i++) {
				ByteClient.sendMsg(i+"你好1");
			}
			long t1 = System.nanoTime();
			System.out.println((t1-t0)/1000000.0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
