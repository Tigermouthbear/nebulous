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
import java.util.stream.Collectors;

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
