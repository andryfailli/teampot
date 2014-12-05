package com.google.teampot.transformer;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.api.server.spi.config.Transformer;

public class Date2TimestampTransformer implements Transformer<Date, Long> {
	
	public Long transformTo(Date in) {
		if (in != null) {
			return in.getTime();
		} else {
			return null;
		}
	}

	public Date transformFrom(Long in) {
		if (in != null) {
			return new Date(in);
		} else {
			return null;
		}
	}
	
	public List<Long> transformTo(List<Date> ins) {
		List<Long> outs = new ArrayList<Long>();
		for (Date in : ins) {
			outs.add(this.transformTo(in));
		}
		return outs;
	}

	public List<Date> transformFrom(List<Long> ins) {
		List<Date> outs = new ArrayList<Date>();
		for (Long in : ins) {
			outs.add(this.transformFrom(in));
		}
		return outs;
	}

}
