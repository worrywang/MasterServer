package com.mars.masterserver.core.domain;

import com.mars.masterserver.net.decoder.MsgProtocol;
import io.netty.channel.Channel;

/**
 * Created by Administrator on 2015/12/23.
 */
public class GameResponse {
	private String id;
	private String content;
	private Channel channel;

	public GameResponse(GameRequest gameRequest){
		this.id = gameRequest.getId();
		this.content = gameRequest.getContent();
		this.channel = gameRequest.getChannel();
	}


	public MsgProtocol.MsgResponse getMsgResponse(){
		MsgProtocol.MsgResponse.Builder builder = MsgProtocol.MsgResponse.newBuilder();
		builder.setId(id);
		builder.setContent(MsgProtocol.Content.newBuilder().setBody(content).build());
		return builder.build();
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
