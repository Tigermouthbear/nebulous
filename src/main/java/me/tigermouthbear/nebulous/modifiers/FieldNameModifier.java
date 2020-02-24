package me.tigermouthbear.nebulous.modifiers;

import me.tigermouthbear.nebulous.util.RandomString;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;

import java.util.*;
import java.util.stream.Collectors;

public class FieldNameModifier extends Modifier
{
	public void modify()
	{
		Map<String, String> remap = new HashMap<>();

		//add a list of field nodes with their corresponding class nodes
		Map<FieldNode, ClassNode> fieldMap = new HashMap<>();
		getClasses().forEach(classNode -> classNode.fields.forEach(fieldNode -> fieldMap.put(fieldNode, classNode)));

		for(FieldNode fieldNode: fieldMap.keySet())
		{
			ClassNode fieldOwner = fieldMap.get(fieldNode);
			String name = RandomString.genRandomString();

			Stack<ClassNode> stack = new Stack<>();
			stack.add(fieldOwner);

			while(!stack.empty())
			{
				//remap field name in stack
				ClassNode classNode = stack.pop();
				remap.put(classNode.name + "." + fieldNode.name, name);

				//add classes which implement or extend the class node to the stack so that their fields get remapped
				stack.addAll(getClasses().filter(supers -> supers.superName.equals(classNode.name)).collect(Collectors.toList()));
				stack.addAll(getImplementations(classNode));
			}
		}

		applyRemap(remap);
	}
}
