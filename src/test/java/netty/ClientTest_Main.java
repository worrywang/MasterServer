package netty;

import com.mars.masterserver.config.InitConfig;
import com.mars.masterserver.config.Settings;
import com.mars.masterserver.net.decoder.MsgProtocol;
import com.mars.masterserver.net.utils.StreamUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
/**
 * Created by Administrator on 2015/12/4.
 */
public class ClientTest_Main {

	static final int COUNT = Integer.parseInt(System.getProperty("count", "1000"));

	public static void main(String[] args) throws Exception {
		// Configure SSL.

		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group)
					.channel(NioSocketChannel.class)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel socketChannel) throws Exception {
							ChannelPipeline p = socketChannel.pipeline();
							switch (Settings.currentFrameType) {
								case Delimiter:
									p.addLast("framer", new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
									break;
								case LengthField:
									//decoded
									p.addLast(new LengthFieldBasedFrameDecoder(InitConfig.MAX_FRAME_LENGTH,0,4,0,4));
//									p.addLast(new ProtobufDecoder());
									//encoded
									p.addLast(new LengthFieldPrepender(4));
									break;
								default:
									break;
							}
							switch (Settings.currentSerializationType){
								case String:
									p.addLast("decoder", new StringDecoder());
									p.addLast("encoder", new StringEncoder());
									break;
								case Byte:
									break;
								case PROTOBUF:
									p.addLast(new ProtobufDecoder(MsgProtocol.MsgResponse.getDefaultInstance()));
//									p.addLast(new ProtobufDecoder(MsgProtocol.MsgRequest.getDefaultInstance()));
									p.addLast(new ProtobufEncoder());
									break;
								default:
									break;
							}
							//注册handler
							p.addLast(new ClientTestHandler());
						}
					});

			// Make a new connection.
			ChannelFuture f = b.connect(InitConfig.HOST, InitConfig.PORT_MAIN).sync();
			Channel ch = f.channel();
			ChannelFuture lastWriteFuture = null;
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			for (;;) {
				String line = in.readLine();
				if (line == null) {
					break;
				}
				System.out.println("this is line end...");
				// Sends the received line to the server.
				switch (Settings.currentSerializationType){
					case Byte:
						StreamUtils s = new StreamUtils();
						s.writeBytes(line.getBytes("UTF-8"));
						lastWriteFuture = ch.writeAndFlush(s.getBuffer());
						break;
					case String:
						lastWriteFuture = ch.writeAndFlush(line + "\r\n");
						break;
					case PROTOBUF:
//						MsgProtocol.MsgRequest.Builder builder = MsgProtocol.MsgRequest.newBuilder();
//						String msg_id = "id_"+(int)(Math.random()*100);
//						MsgProtocol.Content.Builder content_builder = MsgProtocol.Content.newBuilder();
//						content_builder.setBody(line);
//						builder.setId(msg_id);
//						builder.setContent(content_builder.build());
//						lastWriteFuture = ch.writeAndFlush(builder.build());
						break;
					default:break;
				}

				// If user typed the 'bye' command, wait until the server closes
				// the connection.
				if ("bye".equals(line.toLowerCase())) {
					ch.closeFuture().sync();
					break;
				}
			}

			// Wait until all messages are flushed before closing the channel.
			if (lastWriteFuture != null) {
				lastWriteFuture.sync();
			}

		} finally {
			group.shutdownGracefully();
		}
	}
}
