package me.tigermouthbear.nebulous.modifiers

import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.LdcInsnNode

class AllCapsModifier : Modifier() {
    override fun modify() {
        for (cn in classMap.values) {
            for (mn in cn.methods) { // Iterate instructions in method
                for (ain in mn.instructions.toArray()) { // If the instruction is loading a constant value
                    if (ain.opcode == Opcodes.LDC) { // Cast current instruction to Ldc
// If the constant is a string then capitalize it.
                        val ldc = ain as LdcInsnNode
                        if (ldc.cst is String) {
                            ldc.cst = ldc.cst.toString().toUpperCase()
                        }
                    }
                }
            }
        }
    }
}