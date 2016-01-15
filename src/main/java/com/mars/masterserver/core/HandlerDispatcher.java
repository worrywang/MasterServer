package com.mars.masterserver.core;

import com.mars.masterserver.config.InitConfig;
import com.mars.masterserver.config.Settings;
import com.mars.masterserver.core.domain.*;
import com.mars.masterserver.core.handler.GameHandler;
import com.mars.masterserver.core.handler.InitHandler;
import com.mars.masterserver.core.handler.MainInitHandler;
import com.mars.masterserver.net.decoder.MsgProtocol;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.util.LinkedList;
import java.util.List;
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
    //存储接收的对应客户端信息
	private MessageQueueHandler mainMessageQueueHandler,messageQueueHandler;



	private static HandlerDispatcher instance = new HandlerDispatcher();
	private HandlerDispatcher(){
		main_channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
		channels = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
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
					handler.setChannelGroup(channels);
					break;
				default:
					handler = InitHandler.getInstance();
					handler.setChannelGroup(main_channels);
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
			for(GameResponse response:gameResponses){
				writeAndFlushResponse(response);
				response = null;
			}

			request = null;
			gameResponses.clear();
			gameResponses = null;

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
					MsgProtocol.MsgResponse.Builder builder = MsgProtocol.MsgResponse.newBuilder();
					MsgProtocol.Content.Builder content_builder = MsgProtocol.Content.newBuilder();
					MsgProtocol.MsgResponse msgResponse = response.getMsgResponse();
					content_builder.setBody(msgResponse.getContent().getBody());
					builder.setId(msgResponse.getId());
					builder.setContent(content_builder.build());
					channel.writeAndFlush(builder.build());
					break;
				case JSON: //todo: 后续添加其他协议解析
					break;
				default:
					break;
			}
		}
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
