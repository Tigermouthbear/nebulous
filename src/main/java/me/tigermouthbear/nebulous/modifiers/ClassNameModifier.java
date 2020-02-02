package me.tigermouthbear.nebulous.modifiers;

import me.tigermouthbear.nebulous.util.RandomString;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;
import me.tigermouthbear.nebulous.Nebulous;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ClassNameModifier extends Modifier
{
	public void modify()
	{
		Map<String, String> remap = new HashMap<>();

		for(ClassNode cn: getClassMap().values())
		{
			if(!Arrays.asList(cn.name.split("/")).contains("Main")) remap.put(cn.name, getPath(cn.name) + RandomString.genRandomString());
		}

		applyRemap(remap);
	}

	public String getPath(String name)
	{
		String reversedString = reverseString(name);
		String path = reversedString.substring(reversedString.indexOf("/"));
		return reverseString(path);
	}

	private String reverseString(String string)
	{
		StringBuilder sb = new StringBuilder();
		char[] chars = string.toCharArray();

		for(int i = chars.length - 1; i >= 0; i--)
			sb.append(chars[i]);

		return sb.toString();
	}
}
