package com.mars.masterserver.core.domain;

import com.google.protobuf.ByteString;
import com.mars.masterserver.net.decoder.MsgProtocol;

/**
 * Created by Administrator on 2016/1/19.
 */
public class Msg {

	private MsgProtocol.MsgType msgType;
	private String msgId;
	private ByteString body;
	private MsgProtocol.Msg _msg;

	public Msg(MsgProtocol.Msg msg) {
		if (msg != null) {
			this.msgType = msg.getType();
			this.msgId = msg.getId();
			this.body = msg.getBody();
			this._msg = msg;
		}
	}

	public ByteString getBody() {
		return body;
	}

	public void setBody(ByteString body) {
		this.body = body;
	}


	public MsgProtocol.MsgType getMsgType() {
		return msgType;
	}

	public MsgProtocol.Msg get_msg() {
		return _msg;
	}
}
