package me.tigermouthbear.nebulous.modifiers;

import me.tigermouthbear.nebulous.Nebulous;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.SimpleRemapper;
import org.objectweb.asm.tree.ClassNode;

import java.util.ArrayList;
import java.util.Map;

public abstract class Modifier
{
	private Nebulous target;

	public Modifier setTarget(Nebulous target)
	{
		this.target = target;
		return this;
	}

	public abstract void modify();

	protected void applyRemap(Map<String, String> remap)
	{
		SimpleRemapper remapper = new SimpleRemapper(remap);
		for(ClassNode node: new ArrayList<>(getClassMap().values()))
		{
			ClassNode copy = new ClassNode();
			ClassRemapper adapter = new ClassRemapper(copy, remapper);
			node.accept(adapter);

			getClassMap().remove(node.name);
			getClassMap().put(node.name, copy);
		}
	}

	protected boolean isDependency(String name)
	{
		String path = getPath(name);

		for(String depencency: target.getDependencies())
		{
			if(path.contains(depencency)) return true;
		}

		return false;
	}

	protected String getPath(String name)
	{
		if(!name.contains("/")) return "";

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

	protected Map<String, ClassNode> getClassMap()
	{
		return target.getClassNodes();
	}
}
