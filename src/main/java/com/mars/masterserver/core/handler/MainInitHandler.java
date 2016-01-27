package com.mars.masterserver.core.handler;

import com.mars.masterserver.core.domain.GameRequest;
import com.mars.masterserver.core.domain.GameResponse;
import com.mars.masterserver.core.domain.Msg;
import com.mars.masterserver.net.decoder.MsgProtocol;
import io.netty.channel.Channel;

import java.util.*;

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
		List<GameResponse> gameResponses;

		//todo: 根据不同消息类型进行处理回复,ST全回复，EE只回复给APP端,其他消息默认全回复客户端
		//由于仿真端的信息要转发给所有客户端
		List<Msg> msgList = request.getBody();
		if(msgList!=null&&msgList.size()>0){
			HashMap<Channel,GameResponse> gameResponseHashMap = new HashMap<Channel, GameResponse>();
			for(Msg currentMsg:msgList){
				switch (currentMsg.getMsgType()){
					case Environment: //环境信息转发给所有APP端
						HashMap<String,Channel> app_client = channelHashMap.get(MsgProtocol.SRCType.APPC);
						for(Map.Entry<String,Channel> entry:app_client.entrySet()){
							Channel ch = entry.getValue();
								if(gameResponseHashMap.containsKey(ch)){
									gameResponseHashMap.get(ch).addMsg(currentMsg);
								}else{
									GameResponse gameresponse = new GameResponse(request.getHead());
									gameresponse.setChannel(ch);
									gameresponse.addMsg(currentMsg);
									gameResponseHashMap.put(ch,gameresponse);
							}
						}
						break;
					case StateTransfer: //状态迁移事件转发给所有客户端
						for(Channel ch:channelGroup){
							if(gameResponseHashMap.containsKey(ch)){
								gameResponseHashMap.get(ch).addMsg(currentMsg);
							}else{
								GameResponse gameresponse = new GameResponse(request.getHead());
								gameresponse.setChannel(ch);
								gameresponse.addMsg(currentMsg);
								gameResponseHashMap.put(ch,gameresponse);
							}
						}
						break;
					default:
						for(Channel ch:channelGroup){
							if(gameResponseHashMap.containsKey(ch)){
								gameResponseHashMap.get(ch).addMsg(currentMsg);
							}else{
								GameResponse gameresponse = new GameResponse(request.getHead());
								gameresponse.setChannel(ch);
								gameresponse.addMsg(currentMsg);
								gameResponseHashMap.put(ch,gameresponse);
							}
						}
						break;
				}
			}
			if(gameResponseHashMap.size()>0){
				gameResponses  = new ArrayList<GameResponse>(gameResponseHashMap.values());
				return gameResponses;
			}
			return null;
		}
		return null;
	}
}
