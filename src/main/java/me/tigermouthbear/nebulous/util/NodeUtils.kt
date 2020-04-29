package me.tigermouthbear.nebulous.util

import com.sun.org.apache.bcel.internal.util.ClassPath
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.IntInsnNode
import org.objectweb.asm.tree.LdcInsnNode


interface NodeUtils {
	fun getLdcInt(int: Int): AbstractInsnNode {
		if(int <= 32767 && int >= -32768) {
			return IntInsnNode(Opcodes.SIPUSH, int)
		} else if(int <= 127 && int >= -128) {
			return IntInsnNode(Opcodes.BIPUSH, int)
		}

		return when(int) {
			-1 -> InsnNode(Opcodes.ICONST_M1)
			0 -> InsnNode(Opcodes.ICONST_0)
			1 -> InsnNode(Opcodes.ICONST_1)
			2 -> InsnNode(Opcodes.ICONST_2)
			3 -> InsnNode(Opcodes.ICONST_3)
			4 -> InsnNode(Opcodes.ICONST_4)
			5 -> InsnNode(Opcodes.ICONST_5)
			else -> LdcInsnNode(int)
		}
	}
}