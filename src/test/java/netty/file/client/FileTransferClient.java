package netty.file.client;

import com.mars.masterserver.config.InitConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import netty.file.code.NettyMessageDecoder;
import netty.file.code.NettyMessageEncoder;
import netty.file.model.RequestFile;
import netty.file.util.MD5FileUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2016/1/15.
 */
public class FileTransferClient {

	public void connect(int port,String host, final RequestFile echoFile) throws Exception{
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			Bootstrap b= new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY,true)
					.handler(new ChannelInitializer<SocketChannel>() {
						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							ch.pipeline().addLast(new ObjectEncoder());
							ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null)));

							ch.pipeline().addLast(new NettyMessageDecoder());
							ch.pipeline().addLast(new NettyMessageEncoder());
							ch.pipeline().addLast(new FileTransferClientHandler(echoFile));
						}
					});
			ChannelFuture f = b.connect(host, port).sync();
			f.channel().closeFuture().sync();
		}finally {
			group.shutdownGracefully();
		}
	}

	public static void main(String[] args) throws Exception{
		int port =10012;


		try {
			RequestFile echo = new RequestFile();
			File file  = new File("E://learning/雨松MOMO安卓博客课程.rar");
			String fileName = file.getName();
			echo.setFile(file);
			echo.setFile_md5(MD5FileUtil.getFileMD5String(file));
			echo.setFile_name(fileName);
			echo.setFile_type(getSuffix(fileName));
			echo.setStarPos(0); //文件开始位置

			new FileTransferClient().connect(port, "127.0.0.1", echo);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static String getSuffix(String fileName)
	{
		String fileType = fileName.substring(fileName.lastIndexOf("."), fileName.length());
		return fileType;
	}
}
