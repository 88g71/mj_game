package com.naqi.test;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSON;

public class Test {

	public static void main(String[] args) {
		Object o = new HashMap<String,String>();
		System.out.println(o instanceof Map);
		System.out.println(o.getClass().getName());
		new Test().m1((Map)o);
	}
	private Test1 t1;
	
	public Test() {
		this.t1 = new Test1(this);
	}
	
	public void m1(Object o){
		System.out.println("m1");
	}
	public void m1(Map<String, String> map ){
		System.out.println("m2");
	}
	public void m1(HashMap<String, String> map){
		
		System.out.println("m3");
	}
}
class Test1{
	public Test1(Test t) {
		System.out.println(t);
	}
}
