package com.mars.masterserver.config;

/**
 * Created by Administrator on 2015/12/5.
 */
public class Settings {
	public static int corePoolSize = 5;
	public static int maximumPoolSize = 80;
	public static long keepAliveSecond = 300;
	public static long sleepTime = 100;
	public static int HEAD_LENGTH = 4;

	public static InitConfig.Protocol currentProtocol = InitConfig.Protocol.ALL;
	public static InitConfig.FrameType currentFrameType = InitConfig.FrameType.UserDefine;
//	public static InitConfig.MsgType currentMsgType = InitConfig.MsgType.Byte;
	public static InitConfig.SerializationType currentSerializationType = InitConfig.SerializationType.PROTOBUF;
}
