package com.mars.masterserver.core.domain;

import com.mars.masterserver.net.decoder.MsgProtocol;
import io.netty.channel.Channel;

import java.util.zip.GZIPInputStream;

/**
 * masterServer核心的消息存储机制采用GameRequest结构
 * Created by Administrator on 2015/12/23.
 */
public class GameRequest {
	private String id;
	private String content;
	private Channel channel;

	public GameRequest(Channel channel,MsgProtocol.MsgRequest msgRequest){
		if(channel!=null&&msgRequest!=null){
			//todo： 创建请求对象
			this.channel = channel;
			this.id = msgRequest.getId();
			this.content = msgRequest.getContent().getBody();
		}
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
