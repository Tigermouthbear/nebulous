package dev.tigr.nebulous.modifiers.optimizers

import dev.tigr.nebulous.modifiers.IModifier
import org.objectweb.asm.Opcodes.NOP

/**
 * @author Tigermouthbear
 * Removes all of the unneeded NOP instructions
 */
object NOPRemover : IModifier {
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