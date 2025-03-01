package com.github.bannirui.mms.client.metrics;

import com.github.bannirui.mms.client.consumer.Consumer;
import com.github.bannirui.mms.client.consumer.ConsumerFactory;
import com.github.bannirui.mms.client.producer.Producer;
import com.github.bannirui.mms.client.producer.ProducerFactory;
import com.github.bannirui.mms.logger.MmsLogger;
import com.github.bannirui.mms.util.ExecutorServiceUtils;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class MmsStatsReporter implements Runnable {
	private final ScheduledExecutorService executor;

	private volatile boolean running = false;

	public MmsStatsReporter() {
		this.executor = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
			AtomicLong threadIndex = new AtomicLong(0);
			@Override
			public Thread newThread(Runnable r) {
				Thread t = new Thread(r, "MmsConnectionManager-JmxReporter-" + threadIndex.incrementAndGet());
				t.setUncaughtExceptionHandler((t1, e) -> MmsLogger.log.error("uncaughtException in thread: {}", t1.getName(), e));
				return t;
			}
		});
	}

	public void start(long period, TimeUnit unit) {
		running = true;
		executor.scheduleWithFixedDelay(this, period, period, unit);
	}

	public void shutdown() {
		running = false;
		ExecutorServiceUtils.gracefullyShutdown(executor);
	}

	@Override
	public void run() {
		if (running) {
			for (Producer producer : ProducerFactory.getProducers()) {
				producer.statistics();
			}
			for (Consumer consumer : ConsumerFactory.getConsumers()) {
				consumer.statistics();
			}
		}
	}
}

