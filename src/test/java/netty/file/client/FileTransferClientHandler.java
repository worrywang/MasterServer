package netty.file.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.file.model.RequestFile;
import netty.file.model.ResponseFile;
import netty.file.model.SecureModel;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Administrator on 2016/1/15.
 */
public class FileTransferClientHandler extends ChannelInboundHandlerAdapter {
	private volatile int byteRead;
	private volatile long start = 0;
	public RandomAccessFile randomAccessFile;
	private RequestFile request;
	private final int minReadBufferSize = 8192;

	public FileTransferClientHandler(RequestFile ef){
		if(ef.getFile().exists()){
			if(!ef.getFile().isFile()){
				System.out.println("Not a file : "+ef.getFile());
				return;
			}
		}
		this.request = ef;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
//		super.channelActive(ctx);
		System.out.println("FileTransferServerHandler. this is channel active....");
		SecureModel secure = new SecureModel();
		secure.setToken("22222222222");
		ctx.writeAndFlush(secure);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//		super.channelRead(ctx, msg);
		System.out.println("FileTransferClientHandler. this is channel read....");
		if(msg instanceof SecureModel){
			try {
				randomAccessFile = new RandomAccessFile(request.getFile(),"r");
				randomAccessFile.seek(request.getStarPos());
				byte[] bytes = new byte[minReadBufferSize];
				if((byteRead=randomAccessFile.read(bytes))!=-1){
					request.setEndPos(byteRead);
					request.setBytes(bytes);
					request.setFile_size(randomAccessFile.length());
					ctx.writeAndFlush(request);
				}else{
					System.out.println("文件已经读完");
				}
			}catch (FileNotFoundException e){
				e.printStackTrace();
			}catch (IOException i){
				i.printStackTrace();
			}
			return;
		}else if(msg instanceof ResponseFile){
			ResponseFile response = (ResponseFile)msg;
			System.out.println(response.toString());
			if(response.isEnd()){
				randomAccessFile.close();
			}else{
				start = response.getStart();
				if(start!=-1){
					randomAccessFile = new RandomAccessFile(request.getFile(),"r");
					randomAccessFile.seek(start);
					int a = (int)(randomAccessFile.length()-start);
					int sendLength = minReadBufferSize;
					if(a<minReadBufferSize) sendLength = a;
					byte[] bytes = new byte[sendLength];
					if((byteRead=randomAccessFile.read(bytes))!=-1&&(randomAccessFile.length()-start)>0){
						request.setEndPos(byteRead);
						request.setBytes(bytes);
						ctx.writeAndFlush(request);
					}else{
						randomAccessFile.close();
						ctx.close();
					}
				}
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		ctx.close();
	}
}
