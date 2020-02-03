package me.tigermouthbear.nebulous.modifiers;

import me.tigermouthbear.nebulous.util.RandomString;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class ClassNameModifier extends Modifier
{
	public void modify()
	{
		Map<String, String> remap = new HashMap<>();

		getClassMap().values().stream().filter(classNode ->
				!isDependency(classNode.name) &&
						!Arrays.asList(classNode.name.split("/")).contains("Main")).forEach(classNode ->
				remap.put(classNode.name, getPath(classNode.name) + RandomString.genRandomString()));

		applyRemap(remap);
	}
}
