package netty.file.util;


import com.alibaba.fastjson.JSON;
import netty.file.model.*;

/**
 * 传输数据转化包
 * Created by Administrator on 2016/1/15.
 */
public class ObjectConvertUtil {
	public static String convertModle(SecureModel secureModel){
		ReceiveMessage receiveMessage = new ReceiveMessage();
		receiveMessage.setData(JSON.toJSONString(secureModel));
		receiveMessage.setMsgType(Event.MESSAGE_TYPE_SECURE_MODEL);
		return JSON.toJSONString(receiveMessage);
	}

	public static String convertModle(ResponseFile response){
		ReceiveMessage receiveMessage = new ReceiveMessage();
		receiveMessage.setData(JSON.toJSONString(response));
		receiveMessage.setMsgType(Event.MESSAGE_TYPE_RESPONSE_FILE);
		return JSON.toJSONString(receiveMessage);
	}

	public static String convertModle(RequestFile request){
		ReceiveMessage receiveMessage = new ReceiveMessage();
		receiveMessage.setData(JSON.toJSONString(request));
		receiveMessage.setMsgType(Event.MESSAGE_TYPE_REQUEST_FILE);
		return JSON.toJSONString(receiveMessage);
	}

	public static Object convertModle(String receviejson){
		ReceiveMessage receive = (ReceiveMessage)JSON.parseObject(receviejson,ReceiveMessage.class);
		Object obj = null;
		switch (receive.getMsgType()){
			case Event.MESSAGE_TYPE_SECURE_MODEL:
				obj = (SecureModel)JSON.parseObject(receive.getData().toString(),SecureModel.class);
				break;
			case Event.MESSAGE_TYPE_REQUEST_FILE:
				obj = (RequestFile)JSON.parseObject(receive.getData().toString(),RequestFile.class);
				break;
			case Event.MESSAGE_TYPE_RESPONSE_FILE:
				obj = (ResponseFile)JSON.parseObject(receive.getData().toString(),ResponseFile.class);
				break;
		}
		return obj;
	}

	public static String request(Object obj){
		if(obj instanceof SecureModel){
			SecureModel secureModel = (SecureModel)obj;
			return convertModle(secureModel);
		}else if(obj instanceof  RequestFile){
			RequestFile requestFile = (RequestFile)obj;
			return convertModle(requestFile);
		}else if(obj instanceof ResponseFile){
			ResponseFile responseFile = (ResponseFile)obj;
			return convertModle(responseFile);
		}else{
			return null;
		}
	}
}
