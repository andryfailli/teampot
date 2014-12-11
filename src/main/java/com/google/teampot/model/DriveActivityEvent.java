package com.google.teampot.model;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.google.teampot.transformer.Enum2StringTransformer;
import com.googlecode.objectify.annotation.Subclass;

@Subclass(index=true)
public class DriveActivityEvent extends ActivityEvent {

	private String file;
	
	private DriveActivityEventVerb verb;
	
	public DriveActivityEvent() {
		// TODO Auto-generated constructor stub
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}
	
	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public DriveActivityEventVerb getVerb() {
		return verb;
	}

	@ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
	public void setVerb(DriveActivityEventVerb verb) {
		this.verb = verb;
	}
	
	@ApiResourceProperty(name = "verb")
	public String getVerbString() {
		Enum2StringTransformer<DriveActivityEventVerb> t = new Enum2StringTransformer<DriveActivityEventVerb>(DriveActivityEventVerb.class);
		return t.transformTo(this.verb);
	}	

	@ApiResourceProperty(name = "verb")
	public void setVerbString(String verb) {
		Enum2StringTransformer<DriveActivityEventVerb> t = new Enum2StringTransformer<DriveActivityEventVerb>(DriveActivityEventVerb.class);
		this.verb = t.transformFrom(verb);
	}

}
