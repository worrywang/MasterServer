package com.mars.masterserver.core.handler;

import com.mars.masterserver.core.domain.GameRequest;
import com.mars.masterserver.core.domain.GameResponse;
import com.mars.masterserver.net.decoder.MsgProtocol;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/23.
 */
public abstract class ServerMsgHandler implements GameHandler{
	protected ChannelGroup channelGroup;
	protected ChannelGroup mainChannelGroup;
	protected HashMap<MsgProtocol.SRCType,HashMap<String,Channel>> channelHashMap;
	public abstract void execute(Map<Object,Object> model,GameRequest request,GameResponse response);
	public void execute(GameRequest request, GameResponse response) {
		Map<Object,Object> model = new HashMap<Object, Object>();
		execute(model,request,response);
	}

	public void setChannelGroup(ChannelGroup mainChannelGroup,ChannelGroup channelGroup) {
		this.channelGroup = channelGroup;
		this.mainChannelGroup = mainChannelGroup;
	}

	public void setChannelHashMap(HashMap<MsgProtocol.SRCType,HashMap<String,Channel>> channelHashMap){
		this.channelHashMap = channelHashMap;
	}
}
