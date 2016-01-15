package netty.file.invalid;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.*;

/**
 * Created by Administrator on 2016/1/11.
 */
public class FileClientHandler extends SimpleChannelInboundHandler {


	private String file_path = "F:/15 私になりたい私.mp3";  //传输的文件详细地址和文件名
	public static  String OVER_FLAG = "EOF";
	private int readLength = 1024;

	private void sentFile(Channel channel) throws IOException{
		File file = new File(file_path);
		FileInputStream fis = new FileInputStream(file);
		int count =0;
		BufferedInputStream bis = new BufferedInputStream(fis);
		for(;;){
			if(bis.available()>readLength){
			}else{
				readLength = bis.available();
				byte[] bytes = new byte[readLength];
				bis.read(bytes, 0, readLength);
				sendToServer(bytes, channel, readLength);
				byte[] over_flat = OVER_FLAG.getBytes();
				sendToServer(over_flat, channel, over_flat.length);
				System.out.println("all file has upload.");
				return;
			}
			byte[] bytes = new byte[readLength];
			bis.read(bytes,0,readLength);
			sendToServer(bytes,channel,readLength);
			System.out.println("Send count: " + ++count);
		}
	}

	private void sendToServer(byte[] bytes,Channel channel,int legth){
		ByteBuf buf = Unpooled.buffer();
		buf.writeBytes(bytes);

//		//todo: 打印收到的数据
//		FileTools.PrintBytes(bytes,legth);
//		channel.writeAndFlush(buf);
	}


	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		super.channelActive(ctx);
		sentFile(ctx.channel());
	}

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

	}
}
