package com.sys.gateway.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.sys.gateway.common.SpringContextHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 代付异步通知消费
 */
public class DFNotifyConsumerThread extends Thread {

	private Logger logger = LoggerFactory.getLogger(DFNotifyConsumerThread.class);

	// 是否已启动
	private boolean isStarted = false;

	private int consumerSize;

	private final ExecutorService executorService;

	public DFNotifyConsumerThread(int consumerSize){
		this.consumerSize = consumerSize;
		executorService = Executors.newFixedThreadPool(consumerSize);
	}
	
	public DFNotifyConsumerThread(){
		this(1);
	}
	public void init() {
		try {
			if (!isStarted) {
				start();
				isStarted = true;
				logger.info("DFNotifyConsumerThread start success");
			}
		}
		catch (Exception e) {
			logger.error("DFNotifyConsumerThread Error", e);
		}
	}

	@Override
	public void run() {
		
		final List<DFNotifyConsumerTask> consumers = new ArrayList<>();
		for (int i = 0; i < consumerSize; i++) {
			DFNotifyConsumerTask consumer = SpringContextHolder.getBean("dfConsumerTask");
			consumers.add(consumer);
			executorService.submit(consumer);
		}

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				executorService.shutdown();
				try {
					executorService.awaitTermination(5000, TimeUnit.MILLISECONDS);
				}
				catch (InterruptedException e) {
					logger.error(e.getMessage(), e);
				}
			}
		});
	}

	/**
	*
	* @return the consumerSize
	*/
	
	public int getConsumerSize() {
		return consumerSize;
	}

	/**
	*
	* @param consumerSize the consumerSize to set
	*/
	public void setConsumerSize(int consumerSize) {
		this.consumerSize = consumerSize;
	}
}
