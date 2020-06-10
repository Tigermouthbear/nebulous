package dev.tigr.nebulous.modifiers.misc

import dev.tigr.nebulous.modifiers.IModifier
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.ClassWriter.COMPUTE_MAXS
import java.io.ByteArrayOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream

object Packer: IModifier {
	override fun modify() {
		val remove: MutableList<String> = mutableListOf()

		val bos = ByteArrayOutputStream()
		val jos = JarOutputStream(bos)

		// write classes into jar output stream
		classes.forEach { cn ->
			jos.putNextEntry(JarEntry(cn.name + ".class"))

			val writer = ClassWriter(COMPUTE_MAXS)
			writer.newUTF8("Secured by Nebulous")
			cn.accept(writer)
			jos.write(writer.toByteArray())
			jos.closeEntry()

			remove.add(cn.name)
		}

		// copy files into jar output stream
		filesMap.entries.forEach { (key, value) ->
			jos.putNextEntry(JarEntry(key))
			jos.write(value)
			jos.closeEntry()

			remove.add(key)
		}

		jos.close()

		filesMap["test.jar"] = bos.toByteArray()

		files.forEach { file -> if(remove.contains(file)) filesMap.remove(file) }
		classes.forEach { cn -> if(remove.contains(cn.name)) classMap.remove(cn.name) }
	}

	override fun getName(): String {
		return "Packer"
	}
}