package dev.tigr.nebulous.config;

import org.json.JSONArray;

/**
 * @author Tigermouthbear
 */
public class ArrayConfig extends Config<JSONArray> {
    public ArrayConfig(String name) {
        super(name, Type.ARRAY);
    }
}
