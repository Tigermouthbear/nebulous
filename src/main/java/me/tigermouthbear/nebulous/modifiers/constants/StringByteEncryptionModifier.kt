package me.tigermouthbear.nebulous.modifiers.constants

import me.tigermouthbear.nebulous.modifiers.Modifier
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*

/**
 * @author Tigermouthbear
 */

class StringByteEncryptionModifier: Modifier() {
	override fun modify() {
		classes.stream()
		.filter { cn -> !isDependency(cn.name) }
		.forEach { cn ->
			cn.methods.forEach { mn ->
				val strings: MutableList<AbstractInsnNode> = mutableListOf()

				mn.instructions.forEach { ain ->
					if(ain is LdcInsnNode && ain.cst is String) strings.add(ain)
				}

				strings.forEach { ain ->
					mn.instructions.insert(ain, obfuscate(ain))
					mn.instructions.remove(ain)
				}
			}
		}
	}

	private fun obfuscate(ain: AbstractInsnNode): InsnList {
		val insnList = InsnList().apply {
			add(TypeInsnNode(NEW, "java/lang/String"))
			add(InsnNode(DUP))

			val bytes = ((ain as LdcInsnNode).cst as String).toByteArray()

			add(getLdcInt(bytes.size)) // set array length
			add(IntInsnNode(NEWARRAY, T_BYTE)) // create array

			// write each byte of array
			for((index, b) in bytes.withIndex()) {
				add(InsnNode(DUP))
				add(getLdcInt(index))
				add(IntInsnNode(BIPUSH, b.toInt()))
				add(InsnNode(BASTORE))
			}

			// call constructor
			add(MethodInsnNode(INVOKESPECIAL,
					"java/lang/String",
					"<init>",
					"([B)V",
					false))
		}

		return insnList
	}
}