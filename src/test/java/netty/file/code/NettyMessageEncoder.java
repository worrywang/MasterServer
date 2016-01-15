package netty.file.code;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import netty.file.util.ObjectConvertUtil;

import java.util.List;

/**
 * Created by Administrator on 2016/1/15.
 */
public class NettyMessageEncoder extends MessageToMessageEncoder<Object> {
	@Override
	protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
		out.add(ObjectConvertUtil.request(msg));
	}
}
