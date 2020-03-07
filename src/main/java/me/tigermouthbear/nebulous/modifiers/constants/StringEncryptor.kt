package me.tigermouthbear.nebulous.modifiers.constants

class StringEncryptor {
    /*override fun modify() {
        val cn = ClassNode()
                .apply {
                    this.access = Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL
                    this.version = classes.findFirst().get().version
                    this.name = RandomString.genRandomString()
                    this.signature = null
                    this.superName = "java/lang/Object"
                    classMap.put(name, this)
                }
        val decoderMethod = createDecoderMethod()
        cn.methods.add(decoderMethod)

        for (classNode in classes) {
            for (method in classNode.methods) {
                val modifier = InstructionModifier()
                for (insn in method.instructions)
                    if (insn is LdcInsnNode) modifyLdc(modifier, insn, cn, decoderMethod)
                modifier.apply(method)
            }
        }
    }

    fun modifyLdc(modifier: InstructionModifier, insnNode: LdcInsnNode, classNode: ClassNode, decoderMethod: MethodNode) {
        val list = InsnList().apply {
            // get bytes
            val bytes = encodeObject(insnNode.cst)

            add(Utils.getLdcInt(bytes.size)) // set array length
            add(IntInsnNode(Opcodes.NEWARRAY, Opcodes.T_BYTE)) // create array

            // write each byte of array
            for((index, b) in bytes.withIndex()) {
                add(InsnNode(Opcodes.DUP))
                add(Utils.getLdcInt(index))
                add(IntInsnNode(Opcodes.BIPUSH, b.toInt()))
                add(InsnNode(Opcodes.BASTORE))
            }

            // call decoder method
            add(MethodInsnNode(
                    Opcodes.INVOKESTATIC,
                    classNode.name,
                    decoderMethod.name,
                    decoderMethod.desc,
                    false
            ))

            add(TypeInsnNode(Opcodes.CHECKCAST, insnNode.cst.javaClass.name.replace(".", "/")))
        }

        modifier.replace(insnNode, list)
    }

    fun encodeObject(obj: Any): ByteArray {
        val bos = ByteArrayOutputStream()
        val out = ObjectOutputStream(bos)
        out.writeObject(obj)
        out.flush()
        val bytes = bos.toByteArray()
        bos.close()
        return bytes
    }

    fun createDecoderMethod(): MethodNode {
        val decoderMethod = MethodNode(
                Opcodes.ACC_PUBLIC + Opcodes.ACC_STATIC,
                "t",
                "([B)Ljava/lang/Object;",
                null,
                null
        )

        val list = InsnList().apply {
            add(TypeInsnNode(Opcodes.NEW, "java/io/ByteArrayInputStream"))
            add(InsnNode(Opcodes.DUP))
            add(VarInsnNode(Opcodes.ALOAD, 0))
            add(MethodInsnNode(Opcodes.INVOKESPECIAL, "java/io/ByteArrayInputStream", "<init>", "([B)V", false))
            add(VarInsnNode(Opcodes.ASTORE, 1))

            add(TypeInsnNode(Opcodes.NEW, "java/io/ObjectInputStream"))
            add(InsnNode(Opcodes.DUP))
            add(VarInsnNode(Opcodes.ALOAD, 1))
            add(MethodInsnNode(Opcodes.INVOKESPECIAL, "java/io/ObjectInputStream", "<init>", "(Ljava/io/InputStream;)V", false))
            add(MethodInsnNode(Opcodes.INVOKEVIRTUAL, "java/io/ObjectInputStream", "readObject", "()Ljava/lang/Object;", false))
            add(InsnNode(Opcodes.ARETURN))
        }

        decoderMethod.instructions.add(list)
        return decoderMethod
    }*/
}