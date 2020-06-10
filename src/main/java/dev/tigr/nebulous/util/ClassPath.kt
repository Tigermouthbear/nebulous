package dev.tigr.nebulous.util

import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import java.io.ByteArrayOutputStream
import java.util.jar.JarFile
import java.util.stream.Stream

/**
 * @author Tigermouthbear
 * Holds class nodes of external nodes for use with obfuscation
 */

object ClassPath: HashMap<String, ClassNode>() {
	fun load(paths: List<String>) {
		val classpath = System.getProperty("java.class.path").split(System.getProperty("path.separator")).toTypedArray()
		Stream.of(*classpath).filter { path: String -> path.endsWith(".jar") }.forEach(this::add)

		paths.forEach(this::add)
	}

	fun add(path: String) {
		val jar = JarFile(path)
		val entries = jar.entries()

		while(entries.hasMoreElements()) {
			val entry = entries.nextElement()

			jar.getInputStream(entry).use { `in` ->
				val bytes: ByteArray
				val baos = ByteArrayOutputStream()
				val buf = ByteArray(256)
				var n: Int
				while(`in`.read(buf).also { n = it } != -1) {
					baos.write(buf, 0, n)
				}
				bytes = baos.toByteArray()

				if(entry.name.endsWith(".class")) {
					val c = ClassNode()
					ClassReader(bytes).accept(c, ClassReader.SKIP_CODE or ClassReader.SKIP_DEBUG)
					put(c.name, c)
				}
			}
		}
	}
}