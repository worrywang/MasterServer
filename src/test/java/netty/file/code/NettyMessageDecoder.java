package netty.file.code;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import netty.file.util.ObjectConvertUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/1/15.
 */
public class NettyMessageDecoder extends MessageToMessageDecoder<String> {
	@Override
	protected void decode(ChannelHandlerContext ctx, String msg, List<Object> out) throws Exception {
		Object outobj = ObjectConvertUtil.convertModle(msg);
		out.add(outobj);
	}
}
