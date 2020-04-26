package me.tigermouthbear.nebulous.modifiers.constants

import me.tigermouthbear.nebulous.modifiers.Modifier
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*
import java.util.*

/**
 * @author Tigermouthbear
 */

class StringEncryptionModifier: Modifier() {
	override fun modify() {
		val encryptedStrings: MutableList<EncryptedString> = mutableListOf()

		classes.stream()
		.filter { cn -> !isDependency(cn.name) }
		.forEach { cn ->
			cn.methods.forEach { mn ->
				val strings: MutableList<LdcInsnNode> = mutableListOf()

				mn.instructions.forEach { ain ->
					if(ain is LdcInsnNode && ain.cst is String) strings.add(ain)
				}

				strings.forEach { ldc -> encryptedStrings.add(EncryptedString(cn, mn, ldc)) }
			}
		}

		val classesWithMethod: MutableList<ClassNode> = mutableListOf()
		encryptedStrings.forEach { encryptedString ->
			encryptedString.apply {
				if(!classesWithMethod.contains(cn)) {
					createDecryptionMethod(cn)
					classesWithMethod.add(cn)
				}

				mn.instructions.insert(ldc, obfuscate(cn, ldc))
				mn.instructions.remove(ldc)
			}
		}
	}

	private fun obfuscate(cn: ClassNode, ain: AbstractInsnNode): InsnList {
		val ldc = ain as LdcInsnNode

		val insnList = InsnList().apply {
			val encrypted = String(Base64.getEncoder().encode((ldc.cst as String).toByteArray()))

			add(LdcInsnNode(encrypted))

			// call constructor
			add(MethodInsnNode(INVOKESTATIC,
					cn.name,
					"decrypt",
					"(Ljava/lang/String;)Ljava/lang/String;",
					false))
		}

		return insnList
	}

	private fun createDecryptionMethod(cn: ClassNode) {
		val mn = MethodNode(
			ACC_PRIVATE + ACC_STATIC,
			"decrypt",
			"(Ljava/lang/String;)Ljava/lang/String;",
			null,
			null
		)

		mn.instructions.add(InsnList().apply {
			add(TypeInsnNode(NEW, "java/lang/String"))
			add(InsnNode(DUP))
			add(
				MethodInsnNode(
					INVOKESTATIC,
					"java/util/Base64",
					"getDecoder",
					"()Ljava/util/Base64\$Decoder;",
					false)
			)
			add(IntInsnNode(ALOAD, 0))
			add(
				MethodInsnNode(
						INVOKEVIRTUAL,
						"java/util/Base64\$Decoder",
						"decode",
						"(Ljava/lang/String;)[B",
						false)
			)
			add(
				MethodInsnNode(
						INVOKESPECIAL,
						"java/lang/String",
						"<init>",
						"([B)V",
						false)
			)
			add(InsnNode(ARETURN))
		})

		cn.methods.add(mn)
	}

	class EncryptedString(val cn: ClassNode, val mn: MethodNode, val ldc: LdcInsnNode)
}