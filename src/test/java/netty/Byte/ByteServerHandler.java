package netty.Byte;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.SlicedByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;


/**
 * Created by Administrator on 2015/12/10.
 */
public class ByteServerHandler extends ChannelInboundHandlerAdapter {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf byteBuffer=(ByteBuf)msg;
		byte[] result = new byte[byteBuffer.readableBytes()];
		byteBuffer.readBytes(result);
		String resultStr = new String(result);
		System.out.println("channelRead0 "+resultStr);
		//释放资源
		byteBuffer.release();
		String sendMsg = "yes, server is accepted you , nice!\n";
		ByteBuf encoded = ctx.alloc().buffer(4*sendMsg.length());
		encoded.writeBytes(sendMsg.getBytes());
		ctx.channel().writeAndFlush(encoded);
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		System.out.println("has a client");
		ctx.channel().writeAndFlush("yes, server is accepted you , nice! ");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx,
	                            Throwable cause) throws Exception {
		cause.printStackTrace();
		ctx.close();
	}

}
