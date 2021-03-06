package com.mars.masterserver.test;

import com.google.protobuf.ByteString;
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
				pipeline.addLast("handler", new TestClientHandler());
			}
		});
		Channel channel = b.connect(InitConfig.HOST, InitConfig.PORT_MAIN).sync().channel();
		TestClientHandler handler = channel.pipeline().get(TestClientHandler.class);

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String msg_id = "id_" + (int) (Math.random() * 100);
		for (;;) {
			System.out.println("print msgtype: [ST=7,EE=8]");
			int type = Integer.valueOf(in.readLine());
			System.out.println("print msg:");
			String line = in.readLine();
			if (line == null) {
				break;
			}
			System.out.println("this is line end...");

			MsgProtocol.MsgRequest.Builder builder = MsgProtocol.MsgRequest.newBuilder();
			MsgProtocol.Head.Builder head_builder= MsgProtocol.Head.newBuilder();
			MsgProtocol.Content.Builder content_builder = MsgProtocol.Content.newBuilder();
			head_builder.setSrcID(msg_id);
			head_builder.setSrcType(MsgProtocol.SRCType.SIM);
			MsgProtocol.Msg.Builder msg_builder = MsgProtocol.Msg.newBuilder();
			switch (type){
				//TODO: 1-6项不能使用
//				case 1:
//					msg_builder.setType(MsgProtocol.MsgType.AssetLoad);
//					break;
//				case 2:
//					msg_builder.setType(MsgProtocol.MsgType.AssetUpload);
//					break;
//				case 3:
//					msg_builder.setType(MsgProtocol.MsgType.GameObjectAdd);
//					break;
//				case 4:
//					msg_builder.setType(MsgProtocol.MsgType.GameObjectRemove);
//					break;
//				case 5:
//					msg_builder.setType(MsgProtocol.MsgType.Control);
//					break;
//				case 6:
//					msg_builder.setType(MsgProtocol.MsgType.Command);
//					break;
				case 7:
					msg_builder.setType(MsgProtocol.MsgType.StateTransfer);
					break;
				case 8:
					msg_builder.setType(MsgProtocol.MsgType.Environment);
					break;
					default:
						msg_builder.setType(MsgProtocol.MsgType.Command);
						break;
			}
			ByteString body = ByteString.copyFrom(line.getBytes());
			msg_builder.setBody(body);
			content_builder.addMsg(msg_builder.build());
			builder.setHead(head_builder.build());
			builder.setContent(content_builder.build());
			channel.writeAndFlush(builder.build());
		}
	}

}
