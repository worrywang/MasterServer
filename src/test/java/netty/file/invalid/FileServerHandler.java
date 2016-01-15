package netty.file.invalid;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Administrator on 2016/1/11.
 */
public class FileServerHandler extends SimpleChannelInboundHandler {
//	private String file_path = "f:/jiaming2.jpg";
private String file_path = "F:/15 私になりたい私2.mp3"; //写入的详细文件地址和文件名
	private File file;
	private FileOutputStream fos;
	byte[] over_flat;
	public FileServerHandler() {
		over_flat = FileClientHandler.OVER_FLAG.getBytes();
		try {

			file = new File(file_path);
			if (!file.exists()) {
				file.createNewFile();
			} else {
				file.delete();
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
		}catch (IOException e){
			e.printStackTrace();
		}
	}




	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
		ByteBuf buf = (ByteBuf) msg;
		int length = buf.readableBytes();
		byte[] bytes = new byte[length];

		buf.readBytes(bytes);

		if(length==over_flat.length){
			int i;
			for(i=0;i<length;i++){
				if(over_flat[i]!=bytes[i]) break;
			}
			if(i==length){
				System.out.println("the file is over.");
				fos.close();
				return;
			}
		}

//		//todo: 打印收到的数据
//		System.out.println("the receive info..."+length);
//		FileTools.PrintBytes(bytes,length);


		fos.write(bytes);
		fos.flush();

		buf.clear();
	}
}
