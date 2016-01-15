package com.mars.masterserver.core.domain;

import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Administrator on 2015/12/23.
 */
public class MessageQueueHandler {
	protected Map<Channel,MessageQueue> messageQueueMap;

	public MessageQueueHandler(){
		messageQueueMap = new ConcurrentHashMap<Channel, MessageQueue>();
	}

	public void addMessageQueue(Channel channel){
		if(channel!=null){
			MessageQueue messageQueue = new MessageQueue(new ConcurrentLinkedQueue<GameRequest>());
			messageQueueMap.put(channel,messageQueue);
		}
	}

	public boolean addMessage(GameRequest gameRequest){
		boolean added = false;
		if(gameRequest!=null){
			Channel channel=gameRequest.getChannel();
			if(channel!=null) {
				MessageQueue messageQueue = messageQueueMap.get(channel);
				if(messageQueue!=null){
					added = messageQueue.add(gameRequest);
					return added;
				}else{
					//若queue为空，表示channel已经关闭，不使用
					messageQueueMap.remove(channel);
					channel.close();
				}
			}
		}
		return added;
	}

	public boolean checkMessageQueue(Channel channel){
		return messageQueueMap.containsKey(channel);
	}

	public void removeMessageQueue(Channel channel){
		MessageQueue queue = messageQueueMap.remove(channel);
		if(queue!=null){
			queue.clear();
		}
	}

	public Map<Channel, MessageQueue> getMessageQueueMap() {
		return messageQueueMap;
	}

//	protected abstract void handMessageQueue();

}
