package dev.tigr.nebulous.config;

import java.util.ArrayList;
import java.util.List;

public class Config<T> {
	private static List<Config> configs = new ArrayList<>();

	private String name;
	private Type type;
	private T value;

	public Config(String name, Type type) {
		this.name = name;
		this.type = type;

		configs.add(this);
	}

	enum Type {
		ARRAY,
		STRING
	}

	public String getName() {
		return name;
	}

	public Type getType() {
		return type;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public static List<Config> getAll() {
		return configs;
	}
}
