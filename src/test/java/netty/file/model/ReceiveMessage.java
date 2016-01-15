package netty.file.model;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/1/15.
 */
public class ReceiveMessage implements Serializable {

	private static final long serialVersionUID = 6200390330718630934L;

	private short msgType;
	private String data;

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public short getMsgType() {
		return msgType;
	}

	public void setMsgType(short msgType) {
		this.msgType = msgType;
	}
}
