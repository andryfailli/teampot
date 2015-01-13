package com.google.teampot.service.metric;

public abstract class MetricCalculator {
	
	public String getName() {
		return this.getClass().getSimpleName();
	};
	
	public abstract float computeValue();

}
