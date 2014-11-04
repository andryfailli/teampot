package com.google.teampot.transformer;

import java.util.ArrayList;
import java.util.List;

import com.google.api.server.spi.config.Transformer;
import com.googlecode.objectify.Ref;

public class Ref2EntityTransformer<T> implements Transformer<Ref<T>, T> {
	
	public T transformTo(Ref<T> in) {
		if (in != null) {
			return in.get();
		} else {
			return null;
		}
	}

	public Ref<T> transformFrom(T in) {
		if (in != null) {
			return Ref.create(in);
		} else {
			return null;
		}
	}
	
	public List<T> transformTo(Iterable<Ref<T>> ins) {
		List<T> outs = new ArrayList<T>();
		for (Ref<T> in : ins) {
			outs.add(this.transformTo(in));
		}
		return outs;
	}

	public List<Ref<T>> transformFrom(Iterable<T> ins) {
		List<Ref<T>> outs = new ArrayList<Ref<T>>();
		for (T in : ins) {
			outs.add(this.transformFrom(in));
		}
		return outs;
	}

}
