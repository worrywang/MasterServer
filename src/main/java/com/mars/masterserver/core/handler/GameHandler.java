package com.mars.masterserver.core.handler;

import com.mars.masterserver.core.domain.GameRequest;
import com.mars.masterserver.core.domain.GameResponse;
import io.netty.channel.group.ChannelGroup;

import java.util.List;

/**
 * Created by Administrator on 2015/12/23.
 */
public interface GameHandler {
	void execute(GameRequest request,GameResponse response);
	List<GameResponse> execute(GameRequest request);
	void setChannelGroup(ChannelGroup channelGroup);
}
