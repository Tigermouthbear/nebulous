package me.tigermouthbear.nebulous.util

import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.AbstractInsnNode
import org.objectweb.asm.tree.InsnNode
import org.objectweb.asm.tree.IntInsnNode
import org.objectweb.asm.tree.LdcInsnNode

object Utils {
    fun getLdcInt(int: Int) : AbstractInsnNode {
        if (int <= 32767 && int >= -32768) {
            return IntInsnNode(SIPUSH, int)
        } else if (int <= 127 && int >= -128) {
            return IntInsnNode(BIPUSH, int)
        }

        return when (int) {
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
}