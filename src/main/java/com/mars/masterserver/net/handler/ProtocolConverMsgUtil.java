package com.mars.masterserver.net.handler;

import com.mars.masterserver.core.domain.GameRequest;
import com.mars.masterserver.core.domain.GameResponse;
import com.mars.masterserver.core.domain.Head;
import com.mars.masterserver.core.domain.Msg;
import com.mars.masterserver.net.decoder.MsgProtocol;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/1/18.
 */
public class ProtocolConverMsgUtil {

	public static GameRequest convertGameRequest(Channel channel,MsgProtocol.MsgRequest msgRequest){
		GameRequest result = null;
		if(msgRequest!=null){
			MsgProtocol.Head head = msgRequest.getHead();
			MsgProtocol.Content content = msgRequest.getContent();
			if(head!=null&&content!=null) {
				Head _head = new Head(head);
				List<Msg> _body = new ArrayList<Msg>();
				List<MsgProtocol.Msg>  msgList=content.getMsgList();
				if(msgList!=null&&msgList.size()>0){
					for(MsgProtocol.Msg msg:msgList){
						if(msg!=null){
							Msg _msg = new Msg(msg);
							_body.add(_msg);
						}
					}
					result = new GameRequest(channel,_head,_body);
				}
			}
		}
		return result;
	}


	public static MsgProtocol.MsgResponse convertMsgResponse(GameResponse gameResponse){
		MsgProtocol.MsgResponse.Builder builder = MsgProtocol.MsgResponse.newBuilder();
		MsgProtocol.Head head = convertHead(gameResponse.getHead());
		MsgProtocol.Content content = convertContent(gameResponse.getBody());
		if(head!=null) builder.setHead(head);
		if(content!=null) builder.setContent(content);
		return builder.build();
	}

	public static MsgProtocol.Head convertHead(Head head){
		if(head!=null) {
			MsgProtocol.Head.Builder builder = MsgProtocol.Head.newBuilder();
			builder.setSrcID(head.getSrcID());
			builder.setSrcType(head.getSrcType());
			List<String> ids = head.getDstIDs();
			for(String str:ids){
				builder.addDstIDs(str);
			}
			return builder.build();
		}
		return null;
	}



	public static MsgProtocol.Content convertContent(List<Msg> msgList){
		if(msgList!=null&&msgList.size()>0){
			MsgProtocol.Content.Builder builder = MsgProtocol.Content.newBuilder();
			for(Msg msg:msgList){
				builder.addMsg(msg.get_msg());
			}
			return builder.build();
		}
		return null;
	}

}
