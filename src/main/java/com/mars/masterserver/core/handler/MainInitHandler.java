package com.mars.masterserver.core.handler;

import com.mars.masterserver.core.domain.GameRequest;
import com.mars.masterserver.core.domain.GameResponse;
import io.netty.channel.Channel;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/12/24.
 */
public class MainInitHandler extends ServerMsgHandler {
	private static MainInitHandler initHandler = new MainInitHandler();
	private MainInitHandler(){}
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
			//todo: 根据不同的要求，给不同channel发送不同信息
		}
		return gameResponses;
	}
}
