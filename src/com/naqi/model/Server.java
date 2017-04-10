package com.naqi.model;

/**
 * 用户
 * @author hanfeng
 *
 */
public class Server {

	private Integer id;
	private Integer serverIndex;//服务器索引
	private Integer type;		//服务器索引
	private String name;		//姓名
	private String ip;			//ip地址
	private Integer port;		//httpPort
	private Integer sPort;		//socketPort
	
	public Server() {
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getServerIndex() {
		return serverIndex;
	}

	public void setServerIndex(Integer serverIndex) {
		this.serverIndex = serverIndex;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getsPort() {
		return sPort;
	}

	public void setsPort(Integer sPort) {
		this.sPort = sPort;
	}
	
}
