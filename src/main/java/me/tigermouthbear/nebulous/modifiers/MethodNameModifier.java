package me.tigermouthbear.nebulous.modifiers;

import me.tigermouthbear.nebulous.util.RandomString;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;

public class MethodNameModifier extends Modifier
{
	public void modify()
	{
		Map<String, String> remap = new HashMap<>();

		getClassMap().values().stream().filter(classNode ->
				!isDependency(classNode.name)).forEach(classNode ->
				classNode.methods.stream().filter(methodNode -> !methodNode.name.startsWith("<") && (methodNode.access & Opcodes.ACC_NATIVE) == 0 && !methodNode.name.equals("main")).forEach(methodNode ->
						remap.put(classNode.name + "." + methodNode.name + methodNode.desc, RandomString.genRandomString())));

		applyRemap(remap);
	}
}
