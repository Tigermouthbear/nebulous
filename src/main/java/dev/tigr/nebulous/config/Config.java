package dev.tigr.nebulous.config;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tigermouthbear
 */
public class Config<T> {
    private static final List<Config> configs = new ArrayList<>();

    private final String name;
    private final Type type;
    private T value;

    public Config(String name, Type type) {
        this.name = name;
        this.type = type;

        configs.add(this);
    }

    public static List<Config> getAll() {
        return configs;
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

    enum Type {
        ARRAY,
        STRING
    }
}
