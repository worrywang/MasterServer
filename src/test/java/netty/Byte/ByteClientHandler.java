package netty.Byte;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Created by Administrator on 2015/12/10.
 */
public class ByteClientHandler extends ChannelInboundHandlerAdapter{
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf byteBuf = (ByteBuf)msg;
		byte[] result = new byte[byteBuf.readableBytes()];
		byteBuf.readBytes(result);
		String resultStr = new String(result);
		System.out.println("client接收到服务器返回消息： "+resultStr);
		byteBuf.release();
//		ctx.write(msg);
//		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//		super.exceptionCaught(ctx, cause);
		cause.printStackTrace();
	}
}
