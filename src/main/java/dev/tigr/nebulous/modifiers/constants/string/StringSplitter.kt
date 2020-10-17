package dev.tigr.nebulous.modifiers.constants.string

import dev.tigr.nebulous.modifiers.IModifier
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*

/**
 * @author Tigermouthbear
 * Splits all string ldcs into their given byte arrays
 */
object StringSplitter: IModifier {
    override fun modify() {
        classes.stream()
                .filter { cn -> !isExcluded(cn.name) }
                .forEach { cn ->
                    cn.methods.forEach { mn ->
                        val strings: MutableList<LdcInsnNode> = mutableListOf()

                        mn.instructions.forEach { ain ->
                            if(ain is LdcInsnNode && ain.cst is String) strings.add(ain)
                        }

                        strings.forEach { ldc ->
                            mn.instructions.insert(ldc, split(ldc))
                            mn.instructions.remove(ldc)
                        }
                    }
                }
    }

    private fun split(ldc: LdcInsnNode): InsnList {
        return InsnList().apply {
            add(TypeInsnNode(NEW, "java/lang/String"))
            add(InsnNode(DUP))

            val bytes = (ldc.cst as String).toByteArray()

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
    }

    override fun getName(): String {
        return "String Splitter"
    }
}