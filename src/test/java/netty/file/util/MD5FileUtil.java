package netty.file.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

/**
 * Created by Administrator on 2016/1/15.
 */
public class MD5FileUtil {
	protected static char hexDigits[] = {'0','1','2','3','4','5','6','7','8','9',
			'a','b','c','d','e','f'};

	protected static MessageDigest messageDigest = null;

	static {
		try {
			messageDigest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
//			e.printStackTrace();
			System.out.println("MD5FileUtil messageDigest初始化失败");
		}
	}

	/**
	 * 获取文件的MD5
	 * @param file
	 * @return MD5值
	 * @throws IOException
	 */
	public synchronized static String getFileMD5String(File file) throws IOException{
		FileInputStream in = new FileInputStream(file);
		FileChannel ch = in.getChannel();
		MappedByteBuffer byteBuffer = ch.map(FileChannel.MapMode.READ_ONLY,0,file.length());
		messageDigest.update(byteBuffer);
		return bufferToHex(messageDigest.digest());
	}

	private static String bufferToHex(byte bytes[]){
		return bufferToHex(bytes,0,bytes.length);
	}

	private static String bufferToHex(byte bytes[],int m,int n){
		StringBuffer stringBuffer = new StringBuffer(2*n);
		int k = m+n;
		for(int l=m;l<k;l++){
			appedHexPair(bytes[l],stringBuffer);
		}
		return stringBuffer.toString();
	}

	private static void appedHexPair(byte bt,StringBuffer stringBuffer){
		char c0 = hexDigits[(bt&0xf0)>>4];
		char c1 = hexDigits[(bt&0xf)];
		stringBuffer.append(c0);
		stringBuffer.append(c1);
	}

	//todo ：test code
	public static void main(String[] args) throws IOException{
		long begin  =System.currentTimeMillis();

		File big = new File("E:\\实验室\\IMG_1520.MOV");
		String md5 = getFileMD5String(big);

		long end = System.currentTimeMillis();
		System.out.println("md5: "+md5);
		System.out.println("time: "+(end-begin));

		Scanner sc = new Scanner(System.in);
		while(sc.hasNext()){
			String path = sc.next();
			begin  =System.currentTimeMillis();
			try {
				big = new File(path);
				md5 = getFileMD5String(big);

				end = System.currentTimeMillis();
				System.out.println("md5: " + md5);
				System.out.println("time: " + (end - begin));
			}catch (IOException e){
				e.printStackTrace();
			}
		}
	}
}
