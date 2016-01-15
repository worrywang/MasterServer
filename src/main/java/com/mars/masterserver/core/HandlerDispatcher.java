package com.mars.masterserver.core;

import com.mars.masterserver.config.InitConfig;
import com.mars.masterserver.config.Settings;
import com.mars.masterserver.core.domain.*;
import com.mars.masterserver.core.handler.GameHandler;
import com.mars.masterserver.core.handler.InitHandler;
import com.mars.masterserver.core.handler.MainInitHandler;
import com.mars.masterserver.net.decoder.MsgProtocol;
import com.mars.masterserver.net.handler.ProtocolConverMsgUtil;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.*;
import java.util.concurrent.Executor;

/**
 * Created by Administrator on 2015/12/4.
 * 核心逻辑处理器
 */
public class HandlerDispatcher implements Runnable {


	private Executor messageExecutor;
	private long sleepTime = 200;
	private boolean running;

	//逻辑处理端channel
	private final ChannelGroup main_channels;
	//控制端channel
	private final ChannelGroup channels;
	//对应不同的client类型进行channel存储
	private final HashMap<MsgProtocol.SRCType,HashMap<String,Channel>> channels_hashmap;

    //存储接收的对应客户端信息
	private MessageQueueHandler mainMessageQueueHandler,messageQueueHandler;


	private static HandlerDispatcher instance = new HandlerDispatcher();
	private HandlerDispatcher(){
		main_channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
		channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
		channels_hashmap = new HashMap<MsgProtocol.SRCType, HashMap<String, Channel>>();
		for(MsgProtocol.SRCType srcType: MsgProtocol.SRCType.values()){
			channels_hashmap.put(srcType,new HashMap<String, Channel>());
		}
	}

	public static HandlerDispatcher getInstance(){
		synchronized (instance) {
			if (instance == null) {
				instance = new HandlerDispatcher();
			}
		}
		return instance;
	}

	public void init(){
		if(!running){
			running = true;
			mainMessageQueueHandler = new MessageQueueHandler();
			messageQueueHandler = new MessageQueueHandler();
			messageExecutor = new FixThreadPoolExecutor(Settings.corePoolSize,Settings.maximumPoolSize,Settings.keepAliveSecond);
		}
	}

	public void stop(){running = false;}

	public void run() {
		//TODO： 消息转发
		while (running){
			for(Channel channel:main_channels){
				MessageQueue messageQueue = mainMessageQueueHandler.getMessageQueueMap().get(channel);
//				if(messageQueue!=null&&messageQueue.size()>0){
//					messageQueue.printAll();
//				}
				if(messageQueue==null||messageQueue.size()<=0||messageQueue.isRunning())
					continue;
				MessageWorker messageWorker = new MessageWorker(channel,messageQueue, InitConfig.ClientType.MASTER);
				this.messageExecutor.execute(messageWorker);
			}

			for(Channel channel:channels){
				MessageQueue messageQueue = messageQueueHandler.getMessageQueueMap().get(channel);
				if(messageQueue==null||messageQueue.size()<=0||messageQueue.isRunning())
					continue;
				MessageWorker messageWorker = new MessageWorker(channel,messageQueue, InitConfig.ClientType.COMMON);
				this.messageExecutor.execute(messageWorker);
			}

			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				System.err.println("thread sleep throw an exception...");
			}
		}

	}

	/**
	 * 消息队列处理线程实现
	 */
	private final class MessageWorker implements Runnable{

		private MessageQueue messageQueue;
		private Channel channel;
		private GameRequest request;
		private InitConfig.ClientType clientType;
		private MessageWorker(Channel channel,MessageQueue messageQueue,InitConfig.ClientType clientType){
			this.messageQueue = messageQueue;
			this.channel = channel;
			this.clientType = clientType;
			messageQueue.setRunning(true);
			request = messageQueue.getRequestQueue().poll();
		}
		public void run() {
			try {
				handMessageQueue();
			} finally {
				messageQueue.setRunning(false);
			}
		}

		/**
		 * 处理消息队列
		 */
		private void handMessageQueue(){
			GameHandler handler;
			switch (clientType){
				case MASTER:
					handler = MainInitHandler.getInstance();
					handler.setChannelGroup(main_channels,channels);
					handler.setChannelHashMap(channels_hashmap);
					break;
				default:
					handler = InitHandler.getInstance();
					handler.setChannelGroup(main_channels,channels);
					handler.setChannelHashMap(channels_hashmap);
					break;
			}

			List<GameResponse> gameResponses = null;
			long start = System.currentTimeMillis();
			System.out.println("HandlerDispatcher.handMessageQueue() 開始處理協議--["+channel.remoteAddress()+"]");
			try {
				gameResponses=handler.execute(request);
			} catch (Exception e) {
//				e.printStackTrace();
			}
			System.out.println("HandlerDispatcher.handMessageQueue() 協議處理完成--["+channel.remoteAddress()+"]");
			long end = System.currentTimeMillis();
			long diff = (end-start);
			if(diff>=200){
				System.out.println("逻辑处理时间过长，处理时间（s）："+diff);
			}
			//写入协议
			if(gameResponses!=null&&gameResponses.size()>0) {
				for (GameResponse response : gameResponses) {
					writeAndFlushResponse(response);
					response = null;
				}
				gameResponses.clear();
				gameResponses = null;
			}
			request = null;


		}
	}

	/**
	 * 对不同的协议进行封装后返回
	 * @param response
	 */
	private static void writeAndFlushResponse(GameResponse response){
		if(response!=null) {
			switch (Settings.currentSerializationType) {
				case PROTOBUF:
					Channel channel = response.getChannel();
					MsgProtocol.MsgResponse msgResponse = ProtocolConverMsgUtil.convertMsgResponse(response);
					channel.writeAndFlush(msgResponse);
					break;
				case JSON: //todo: 后续添加其他协议解析
					break;
				default:
					break;
			}
		}
	}

	/**
	 * 对没有存储ID的客户端进行分类存储
	 * @param head
	 * @param channel
	 */
	public void headMsgHandler(MsgProtocol.Head head, Channel channel){
		if(head!=null){
			MsgProtocol.SRCType currentSRCType = head.getSrcType();
			HashMap<String,Channel> clientChannelHashMap = channels_hashmap.get(currentSRCType);
			String srcID = head.getSrcID();
			if(!clientChannelHashMap.containsKey(srcID)){
				clientChannelHashMap.put(srcID,channel);
			}
		}
	}

	private void deleteChannelID(Channel channel){
		for(Map.Entry<MsgProtocol.SRCType,HashMap<String,Channel>> entry: channels_hashmap.entrySet()){
			HashMap<String,Channel> channel_id = entry.getValue();
			List<String> delete_ids = new ArrayList<String>();
			for(Map.Entry<String,Channel> entry1: channel_id.entrySet()){
				Channel current = entry1.getValue();
				if(current.equals(channel)){
					delete_ids.add(entry1.getKey());
				}
			}
			for(String id:delete_ids){
				deleteChannelID(id);
			}
		}
	}

	private void deleteChannelID(MsgProtocol.SRCType srcType,String id){
		HashMap<String,Channel> clientChannelHashMap = channels_hashmap.get(srcType);
		clientChannelHashMap.remove(id);
	}

	private void deleteChannelID(String id){
		for(Map.Entry<MsgProtocol.SRCType,HashMap<String,Channel>> entry: channels_hashmap.entrySet()){
			HashMap channel_id = entry.getValue();
			channel_id.remove(id);
		}

	}

	public void removeChannelInfo(Channel channel){
		messageQueueHandler.removeMessageQueue(channel);
		deleteChannelID(channel);
	}

	public void removeMainChannelInfo(Channel channel){
		mainMessageQueueHandler.removeMessageQueue(channel);
		deleteChannelID(channel);
	}

	public void setMessageExecutor(Executor messageExecutor) {
		this.messageExecutor = messageExecutor;
	}

	public void setSleepTime(long sleepTime) {
		this.sleepTime = sleepTime;
	}

	public void addMainChannel(Channel channel){
		if(channel!=null) main_channels.add(channel);
	}

	public void addChannel(Channel channel){
		if(channel!=null) channels.add(channel);
	}

	public ChannelGroup getChannels() {
		return channels;
	}

	public ChannelGroup getMain_channels() {
		return main_channels;
	}

	public MessageQueueHandler getMainMessageQueueHandler() {
		return mainMessageQueueHandler;
	}

	public MessageQueueHandler getMessageQueueHandler() {
		return messageQueueHandler;
	}


}
