package com.onirica.carrousel;

public class Team {
	private String id;
	private String crestURL;
	
	public Team(String id, String crestURL) {
		super();
		this.id = id;
		this.crestURL = crestURL;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCrestURL() {
		return crestURL;
	}
	public void setCrestURL(String crestURL) {
		this.crestURL = crestURL;
	}
	
	public String toString(){
		return id;
	}
}
