package com.mars.masterserver.core.domain;


import com.mars.masterserver.net.decoder.MsgProtocol;
import io.netty.channel.Channel;

import java.util.List;
import java.util.zip.GZIPInputStream;

/**
 * masterServer核心的消息存储机制采用GameRequest结构
 * Created by Administrator on 2015/12/23.
 */
public class GameRequest {
//	private String id;
//private String content;
	private Head head;
	private List<Msg> body;
	private Channel channel;

	public GameRequest(Channel channel,Head head,List<Msg> body){
		if(channel!=null&&head!=null&&body!=null&&body.size()>0) {
			//todo： 创建请求对象
			this.head= head;
			this.channel = channel;
			this.body = body;
		}
	}

//	public GameRequest(Channel channel,Head head,Msg msg){
//
//	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

//	public String getContent() {
//		return content;
//	}
//
//	public void setContent(String content) {
//		this.content = content;
//	}


	public List<Msg> getBody() {
		return body;
	}

	public void setBody(List<Msg> body) {
		this.body = body;
	}

	public void deleteBody(){
		if(body!=null){
			body.clear();
			body = null;
		}
	}

	public Head getHead() {
		return head;
	}

	public void setHead(Head head) {
		this.head = head;
	}

	//	public String getId() {
//		return id;
//	}
//
//	public void setId(String id) {
//		this.id = id;
//	}
}
