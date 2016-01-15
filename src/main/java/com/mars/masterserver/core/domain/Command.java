package com.mars.masterserver.core.domain;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * 控制命令体
 * Created by Administrator on 2015/12/7.
 */
public class Command {
	private int id;
	private InputStream CommandData;

	private static int count =0;
	public Command(String inCommandData){
		this.CommandData = new ByteArrayInputStream(inCommandData.getBytes());
		this.id = count++;
	}

	public Command(InputStream inCommandData){
		this.id=count++;
	}

}
