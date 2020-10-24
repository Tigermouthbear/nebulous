package dev.tigr.nebulous.modifiers.optimizers

import dev.tigr.nebulous.modifiers.AbstractModifier
import org.objectweb.asm.tree.LineNumberNode

/**
 * @author Tigermouthbear
 * Removes all of the unneeded line number instructions
 */
object LineNumberRemover: AbstractModifier("LineNumberRemover") {
    override fun modify() {
        classes.stream()
                .filter { cn -> !isExcluded(cn.name) }
                .forEach { cn ->
                    cn.methods.forEach { mn ->
                        mn.instructions.removeAll { ain -> ain is LineNumberNode }
                    }
                }
    }
}