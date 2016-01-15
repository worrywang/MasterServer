package com.mars.masterserver.core.handler;

import com.mars.masterserver.core.domain.GameRequest;
import com.mars.masterserver.core.domain.GameResponse;
import io.netty.channel.Channel;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/23.
 */
public class InitHandler extends ServerMsgHandler{
	private static InitHandler initHandler = new InitHandler();
	private InitHandler(){}
	public static GameHandler getInstance(){
		return initHandler;
	}

	@Override
	public void execute(Map<Object, Object> model, GameRequest request, GameResponse response) {

	}

	public List<GameResponse> execute(GameRequest request) {
		List<GameResponse> gameResponses = new LinkedList<GameResponse>();
		for(Channel channel:channelGroup){
			GameResponse gameResponse = new GameResponse(request);
			gameResponse.setChannel(channel);
			gameResponses.add(gameResponse);
		}
		return gameResponses;
	}
}
