package com.mars.masterserver.test;

import java.util.TimerTask;

/**
 * Created by Administrator on 2016/1/5.
 */
public class ReconnectTimer extends TimerTask {

	@Override
	public void run() {
		System.out.println("正在尝试与服务器连接。。。。。。。");

	}
}
