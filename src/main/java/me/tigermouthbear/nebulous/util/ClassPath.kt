package me.tigermouthbear.nebulous.util

import me.tigermouthbear.nebulous.Nebulous
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import java.io.ByteArrayOutputStream
import java.util.jar.JarFile
import java.util.stream.Stream

/**
 * @author Tigermouthbear
 */

object ClassPath: HashMap<String, ClassNode>() {
	init {
		val classpath = System.getProperty("java.class.path").split(System.getProperty("path.separator")).toTypedArray()
		Stream.of(*classpath).filter { path: String -> path.endsWith(".jar") }.forEach(this::add)

		add("C:\\Users\\bearw\\Desktop\\test\\build\\libs\\forge-1.12.2-14.23.5.2768-PROJECT(ares)-srgBin.jar")
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