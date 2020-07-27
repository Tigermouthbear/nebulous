package dev.tigr.nebulous.modifiers.constants.string

import dev.tigr.nebulous.modifiers.IModifier
import org.objectweb.asm.Opcodes
import org.objectweb.asm.tree.*

/**
 * @author Tigermouthbear
 * Moves all string ldcs to a single array in a given class
 */
object StringPooler : IModifier {
    private const val arrayName = "stringPoolArray"

    override fun modify() {
        // move all strings to array
        classes.stream()
                .filter { cn -> !isExcluded(cn.name) }
                .forEach { cn ->
                    // add all ldcs to list
                    val ldcMap: MutableMap<LdcInsnNode, MethodNode> = mutableMapOf()
                    cn.methods.forEach { mn ->
                        mn.instructions.forEach { ain ->
                            if (ain is LdcInsnNode && ain.cst is String) ldcMap[ain] = mn
                        }
                    }

                    if (ldcMap.isNotEmpty()) {
                        // create array
                        cn.fields.add(FieldNode(
                                (if (cn.access and Opcodes.ACC_INTERFACE != 0) Opcodes.ACC_PUBLIC else Opcodes.ACC_PRIVATE) or (if (cn.version > Opcodes.V1_8) 0 else Opcodes.ACC_FINAL) or Opcodes.ACC_STATIC,
                                arrayName,
                                "[Ljava/lang/String;",
                                null,
                                null))

                        // make sure clinit is present in class, if not, create it
                        var clinit = getMethod(cn, "<clinit>")
                        if (clinit == null) {
                            clinit = MethodNode(Opcodes.ACC_STATIC, "<clinit>", "()V", null, arrayOf<String>())
                            cn.methods.add(clinit)
                        }
                        if (clinit.instructions == null) clinit.instructions = InsnList()

                        // create array with values and
                        val arrayInstructions = InsnList().apply {
                            add(getLdcInt(ldcMap.size)) // set array length
                            add(TypeInsnNode(Opcodes.ANEWARRAY, "java/lang/String")) // create array

                            // write each ldc of array
                            for ((index, ldc) in ldcMap.keys.withIndex()) {
                                // add to array
                                add(InsnNode(Opcodes.DUP))
                                add(getLdcInt(index))
                                add(LdcInsnNode(ldc.cst as String))
                                add(InsnNode(Opcodes.AASTORE))

                                // replace ldc with reference to array
                                ldcMap[ldc]!!.instructions.insert(ldc, InsnList().apply {
                                    add(FieldInsnNode(
                                            Opcodes.GETSTATIC,
                                            cn.name,
                                            arrayName,
                                            "[Ljava/lang/String;"
                                    ))
                                    add(getLdcInt(index))
                                    add(InsnNode(Opcodes.AALOAD))
                                })
                                ldcMap[ldc]!!.instructions.remove(ldc)
                            }

                            // put in array
                            add(FieldInsnNode(
                                    Opcodes.PUTSTATIC,
                                    cn.name,
                                    arrayName,
                                    "[Ljava/lang/String;"))
                        }

                        if (clinit.instructions == null || clinit.instructions.first == null) {
                            clinit.instructions.add(arrayInstructions)
                            clinit.instructions.add(InsnNode(Opcodes.RETURN))
                        } else {
                            clinit.instructions.insertBefore(clinit.instructions.first, arrayInstructions)
                        }
                    }
                }
    }

    override fun getName(): String {
        return "String Pooler"
    }
}