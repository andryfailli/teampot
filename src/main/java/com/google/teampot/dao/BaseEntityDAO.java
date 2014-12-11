package com.google.teampot.dao;

import static com.google.teampot.OfyService.ofy;

import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Ref;

public class BaseEntityDAO<T> {
	 
	private final Class<T> entityClass;
	
	protected int pageSize = 20;
	
	public BaseEntityDAO(Class<T> entityClass) {
		this.entityClass = entityClass;
	}
	
	public T get(Key<T> key) {
		return (T) ofy().load().key(key).now();
	}
	
	public T get(String key) {
		Key<T> okey = Key.create(key);
		return get(okey);
	}
	
	public T get(Long id) {
		Key<T> okey = Key.create(this.entityClass,id);
		return get(okey);
	}
	
	public void remove(Key<T> key) {
		ofy().delete().key(key).now();
	}
	
	public void remove(T entity) {
		ofy().delete().entity(entity).now();
	}
	
	public void remove(String key) {
		Key<T> okey = Key.create(key);
		remove(okey);
	}
	
	public void save(T entity) {
		ofy().save().entity(entity).now();
	}
	
	public List<T> list() {
		return ofy().load().type(this.entityClass).list();
	}
	
	public List<T> list(int limit) {
		return ofy().load().type(this.entityClass).limit(limit).list();
	}
	
	public List<T> list(String parent) {
		return ofy().load().type(this.entityClass).ancestor(Ref.create(Key.create(parent))).list();
	}

}
