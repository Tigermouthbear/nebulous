package dev.tigr.nebulous.modifiers.optimizers

import dev.tigr.nebulous.modifiers.AbstractModifier
import org.objectweb.asm.Opcodes.NOP

/**
 * @author Tigermouthbear
 * Removes all of the unneeded NOP instructions
 */
object NOPRemover: AbstractModifier("NOPRemover") {
    override fun modify() {
        classes.stream()
                .filter { cn -> !isExcluded(cn.name) }
                .forEach { cn ->
                    cn.methods.forEach { mn ->
                        mn.instructions.removeAll { ain -> ain.opcode == NOP }
                    }
                }
    }
}