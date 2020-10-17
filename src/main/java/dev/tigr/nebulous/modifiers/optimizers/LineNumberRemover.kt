package dev.tigr.nebulous.modifiers.optimizers

import dev.tigr.nebulous.modifiers.IModifier
import org.objectweb.asm.tree.LineNumberNode

/**
 * @author Tigermouthbear
 * Removes all of the unneeded line number instructions
 */
object LineNumberRemover: IModifier {
    override fun modify() {
        classes.stream()
                .filter { cn -> !isExcluded(cn.name) }
                .forEach { cn ->
                    cn.methods.forEach { mn ->
                        mn.instructions.removeAll { ain -> ain is LineNumberNode }
                    }
                }
    }

    override fun getName(): String {
        return "Line Number Remover"
    }
}