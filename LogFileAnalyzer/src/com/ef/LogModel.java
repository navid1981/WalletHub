package com.ef;

import java.io.Serializable;
/**
 * Model class for mapping java object to db table if we want use any ORM in future.
 * @author Navid Vaziri
 *
 */
public class LogModel implements Serializable{
	private String date;
	private String ip;
	private String request;
	private int status;
	private String description;
	
	public LogModel(){
		
	}
	
	public LogModel(String date, String ip, String request, int status, String description) {
		super();
		this.date = date;
		this.ip = ip;
		this.request = request;
		this.status = status;
		this.description = description;
	}

	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getRequest() {
		return request;
	}
	public void setRequest(String request) {
		this.request = request;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "LogModel [date=" + date + ", ip=" + ip + ", request=" + request + ", status=" + status
				+ ", description=" + description + "]";
	}

}
