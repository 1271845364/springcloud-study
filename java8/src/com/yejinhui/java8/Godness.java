package com.yejinhui.java8;

public class Godness {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Godness(String name) {
		super();
		this.name = name;
	}

	@Override
	public String toString() {
		return "Godness [name=" + name + "]";
	}

}