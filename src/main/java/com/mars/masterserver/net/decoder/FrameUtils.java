package com.mars.masterserver.net.decoder;

import com.mars.masterserver.config.InitConfig;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * Created by Administrator on 2015/12/8.
 */
public class FrameUtils {

	/**
	 * 对传输消息的编码解码进行封装
	 * @param pipeline
	 * @param frameType
	 * @param serializationType
	 */
	public static void setDecoderAndEncoder(ChannelPipeline pipeline, InitConfig.FrameType frameType,InitConfig.SerializationType serializationType){
		setFrameType(pipeline, frameType, serializationType);
	}

	private static void setFrameType(ChannelPipeline pipeline,InitConfig.FrameType type,InitConfig.SerializationType serializationType){
		switch (type){
			case Delimiter:
				pipeline.addLast("framer",new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()));
				setMsgType(pipeline, serializationType);
				break;
			case LengthField:
				pipeline.addLast("frameDecoder",new LengthFieldBasedFrameDecoder(InitConfig.MAX_FRAME_LENGTH,0,4,0,4));
				setMsgType(pipeline, serializationType);
				pipeline.addLast("frameEncoder",new LengthFieldPrepender(4));
				break;
			case UserDefine:
				pipeline.addLast(new MsgRequestDecoder());
				setMsgType(pipeline, serializationType);
				pipeline.addLast(new MsgResponseEncoder());

				break;
			default:
				break;
		}
	}

	private static void setMsgType(ChannelPipeline pipeline, InitConfig.SerializationType type){
		switch (type){
			case PROTOBUF:
				pipeline.addLast(new ProtobufDecoder(MsgProtocol.MsgRequest.getDefaultInstance()));
//				pipeline.addLast(new ProtobufDecoder(MsgProtocol.MsgResponse.getDefaultInstance()));
				pipeline.addLast(new ProtobufEncoder());
				break;
			case JSON:
				break;
			case String:
				pipeline.addLast("decoder",new StringDecoder());
				pipeline.addLast("encoder",new StringEncoder());
			default:
				break;
		}
	}
//	private static void setSerializationType(ChannelPipeline pipeline,InitConfig.SerializationType type){
//		switch (type){
//			case JSON:
//				break;
//			case PROTOBUF:
//				break;
//			default:
//				break;
//		}
//	}
}
