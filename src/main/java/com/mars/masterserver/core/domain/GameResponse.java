package com.mars.masterserver.core.domain;

import com.mars.masterserver.net.decoder.MsgProtocol;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/23.
 */
public class GameResponse {
//	private String id;
//	private String content;
	private Head head;
	private List<Msg> body;
	private Channel channel;

	public GameResponse(GameRequest gameRequest){
		this.head = gameRequest.getHead();
		this.body = gameRequest.getBody();
		this.channel = gameRequest.getChannel();
	}

	public GameResponse(Head head){
		this.head = head;
		this.body = new ArrayList<Msg>();
	}

//	public MsgProtocol.MsgResponse getMsgResponse(){
//		MsgProtocol.MsgResponse.Builder builder = MsgProtocol.MsgResponse.newBuilder();
//
//		builder.setId(id);
//		builder.setContent(MsgProtocol.Content.newBuilder().setBody(content).build());
//		return builder.build();
//	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public List<Msg> getBody() {
		return body;
	}

	public void setBody(List<Msg> body) {
		this.body = body;
	}

	public Head getHead() {
		return head;
	}

	public void setHead(Head head) {
		this.head = head;
	}

	public void addMsg(Msg msg){
		body.add(msg);
	}

	//	public String getContent() {
//		return content;
//	}
//
//	public void setContent(String content) {
//		this.content = content;
//	}
//
//	public String getId() {
//		return id;
//	}
//
//	public void setId(String id) {
//		this.id = id;
//	}
}
