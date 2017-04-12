package com.naqi.mj.model;
/**
 * 杠枚举
 * @author feng
 */
public enum Gang {
	
	AN_GANG (1, "angang"),
	DIAN_GANG (2, "diangang"),
	WAN_GANG (3, "wangang");
	
	private int id;
	private String name;
	
	private Gang(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static Gang parse(short id) {
		Gang temp = null;
		for (Gang item : Gang.values()) {
			if (item.getId() == id) {
				temp = item;
				break;
			}
		}
		return temp;
	}
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}