package netty;

import com.mars.masterserver.config.InitConfig;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * Created by Administrator on 2015/12/4.
 */
public class ClientTestHandler extends ChannelInboundHandlerAdapter {
	/**
	 * Creates a client-side handler.
	 */

	private final ByteBuf firstMessage;
	public ClientTestHandler() {
		firstMessage = Unpooled.buffer(InitConfig.SIZE);
		for (int i = 0; i < firstMessage.capacity(); i ++) {
			firstMessage.writeByte((byte) i);
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		ctx.writeAndFlush(firstMessage);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		ctx.write(msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		// Close the connection when an exception is raised.
		cause.printStackTrace();
		ctx.close();
	}
}
