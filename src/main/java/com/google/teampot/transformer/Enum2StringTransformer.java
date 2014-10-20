package com.google.teampot.transformer;

import java.util.ArrayList;
import java.util.List;

import com.google.api.server.spi.config.Transformer;

public class Enum2StringTransformer<T extends Enum<T>> implements Transformer<T, String> {
	
	private Class<T> enumClass;
	
	public Enum2StringTransformer(Class<T> enumClass) {
		this.enumClass = enumClass;
	}
	
	public String transformTo(T in) {
		if (in != null) {
			return in.name();
		} else {
			return null;
		}
	}

	
	public T transformFrom(String in) {
		if (in != null) {
			return (T) Enum.valueOf(this.enumClass, in);
		} else {
			return null;
		}
	}
	
	public List<String> transformTo(List<T> ins) {
		List<String> outs = new ArrayList<String>();
		for (T in : ins) {
			outs.add(this.transformTo(in));
		}
		return outs;
	}

	public List<T> transformFrom(List<String> ins) {
		List<T> outs = new ArrayList<T>();
		for (String in : ins) {
			outs.add(this.transformFrom(in));
		}
		return outs;
	}

}
