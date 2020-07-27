package dev.tigr.nebulous.modifiers.optimizers

import dev.tigr.nebulous.modifiers.IModifier
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.JumpInsnNode

/**
 * @author Tigermouthbear
 * Inlines goto-return instructions by setting the goto's jump node to a return
 */
object GotoReturnInliner : IModifier {
    override fun modify() {
        classes.stream()
                .filter { cn -> !isExcluded(cn.name) }
                .forEach { cn ->
                    cn.methods.forEach { mn ->
                        mn.instructions
                                .filter { ain -> ain.opcode == Opcodes.GOTO }
                                .forEach { ain ->
                                    val jump = ain as JumpInsnNode
                                    val target = jump.label.next

                                    if (target != null && isReturn(target)) {
                                        mn.instructions.set(ain, InsnNode(target.opcode))
                                    }
                                }
                    }
                }
    }

    override fun getName(): String {
        return "Goto-Return Inliner"
    }
}