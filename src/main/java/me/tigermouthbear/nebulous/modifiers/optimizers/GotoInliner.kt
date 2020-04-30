package me.tigermouthbear.nebulous.modifiers.optimizers

import me.tigermouthbear.nebulous.modifiers.IModifier
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.JumpInsnNode

/**
 * @author Tigermouthbear
 * Inlines goto-goto instructions by setting the first goto's target to the second goto's target
 */

object GotoInliner: IModifier {
	override fun modify() {
		classes.stream()
		.filter { cn -> !isExcluded(cn.name) }
		.forEach { cn ->
			cn.methods.forEach { mn ->
				mn.instructions
				.filter { ain -> ain.opcode == GOTO }
				.forEach { ain ->
					val jump = ain as JumpInsnNode
					val target = jump.label.next

					if(target != null && target.opcode == GOTO) {
						jump.label = (target as JumpInsnNode).label
					}
				}
			}
		}
	}

	override fun getName(): String {
		return "Goto Inliner"
	}
}