package org.xplan.manager.web.shared;

import java.io.Serializable;

public class Plan implements Serializable, Comparable<Plan>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 573099017461370301L;
	String name;
	String id;
	String type;
	Boolean loaded=false;
	Boolean validated=false;

	public Plan(){
		this.name = "N/A";
		this.id = "-";
		this.type = "NO TYPE";
	}	
	
	public Plan(String name, String id, String type){
		this.name = name;
		this.id = id;
		this.type = type;
	}
	
	public String getName() {
		return name;
	}
	
	public String getId() {
		return id;
	}
	
	public String getType() {
		return type;
	}
	
	public Boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(Boolean loaded) {
		this.loaded = loaded;
	}

	public Boolean isValidated() {
		return validated;
	}

	public void setValidated(Boolean validated) {
		this.validated = validated;
	}

	@Override
	public int compareTo(Plan o) {
		return (o == null || o.name == null) ? -1 : -o.name.compareTo(name);
	}

}