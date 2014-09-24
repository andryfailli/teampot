package com.google.teampot.transformer;

import java.util.ArrayList;
import java.util.List;

import com.google.api.server.spi.config.Transformer;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;

public class Ref2StringTransformer<T> implements Transformer<Ref<T>, String> {
	
	public String transformTo(Ref<T> in) {
		if (in != null) {
			return in.getKey().getString();
		} else {
			return null;
		}
	}

	public Ref<T> transformFrom(String in) {
		if (in != null) {
			Key<T> key = Key.create(in);
			return Ref.create(key);
		} else {
			return null;
		}
	}
	
	public List<String> transformTo(List<Ref<T>> ins) {
		List<String> outs = new ArrayList<String>();
		for (Ref<T> in : ins) {
			outs.add(this.transformTo(in));
		}
		return outs;
	}

	public List<Ref<T>> transformFrom(List<String> ins) {
		List<Ref<T>> outs = new ArrayList<Ref<T>>();
		for (String in : ins) {
			outs.add(this.transformFrom(in));
		}
		return outs;
	}

}
