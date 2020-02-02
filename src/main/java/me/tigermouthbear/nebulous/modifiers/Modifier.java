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

	protected Map<String, ClassNode> getClassMap()
	{
		return target.getClassNodes();
	}

	protected void applyRemap(Map<String, String> remap)
	{
		SimpleRemapper remapper = new SimpleRemapper(remap);
		for(ClassNode node: new ArrayList<>(getClassMap().values()))
		{
			ClassNode copy = new ClassNode();
			ClassRemapper adapter = new ClassRemapper(copy, remapper);
			node.accept(adapter);
			getClassMap().put(node.name, copy);
		}
	}
}
