package com.github.bannirui.mms.client.metrics;

import com.codahale.metrics.Meter;
import com.codahale.metrics.Metered;
import com.codahale.metrics.Snapshot;
import com.codahale.metrics.Timer;
import com.github.bannirui.mms.stats.ClientInfo;
import com.github.bannirui.mms.stats.DistributionInfo;
import com.github.bannirui.mms.stats.MeterInfo;
import com.github.bannirui.mms.stats.StatsInfo;
import com.github.bannirui.mms.stats.TimerInfo;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.springside.modules.utils.net.NetUtil;

public abstract class MmsMetrics {
	private String clientName;
	private String mmsName;

	private ClientInfo clientInfo;

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getMmsName() {
		return mmsName;
	}

	public void setMmsName(String mmsName) {
		this.mmsName = mmsName;
	}

	public ClientInfo getClientInfo() {
		return clientInfo;
	}

	public void setClientInfo(ClientInfo clientInfo) {
		this.clientInfo = clientInfo;
	}

	MmsMetrics(String clientName, String mmsName) {
		this.clientName = clientName;
		this.mmsName = mmsName;
		clientInfo = buildClientInfo(clientName, mmsName);

	}

	public MeterInfo transfer(Meter meter, String type) {
		MeterInfo info = new MeterInfo();
		info.setCount(meter.getCount());
		info.setMean(meter.getMeanRate());
		info.setMin1Rate(meter.getOneMinuteRate());
		info.setMin5Rate(meter.getFiveMinuteRate());
		info.setMin15Rate(meter.getFifteenMinuteRate());
		info.setType(type);
		return info;
	}

	public TimerInfo transfer(Timer timer, String type) {
		TimerInfo info = new TimerInfo();
		Snapshot snapshot = timer.getSnapshot();
		info.setMean(transferToMilliseconds(snapshot.getMean()));
		info.setMin(transferToMilliseconds(snapshot.getMin()));
		info.setMax(transferToMilliseconds(snapshot.getMax()));
		info.setMedian(transferToMilliseconds(snapshot.getMedian()));
		info.setStddev(transferToMilliseconds(snapshot.getStdDev()));
		info.setPercent75(transferToMilliseconds(snapshot.get75thPercentile()));
		info.setPercent90(transferToMilliseconds(snapshot.getValue(0.9)));
		info.setPercent95(transferToMilliseconds(snapshot.get95thPercentile()));
		info.setPercent98(transferToMilliseconds(snapshot.get98thPercentile()));
		info.setPercent99(transferToMilliseconds(snapshot.get99thPercentile()));
		info.setPercent999(transferToMilliseconds(snapshot.get999thPercentile()));
		info.setType(type);
		return info;
	}

	public DistributionInfo transfer(Distribution distribution, String type) {
		DistributionInfo info = new DistributionInfo();
		info.setLessThan1Ms(distribution.getLessThan1Ms().longValue());
		info.setLessThan5Ms(distribution.getLessThan5Ms().longValue());
		info.setLessThan10Ms(distribution.getLessThan10Ms().longValue());
		info.setLessThan50Ms(distribution.getLessThan50Ms().longValue());
		info.setLessThan100Ms(distribution.getLessThan100Ms().longValue());
		info.setLessThan500Ms(distribution.getLessThan500Ms().longValue());
		info.setLessThan1000Ms(distribution.getLessThan1000Ms().longValue());
		info.setMoreThan1000Ms(distribution.getMoreThan1000Ms().longValue());
		info.setType(type);
		return info;
	}

	public abstract StatsInfo reportMessageStatistics();

	public abstract String reportLogStatistics();

	void processMeter(Metered meter, StringBuilder context) throws Exception {
		context.append(String.format("             count = %d\n", meter.getCount()));
		context.append(String.format("         mean rate = %.2f \n", meter.getMeanRate()));
		context.append(String.format("     1-minute rate = %.2f \n", meter.getOneMinuteRate()));
		context.append(String.format("     5-minute rate = %.2f \n", meter.getFiveMinuteRate()));
		context.append(String.format("    15-minute rate = %.2f \n", meter.getFifteenMinuteRate()));
	}

	void processTimer(Timer timer, StringBuilder context) throws Exception {
		processMeter(timer, context);
		final Snapshot snapshot = timer.getSnapshot();
		context.append(String.format("               min = %d\n", snapshot.getMin()));
		context.append(String.format("               max = %d\n", snapshot.getMax()));
		context.append(String.format("              mean = %.2f\n", snapshot.getMean()));
		context.append(String.format("            stddev = %.2f\n", snapshot.getStdDev()));
		context.append(String.format("            median = %.2f\n", snapshot.getMedian()));
		context.append(String.format("              75%% <= %.2f\n", snapshot.get75thPercentile()));
		context.append(String.format("              90%% <= %.2f\n", snapshot.getValue(0.90)));
		context.append(String.format("              95%% <= %.2f\n", snapshot.get95thPercentile()));
		context.append(String.format("              98%% <= %.2f\n", snapshot.get98thPercentile()));
		context.append(String.format("              99%% <= %.2f\n", snapshot.get99thPercentile()));
		context.append(String.format("              999%% <= %.2f\n", snapshot.get999thPercentile()));
	}

	private ClientInfo buildClientInfo(String clientName, String mmsName) {
		ClientInfo clientInfo = new ClientInfo();
		clientInfo.setClientName(clientName);
		clientInfo.setMmsName(mmsName);
		clientInfo.setIp(NetUtil.getLocalHost());
		return clientInfo;
	}

	private double transferToMilliseconds(double costTime) {
		BigDecimal data = new BigDecimal(costTime);
		BigDecimal bigDecimal = data.divide(new BigDecimal(1000000), RoundingMode.HALF_DOWN);
		return bigDecimal.doubleValue();
	}
}
