package netty.file.invalid;

/**
 * Created by Administrator on 2016/1/11.
 */
public class FileTools {

	public static void PrintBytes(byte[] bytes,int length){
		for(int i=0;i<length;i++){
			System.out.print(bytes[i]);
		}
		System.out.println();
	}
}
