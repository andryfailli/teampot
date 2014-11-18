package com.google.stringifier;

import com.google.teampot.model.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.stringifier.Stringifier;

public class RefUserStringifier implements Stringifier<Ref<User>> {

	public RefUserStringifier() {
		
	}

	public String toString(Ref<User> obj) {
		if (obj != null)
			return obj.getKey().getString();
		else
			return null;
	}

	public Ref<User> fromString(String str) {
		if (str != null) {
			Key<User> key = Key.create(str);
			return Ref.create(key);
		} else
			return null;
	}

}
