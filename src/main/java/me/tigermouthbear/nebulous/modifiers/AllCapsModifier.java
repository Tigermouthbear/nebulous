package me.tigermouthbear.nebulous.modifiers;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class AllCapsModifier extends Modifier
{
	public void modify()
	{
		for(ClassNode cn: getClassMap().values())
		{
			for(MethodNode mn: cn.methods)
			{
				// Iterate instructions in method
				for(AbstractInsnNode ain: mn.instructions.toArray())
				{
					// If the instruction is loading a constant value
					if(ain.getOpcode() == Opcodes.LDC)
					{
						// Cast current instruction to Ldc
						// If the constant is a string then capitalize it.
						LdcInsnNode ldc = (LdcInsnNode) ain;
						if(ldc.cst instanceof String)
						{
							ldc.cst = ldc.cst.toString().toUpperCase();
						}
					}
				}
			}
		}
	}
}
