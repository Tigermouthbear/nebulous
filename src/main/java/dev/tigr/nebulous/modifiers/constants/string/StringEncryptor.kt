package dev.tigr.nebulous.modifiers.constants.string

import dev.tigr.nebulous.Nebulous
import dev.tigr.nebulous.encryption.AESStringEncryptor
import dev.tigr.nebulous.encryption.BlowfishStringEncryptor
import dev.tigr.nebulous.encryption.IStringEncryptor
import dev.tigr.nebulous.encryption.PBEStringEncryptor
import dev.tigr.nebulous.modifiers.IModifier
import dev.tigr.nebulous.util.Dictionary
import org.objectweb.asm.ClassReader
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*
import java.security.SecureRandom

/**
 * @author Tigermouthbear
 * Encrypts all string ldcs with a custom cipher
 */

object StringEncryptor: IModifier {
	private val RANDOM = SecureRandom()
	private val encryptors = arrayOf(PBEStringEncryptor(), BlowfishStringEncryptor(), AESStringEncryptor())

	override fun modify() {
		val encryptedStrings: MutableList<EncryptedString> = mutableListOf()

		// add all ldc strings to encrypted strings list
		classes.stream()
		.filter { cn -> !isExcluded(cn.name) }
		.forEach { cn ->
			cn.methods.forEach { mn ->
				mn.instructions.toArray()
				.filter { ain -> ain is LdcInsnNode && ain.cst is String }
				.forEach { ain -> encryptedStrings.add(EncryptedString(cn, mn, ain as LdcInsnNode, encryptors[RANDOM.nextInt(encryptors.size)], Dictionary.genRandomString())) }
			}
		}

		// encrypt all strings
		encryptedStrings.forEach { encryptedString -> encrypt(encryptedString)}
	}

	private fun encrypt(encryptedString: EncryptedString) {
		encryptedString.apply {
			// add decryptor method if its not there already
			if(cn.methods.stream().filter { mn -> mn.name == encryptor.name }.count() <= 0) {
				// read encryptors class
				val classReader = ClassReader(Nebulous::class.java.getResourceAsStream("/" + encryptor.javaClass.name.replace('.', '/') + ".class"))
				val enc = ClassNode()
				classReader.accept(enc, 0)

				// read decryptor method in class
				val method: MethodNode? = getMethod(enc, "decrypt")

				// set method name to the name of the encryptor
				method!!.access = ACC_PRIVATE or ACC_STATIC
				method.name = encryptor.name

				// add decryptor to the class
				cn.methods.add(method)
			}

			// create decryption method call
			val insnList =  InsnList().apply {
				// encrypt string
				val encrypted = encryptor.encrypt(key, ldc.cst as String)

				// pass key the encrypted string through method
				add(LdcInsnNode(key))
				add(LdcInsnNode(encrypted))

				// call constructor
				add(MethodInsnNode(INVOKESTATIC,
						cn.name,
						encryptor.name,
						"(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",
						false))
			}

			// replace ldc with method call
			mn.instructions.insert(ldc, insnList)
			mn.instructions.remove(ldc)
		}
	}

	class EncryptedString(val cn: ClassNode, val mn: MethodNode, val ldc: LdcInsnNode, val encryptor: IStringEncryptor, val key: String)

	override fun getName(): String {
		return "String Encryptor"
	}
}