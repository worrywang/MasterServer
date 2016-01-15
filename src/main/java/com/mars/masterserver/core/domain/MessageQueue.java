package com.mars.masterserver.core.domain;


import java.util.Queue;

/**
 * Created by Administrator on 2015/12/7.
 */
public class MessageQueue {
	private Queue<GameRequest> requestQueue;
	private boolean running = false;

	public MessageQueue(Queue<GameRequest> requestQueue){
		this.requestQueue = requestQueue;
	}

	public Queue<GameRequest> getRequestQueue() {
		return requestQueue;
	}

	public void printAll(){
		for(GameRequest gameRequest:requestQueue){
			System.out.println("[" + gameRequest.getHead().toString() + "]: " + gameRequest.getBody().toString());
		}
		System.out.println("MessageQueue.printAll()--------------------------------------");
	}

	/**
	 * 清除消息队列
	 */
	public void clear(){
		requestQueue.clear();
		requestQueue = null;
	}

	/**
	 * 获取消息队列的长度
	 * @return
	 */
	public int size(){
		return requestQueue!=null?requestQueue.size():0;
	}

	/**
	 * 向消息队列中添加请求消息
	 * @param request
	 * @return
	 */
	public boolean add(GameRequest request){
		if(this.requestQueue==null) return false;
		return this.requestQueue.add(request);
	}

	/**
	 * 设置消息队列运行状态
	 * @param running
	 */
	public void setRunning(boolean running){
		this.running = running;
	}

	/**
	 * 消息队列是否正在被轮序
	 * @return
	 */
	public  boolean isRunning(){return running;}
}
