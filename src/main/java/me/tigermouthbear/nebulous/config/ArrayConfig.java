package me.tigermouthbear.nebulous.config;

import org.json.JSONArray;

public class ArrayConfig extends Config<JSONArray> {
	public ArrayConfig(String name) {
		super(name, Type.ARRAY);
	}
}
