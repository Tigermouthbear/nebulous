package me.tigermouthbear.nebulous.modifiers.optimizers

import me.tigermouthbear.nebulous.modifiers.IModifier
import org.objectweb.asm.Opcodes.*

/**
 * @author Tigermouthbear
 * Removes all of the unneeded NOP instructions
 */

class NOPRemover: IModifier {
	override fun modify() {
		classes.stream()
		.filter { cn -> !isExcluded(cn.name) }
		.forEach { cn ->
			cn.methods.forEach { mn ->
				mn.instructions.removeAll { ain -> ain.opcode == NOP }
			}
		}
	}

	override fun getName(): String {
		return "NOP Remover"
	}
}