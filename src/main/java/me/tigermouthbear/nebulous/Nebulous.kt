package me.tigermouthbear.nebulous

import me.tigermouthbear.nebulous.config.ArrayConfig
import me.tigermouthbear.nebulous.config.ConfigReader
import me.tigermouthbear.nebulous.config.StringConfig
import me.tigermouthbear.nebulous.modifiers.renaming.ClassNameModifier
import me.tigermouthbear.nebulous.modifiers.renaming.FieldNameModifier
import me.tigermouthbear.nebulous.modifiers.Modifier
import me.tigermouthbear.nebulous.modifiers.constants.StringByteEncryptionModifier
import me.tigermouthbear.nebulous.modifiers.constants.StringEncryptionModifier
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.jar.Manifest

/**
 * @author Tigermouthbear
 */

object Nebulous {
	private val input = StringConfig("input")
	private val output = StringConfig("output")
	private val dependencies = ArrayConfig("dependencies")

    private val files: MutableMap<String, ByteArray> = HashMap()
    private val classNodes: MutableMap<String, ClassNode> = HashMap()
	private lateinit var manifest: Manifest

	fun run(config: File) {
		ConfigReader.read(config)

		openJar(JarFile(input.value))

		//val modifiers: List<Modifier> = arrayListOf(FieldNameModifier(), ClassNameModifier())
		val modifiers: List<Modifier> = arrayListOf(StringEncryptionModifier(), StringByteEncryptionModifier(), FieldNameModifier(), ClassNameModifier())

		modifiers.forEach { modifier -> modifier.modify() }

		saveJar()
	}

	private fun openJar(jar: JarFile) {
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

				if(!entry.name.endsWith(".class")) {
					files[entry.name] = bytes
				} else {
					val c = ClassNode()
					ClassReader(bytes).accept(c, ClassReader.EXPAND_FRAMES)
					classNodes.put(c.name, c)
				}
			}
		}

		// open manifest
		manifest = Manifest(ByteArrayInputStream(files["META-INF/MANIFEST.MF"]))
	}

	private fun saveJar() {
		// save manifest
		val mos = ByteArrayOutputStream()
		manifest.write(mos)
		files["META-INF/MANIFEST.MF"] = mos.toByteArray()

		var location: String = output.value
		if(!location.endsWith(".jar")) location += ".jar"

		val jarPath = Paths.get(location)
		Files.deleteIfExists(jarPath)

		val outJar = JarOutputStream(Files.newOutputStream(jarPath, *arrayOf(StandardOpenOption.CREATE, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)))

        // write classes into jar
		classNodes.values.forEach { cn ->
			outJar.putNextEntry(JarEntry(cn.name + ".class"))

			val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
			cn.accept(writer)
			outJar.write(writer.toByteArray())

			outJar.closeEntry()
		}

		// copy files into jar
		files.entries.forEach { (key, value) ->
			outJar.putNextEntry(JarEntry(key))
			outJar.write(value)
			outJar.closeEntry()
		}

		outJar.close()
	}

    fun getClassNodes(): MutableMap<String, ClassNode> {
        return classNodes
    }

    fun getFiles(): MutableMap<String, ByteArray> {
        return files
    }

	fun getManifest(): Manifest {
		return manifest
	}

	fun getDependencies(): List<String> {
		val temp: MutableList<String> = mutableListOf()
		for(i in 0 until dependencies.value!!.length()) temp.add(dependencies.value!!.getString(i))
		return temp
	}
}