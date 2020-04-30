package me.tigermouthbear.nebulous

import me.tigermouthbear.nebulous.config.ArrayConfig
import me.tigermouthbear.nebulous.config.ConfigReader
import me.tigermouthbear.nebulous.config.StringConfig
import me.tigermouthbear.nebulous.modifiers.IModifier
import me.tigermouthbear.nebulous.modifiers.constants.number.NumberPooler
import me.tigermouthbear.nebulous.modifiers.constants.string.StringEncryptor
import me.tigermouthbear.nebulous.modifiers.constants.string.StringPooler
import me.tigermouthbear.nebulous.modifiers.constants.string.StringSplitter
import me.tigermouthbear.nebulous.modifiers.misc.DebugInfoRemover
import me.tigermouthbear.nebulous.modifiers.misc.FullAccessFlags
import me.tigermouthbear.nebulous.modifiers.misc.MemberShuffler
import me.tigermouthbear.nebulous.modifiers.optimizers.GotoInliner
import me.tigermouthbear.nebulous.modifiers.optimizers.GotoReturnInliner
import me.tigermouthbear.nebulous.modifiers.optimizers.LineNumberRemover
import me.tigermouthbear.nebulous.modifiers.optimizers.NOPRemover
import me.tigermouthbear.nebulous.modifiers.renamers.ClassRenamer
import me.tigermouthbear.nebulous.modifiers.renamers.FieldRenamer
import me.tigermouthbear.nebulous.modifiers.renamers.MethodRenamer
import me.tigermouthbear.nebulous.util.ClassPath
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
	private val exclusions = ArrayConfig("exclusions")
	private val libraries = ArrayConfig("libraries")

	private val files: MutableMap<String, ByteArray> = HashMap()
	private val classNodes: MutableMap<String, ClassNode> = HashMap()
	private lateinit var manifest: Manifest

	fun run(config: File) {
		val startTime = System.currentTimeMillis()

		ConfigReader.read(config)

		// load jar and libraries
		openJar(JarFile(input.value))
		ClassPath.load(getLibraries())

		val modifiers: List<IModifier> =
		arrayListOf(
				// strings
				StringPooler,
				StringEncryptor,
				StringSplitter,

				// numbers
				NumberPooler,

				// names
				FieldRenamer,
				MethodRenamer,
				ClassRenamer,

				// misc
				MemberShuffler,
				FullAccessFlags,
				DebugInfoRemover,

				// optimizers
				NOPRemover,
				LineNumberRemover,
				GotoInliner,
				GotoReturnInliner
		)

		modifiers.forEach { modifier ->
			val current = System.currentTimeMillis()
			modifier.modify()
			println(modifier.getName() + " completed in " + (System.currentTimeMillis() - current) + " milliseconds")
		}

		saveJar()

		println("\nNebulous finished in " + (System.currentTimeMillis() - startTime) + " milliseconds")
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
			writer.newUTF8("Secured by Nebulous")
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

	fun getExclusions(): List<String> {
		val temp: MutableList<String> = mutableListOf()
		for(i in 0 until exclusions.value!!.length()) temp.add(exclusions.value!!.getString(i))
		return temp
	}

	fun getLibraries(): List<String> {
		val temp: MutableList<String> = mutableListOf()
		for(i in 0 until libraries.value!!.length()) temp.add(libraries.value!!.getString(i))
		return temp
	}
}