package com.github.bannirui.mms.client.metrics;

import java.util.concurrent.atomic.LongAdder;

public class Distribution {
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	private LongAdder lessThan1Ms = new LongAdder();
	private LongAdder lessThan5Ms = new LongAdder();
	private LongAdder lessThan10Ms = new LongAdder();
	private LongAdder lessThan50Ms = new LongAdder();
	private LongAdder lessThan100Ms = new LongAdder();
	private LongAdder lessThan500Ms = new LongAdder();
	private LongAdder lessThan1000Ms = new LongAdder();
	private LongAdder moreThan1000Ms = new LongAdder();


	static Distribution newDistribution(String name) {
		Distribution distribution = new Distribution();
		distribution.setName(name);
		return distribution;

	}


	public void markTime(long costInMs) {
		if (costInMs < 1) {
			lessThan1Ms.increment();
		} else if (costInMs < 5) {
			lessThan5Ms.increment();
		} else if (costInMs < 10) {
			lessThan10Ms.increment();
		} else if (costInMs < 50) {
			lessThan50Ms.increment();
		} else if (costInMs < 100) {
			lessThan100Ms.increment();
		} else if (costInMs < 500) {
			lessThan500Ms.increment();
		} else if (costInMs < 1000) {
			lessThan1000Ms.increment();
		} else {
			moreThan1000Ms.increment();
		}
	}

	public void markSize(long costInMs) {
		if (costInMs < 1024) {
			lessThan1Ms.increment();
		} else if (costInMs < 5 * 1024) {
			lessThan5Ms.increment();
		} else if (costInMs < 10 * 1024) {
			lessThan10Ms.increment();
		} else if (costInMs < 50 * 1024) {
			lessThan50Ms.increment();
		} else if (costInMs < 100 * 1024) {
			lessThan100Ms.increment();
		} else if (costInMs < 500 * 1024) {
			lessThan500Ms.increment();
		} else if (costInMs < 1024 * 1024) {
			lessThan1000Ms.increment();
		} else {
			moreThan1000Ms.increment();
		}
	}


	public String output() {


		return System.currentTimeMillis() +
				"\n 1  - " + lessThan1Ms +
				"\n 5 - " + lessThan5Ms +
				"\n 10 - " + lessThan10Ms +
				"\n 50 - " + lessThan50Ms +
				"\n 100 - " + lessThan100Ms +
				"\n 500 - " + lessThan500Ms +
				"\n 1000 - " + lessThan1000Ms +
				"\n 1000more - " + moreThan1000Ms;

	}

	public LongAdder getLessThan1Ms() {
		return lessThan1Ms;
	}

	public void setLessThan1Ms(LongAdder lessThan1Ms) {
		this.lessThan1Ms = lessThan1Ms;
	}

	public LongAdder getLessThan5Ms() {
		return lessThan5Ms;
	}

	public void setLessThan5Ms(LongAdder lessThan5Ms) {
		this.lessThan5Ms = lessThan5Ms;
	}

	public LongAdder getLessThan10Ms() {
		return lessThan10Ms;
	}

	public void setLessThan10Ms(LongAdder lessThan10Ms) {
		this.lessThan10Ms = lessThan10Ms;
	}

	public LongAdder getLessThan50Ms() {
		return lessThan50Ms;
	}

	public void setLessThan50Ms(LongAdder lessThan50Ms) {
		this.lessThan50Ms = lessThan50Ms;
	}

	public LongAdder getLessThan100Ms() {
		return lessThan100Ms;
	}

	public void setLessThan100Ms(LongAdder lessThan100Ms) {
		this.lessThan100Ms = lessThan100Ms;
	}

	public LongAdder getLessThan500Ms() {
		return lessThan500Ms;
	}

	public void setLessThan500Ms(LongAdder lessThan500Ms) {
		this.lessThan500Ms = lessThan500Ms;
	}

	public LongAdder getLessThan1000Ms() {
		return lessThan1000Ms;
	}

	public void setLessThan1000Ms(LongAdder lessThan1000Ms) {
		this.lessThan1000Ms = lessThan1000Ms;
	}

	public LongAdder getMoreThan1000Ms() {
		return moreThan1000Ms;
	}

	public void setMoreThan1000Ms(LongAdder moreThan1000Ms) {
		this.moreThan1000Ms = moreThan1000Ms;
	}
}




