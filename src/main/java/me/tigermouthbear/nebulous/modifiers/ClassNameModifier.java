package me.tigermouthbear.nebulous.modifiers;

import me.tigermouthbear.nebulous.util.RandomString;

import java.util.HashMap;
import java.util.Map;

public class ClassNameModifier extends Modifier
{
	public void modify()
	{
		Map<String, String> remap = new HashMap<>();

		getClasses().forEach(classNode ->
				remap.put(classNode.name, "AresClientOnTop" + RandomString.genRandomString()));

		applyRemap(remap);
	}
}
