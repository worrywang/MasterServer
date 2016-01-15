package com.mars.masterserver.config;

/**
 * Created by Administrator on 2015/12/4.
 */
public class InitConfig {
//	public static boolean SSL = System.getProperty("ssl")!=null;
public static boolean SSL = false;
//	public static String HOST = System.getProperty("host", "127.0.0.1");
public static String HOST = System.getProperty("host", "192.168.10.105");

	public static int TCP_PORT = Integer.parseInt(System.getProperty("port", "8088"));
	public static int UDP_PORT = Integer.parseInt(System.getProperty("port","8089"));
	public static int PORT_MAIN = Integer.parseInt(System.getProperty("port","8992"));
	public static int SIZE = Integer.parseInt(System.getProperty("size","256"));

	public static int MAX_FRAME_LENGTH = Integer.MAX_VALUE;
//	public static int MAX_FRAME_LENGTH = 1048576;
	public enum ClientType{COMMON,MASTER}
	public enum Protocol{TCP,UDP,ALL}
	public enum FrameType{LengthField,Delimiter,UserDefine}
	public enum MsgType{String,Byte}
	public enum SerializationType{PROTOBUF,JSON,String,Byte,NONE}
}
