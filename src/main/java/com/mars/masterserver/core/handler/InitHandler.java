package com.mars.masterserver.core.handler;

import com.mars.masterserver.core.domain.GameRequest;
import com.mars.masterserver.core.domain.GameResponse;
import com.mars.masterserver.core.domain.Msg;
import com.mars.masterserver.net.decoder.MsgProtocol;
import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;

import java.util.*;

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
		List<GameResponse> gameResponses;

		//todo: 根据不同消息类型进行处理回复,GA,GR,CE回复给Sim，CommandEvent回复给个别view端
		//由于仿真端的信息要转发给所有客户端
		List<Msg> msgList = request.getBody();
		if(msgList!=null&&msgList.size()>0){
			HashMap<Channel,GameResponse> gameResponseHashMap = new HashMap<Channel, GameResponse>();
			for(Msg currentMsg:msgList){
				switch (currentMsg.getMsgType()){
					case Command: //指挥控制命令转发给所有Unity端
						HashMap<String,Channel> app_client = channelHashMap.get(MsgProtocol.SRCType.UNITYC);
						List<String> dstIDs = request.getHead().getDstIDs();
						if(dstIDs!=null&&dstIDs.size()>0){
							for(String id:dstIDs){
								Channel ch = app_client.get(id);
								if(gameResponseHashMap.containsKey(ch)){
									gameResponseHashMap.get(ch).addMsg(currentMsg);
								}else{
									GameResponse gameresponse = new GameResponse(request.getHead());
									gameresponse.setChannel(ch);
									gameresponse.addMsg(currentMsg);
									gameResponseHashMap.put(ch,gameresponse);
								}
							}
						}
						break;
					default: //默认所有消息转发给服务器端
						for(Channel ch:mainChannelGroup){
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
