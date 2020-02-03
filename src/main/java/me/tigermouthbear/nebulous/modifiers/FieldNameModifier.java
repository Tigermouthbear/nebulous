package me.tigermouthbear.nebulous.modifiers;

import me.tigermouthbear.nebulous.util.RandomString;

import java.util.HashMap;
import java.util.Map;

public class FieldNameModifier extends Modifier
{
	public void modify()
	{
		Map<String, String> remap = new HashMap<>();

		getClassMap().values().stream().filter(classNode ->
				!isDependency(classNode.name)).forEach(classNode ->
				classNode.fields.forEach(fieldNode ->
						remap.put(classNode.name + "." + fieldNode.name, RandomString.genRandomString())));

		applyRemap(remap);
	}
}
