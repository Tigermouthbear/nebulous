package me.tigermouthbear.nebulous.modifiers;

import me.tigermouthbear.nebulous.util.RandomString;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.*;
import java.util.stream.Collectors;

public class MethodNameModifier extends Modifier
{
	public void modify()
	{
		Map<String, String> remap = new HashMap<>();

		/*getClassMap().values().stream().filter(classNode ->
				!isDependency(classNode.name)).forEach(classNode ->
				classNode.methods.stream().filter(methodNode -> !methodNode.name.startsWith("<") && (methodNode.access & Opcodes.ACC_NATIVE) == 0 && !methodNode.name.equals("main")).forEach(methodNode ->
						remap.put(classNode.name + "." + methodNode.name + methodNode.desc, RandomString.genRandomString())));*/

		Map<ClassNode, String> superclasses = new HashMap<>();
		getClasses().filter(classNode -> !getClassMap().containsKey(classNode.superName) && (classNode.access & Opcodes.ACC_INTERFACE) == 0).collect(Collectors.toList()).forEach(classNode -> superclasses.put(classNode, classNode.name));

		Map<ClassNode, List<String>> annotations = new HashMap<>();
		getClasses().filter(classNode -> classNode.visibleAnnotations != null).forEach(classNode -> annotations.put(classNode, getAnnotations(classNode)));

		for(ClassNode classNode: superclasses.keySet())
		{
			List<MethodNode> methods = classNode.methods.stream().filter(methodNode -> !methodNode.name.startsWith("<") && (methodNode.access & Opcodes.ACC_NATIVE) == 0 && !methodNode.name.equals("main")).collect(Collectors.toList());

			if(classNode.visibleAnnotations!= null) classNode.visibleAnnotations.forEach(an -> System.out.println(an.desc));

			for(MethodNode methodNode: methods)
			{
				String name = RandomString.genRandomString();

				Stack<ClassNode> stack = new Stack<>();
				stack.add(classNode);
				while(!stack.isEmpty())
				{
					ClassNode node = stack.pop();
					//System.out.println(node.name + "-----" + methodNode.name + "-----" + name);
					remap.put(node.name + "." + methodNode.name + methodNode.desc, name);
					if(annotations.containsKey(node)) annotations.get(node).forEach(n -> remap.put(node.name + "." + n, n));


					stack.addAll(getClasses().filter(sub -> sub.superName.equals(node.name)).collect(Collectors.toList()));
					stack.addAll(getImplementations(node));
				}
			}
		}

		applyRemap(remap);
	}

	private List<String> getAnnotations(ClassNode classNode)
	{
		List<String> s = new ArrayList<>();
		classNode.visibleAnnotations.forEach(a -> s.add(a.desc + a.values));
		return s;
	}
}
