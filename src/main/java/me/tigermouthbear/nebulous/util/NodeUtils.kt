package me.tigermouthbear.nebulous.util

import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.IntInsnNode
import org.objectweb.asm.tree.LdcInsnNode

/**
 * @author Tigermouthbear
 * Utils to interface with nodes
 */

interface NodeUtils {
	fun getLdcInt(int: Int): AbstractInsnNode {
		if(int <= 32767 && int >= -32768) {
			return IntInsnNode(SIPUSH, int)
		} else if(int <= 127 && int >= -128) {
			return IntInsnNode(BIPUSH, int)
		}

		return when(int) {
			-1 -> InsnNode(ICONST_M1)
			0 -> InsnNode(ICONST_0)
			1 -> InsnNode(ICONST_1)
			2 -> InsnNode(ICONST_2)
			3 -> InsnNode(ICONST_3)
			4 -> InsnNode(ICONST_4)
			5 -> InsnNode(ICONST_5)
			else -> LdcInsnNode(int)
		}
	}

	fun getIntFromAin(ain: AbstractInsnNode): Int {
		return when(ain.opcode) {
			ICONST_M1 -> -1
			ICONST_0 -> 0
			ICONST_1 -> 1
			ICONST_2 -> 2
			ICONST_3 -> 3
			ICONST_4 -> 4
			ICONST_5 -> 5
			SIPUSH -> (ain as IntInsnNode).operand
			BIPUSH -> (ain as IntInsnNode).operand
			LDC -> (ain as LdcInsnNode).cst as Int
			else -> -2 // should not get here
		}
	}

	fun isReturn(ain: AbstractInsnNode): Boolean {
		return ain.opcode in IRETURN..RETURN
	}
}