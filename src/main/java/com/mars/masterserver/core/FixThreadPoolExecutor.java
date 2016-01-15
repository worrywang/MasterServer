package com.mars.masterserver.core;

import java.util.concurrent.*;

/**
 * Created by Administrator on 2015/12/5.
 */
public class FixThreadPoolExecutor extends ThreadPoolExecutor{
	public FixThreadPoolExecutor(int corePoolSize,int maximumPoolSize,
	                             long keepAliveSecond,String poolName){
		super(corePoolSize,maximumPoolSize,keepAliveSecond, TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory());
	}

	public FixThreadPoolExecutor(int corePoolSize,int maximumPoolSize,
	                             long keepAliveSecond){
		super(corePoolSize,maximumPoolSize,keepAliveSecond,TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>());

	}
}
