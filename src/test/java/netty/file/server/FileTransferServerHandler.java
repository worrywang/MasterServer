package netty.file.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import netty.file.model.RequestFile;
import netty.file.model.ResponseFile;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by Administrator on 2016/1/15.
 */
public class FileTransferServerHandler extends ChannelInboundHandlerAdapter{
	private volatile int byteRead;
	private volatile long start = 0;

	/**
	 * 文件默认存储位置
	 */
	private String file_dir = "f://";

	private RandomAccessFile randomAccessFile;
	private File file ;
	private long fileSize = -1 ;

	public FileTransferServerHandler(){
		System.out.println("FileTransferServerHandler");
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//		super.channelRead(ctx, msg);
		System.out.println("FileTransferServerHandler. this is channel read....");
		if(msg instanceof RequestFile){
			RequestFile ef = (RequestFile)msg;
			byte[] bytes = ef.getBytes();
			byteRead = ef.getEndPos();

			String md5 = ef.getFile_md5();

			if(start==0){
				String path = file_dir+File.separator+md5+ef.getFile_type();
				file = new File(path);
				fileSize = ef.getFile_size();

				//根据MD5和文件类型 来确定是否存在这样的文件 如果存在就 秒传
				if(file.exists()){
					ResponseFile responseFile = new ResponseFile(start,md5,getFilePath());
					ctx.writeAndFlush(responseFile);
					return;
				}
				randomAccessFile = new RandomAccessFile(file,"rw");
			}
			randomAccessFile.seek(start);
			randomAccessFile.write(bytes);
			start = start+byteRead;

			if(byteRead>0&&(start<fileSize&&fileSize!=-1)){
				ResponseFile responseFile = new ResponseFile(start,md5,(start*100)/fileSize);
				responseFile.setFile_url(getFilePath());
				ctx.writeAndFlush(responseFile);
			}else{
				System.out.println("create file success。。。。");
				ResponseFile responseFile = new ResponseFile(start,md5,getFilePath());
				ctx.writeAndFlush(responseFile);

				randomAccessFile.close();
				file = null;
				fileSize = -1;
				randomAccessFile = null;
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//		super.exceptionCaught(ctx, cause);
		cause.printStackTrace();

		//当连接断开的时候，关闭未关闭的文件流
		if(randomAccessFile!=null){
			try {
				randomAccessFile.close();
			}catch (IOException e){
				e.printStackTrace();
			}
		}
		ctx.close();
	}

	private String getFilePath(){
		if(file!=null){
			return "f:/"+"/"+file.getName();
		}else
			return null;
	}
}
