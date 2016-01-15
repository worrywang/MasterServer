package com.mars.masterserver.core.domain;

import com.mars.masterserver.net.decoder.MsgProtocol;

import java.util.List;

/**
 * Created by Administrator on 2016/1/18.
 */
public class Head {
	private String srcID;
	private MsgProtocol.SRCType srcType;
	private List<String> dstIDs;

	public Head(){
		dstIDs =  null;
	}

	public Head(String srcID,MsgProtocol.SRCType srcType){
		this.srcID = srcID;
		this.srcType = srcType;
		dstIDs = null;
	}

	public Head(MsgProtocol.Head head){
		if(head!=null){
			this.srcID = head.getSrcID();
			this.srcType = head.getSrcType();
			this.dstIDs = head.getDstIDsList();
		}
	}

	public List<String> getDstIDs() {
		return dstIDs;
	}

	public void setDstIDs(List<String> dstIDs) {
		this.dstIDs = dstIDs;
	}

	public String getSrcID() {
		return srcID;
	}

	public void setSrcID(String srcID) {
		this.srcID = srcID;
	}

	public MsgProtocol.SRCType getSrcType() {
		return srcType;
	}

	public void setSrcType(MsgProtocol.SRCType srcType) {
		this.srcType = srcType;
	}

	@Override
	public String toString() {
//		return super.toString();
		StringBuilder sb = new StringBuilder();
		sb.append("srcID: ");
		sb.append(srcID);
		sb.append(" ; srcType: ");
		sb.append(srcType);
		sb.append(" ; dstIDs: ");
		sb.append(dstIDs);
		return sb.toString();
	}


}


