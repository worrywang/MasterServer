package com.mars.masterserver.test;

import com.mars.masterserver.config.InitConfig;
import com.mars.masterserver.net.decoder.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2015/12/23.
 */
public class TestClient {
	public static void main(String[] args) throws Exception{
		EventLoopGroup group = new NioEventLoopGroup();
		Bootstrap b = new Bootstrap();
		b.group(group).channel(NioSocketChannel.class);
		b.handler(new ChannelInitializer<Channel>() {
			@Override
			protected void initChannel(Channel ch) throws Exception {
				ChannelPipeline pipeline = ch.pipeline();
//				pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
//				pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
				//decode
//				pipeline.addLast(new ProtobufVarint32FrameDecoder());

				pipeline.addLast(new MsgResponseDecoder());
				pipeline.addLast(new ProtobufDecoder(MsgProtocol.MsgResponse.getDefaultInstance()));
//				pipeline.addLast(new ProtobufDecoder(MsgProtocol.MsgRequest.getDefaultInstance()));
				//encode
//				pipeline.addLast(new ProtobufVarint32LengthFieldPrepender());
				pipeline.addLast(new ProtobufEncoder());
				pipeline.addLast(new MsgRequestEncoder());
				pipeline.addLast("handler", new TestClientHandler_());
			}
		});
		Channel channel = b.connect(InitConfig.HOST, InitConfig.PORT_MAIN).sync().channel();
		TestClientHandler handler = channel.pipeline().get(TestClientHandler.class);

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		for (;;) {
			String line = in.readLine();
			if (line == null) {
				break;
			}
			System.out.println("this is line end...");

			MsgProtocol.MsgRequest.Builder builder = MsgProtocol.MsgRequest.newBuilder();
			String msg_id = "id_" + (int) (Math.random() * 100);
			MsgProtocol.Content.Builder content_builder = MsgProtocol.Content.newBuilder();
			content_builder.setBody(line);
			builder.setId(msg_id);
			builder.setContent(content_builder.build());
			channel.writeAndFlush(builder.build());
		}
	}

}
