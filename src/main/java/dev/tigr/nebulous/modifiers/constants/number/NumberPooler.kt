package dev.tigr.nebulous.modifiers.constants.number

import dev.tigr.nebulous.modifiers.IModifier
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*

/**
 * @author Tigermouthbear
 */
object NumberPooler: IModifier {
    private const val arrayName = "numberPoolArray"

    override fun modify() {
        // move all numbers to array
        classes.stream()
                .filter { cn -> !isExcluded(cn.name) }
                .forEach { cn ->
                    // add all numbers to list
                    val numberMap: MutableMap<AbstractInsnNode, MethodNode> = mutableMapOf()
                    cn.methods.forEach { mn ->
                        mn.instructions.forEach { ain ->
                            if((ain is LdcInsnNode && ain.cst is Int) || (ain.opcode == BIPUSH || ain.opcode == SIPUSH)
                                    || (ain.opcode in ICONST_M1..ICONST_5)) {
                                numberMap[ain] = mn
                            }
                        }
                    }

                    if(numberMap.isNotEmpty()) {
                        // create array
                        cn.fields.add(FieldNode(
                                (if(cn.access and ACC_INTERFACE != 0) ACC_PUBLIC else ACC_PRIVATE) or (if(cn.version > V1_8) 0 else ACC_FINAL) or ACC_STATIC,
                                arrayName,
                                "[I",
                                null,
                                null))

                        // make sure clinit is present in class, if not, create it
                        var clinit = getMethod(cn, "<clinit>")
                        if(clinit == null) {
                            clinit = MethodNode(ACC_STATIC, "<clinit>", "()V", null, arrayOf<String>())
                            cn.methods.add(clinit)
                        }
                        if(clinit.instructions == null) clinit.instructions = InsnList()

                        // create array with values and
                        val arrayInstructions = InsnList().apply {
                            add(getLdcInt(numberMap.size)) // set array length
                            add(IntInsnNode(NEWARRAY, T_INT)) // create array

                            // write each number of array
                            for((index, ain) in numberMap.keys.withIndex()) {
                                // add to array
                                add(InsnNode(DUP))
                                add(getLdcInt(index))
                                add(getLdcInt(getIntFromAin(ain)))
                                add(InsnNode(IASTORE))

                                // replace number with reference to array
                                numberMap[ain]!!.instructions.insert(ain, InsnList().apply {
                                    add(FieldInsnNode(
                                            GETSTATIC,
                                            cn.name,
                                            arrayName,
                                            "[I"
                                    ))
                                    add(getLdcInt(index))
                                    add(InsnNode(IALOAD))
                                })
                                numberMap[ain]!!.instructions.remove(ain)
                            }

                            // put in array
                            add(FieldInsnNode(
                                    PUTSTATIC,
                                    cn.name,
                                    arrayName,
                                    "[I"))
                        }

                        if(clinit.instructions == null || clinit.instructions.first == null) {
                            clinit.instructions.add(arrayInstructions)
                            clinit.instructions.add(InsnNode(RETURN))
                        } else {
                            clinit.instructions.insertBefore(clinit.instructions.first, arrayInstructions)
                        }
                    }
                }
    }

    override fun getName(): String {
        return "Number Pooler"
    }
}