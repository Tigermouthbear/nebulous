package dev.tigr.nebulous.modifiers

import dev.tigr.nebulous.Nebulous
import dev.tigr.nebulous.util.NodeUtils
import dev.tigr.nebulous.util.Utils
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.SimpleRemapper
import org.objectweb.asm.tree.ClassNode
import java.util.jar.Manifest
import java.util.stream.Collectors

/**
 * @author Tigermouthbear
 */

interface IModifier: Utils, NodeUtils {
	val classMap: MutableMap<String, ClassNode>
		get() = Nebulous.getClassNodes()

	val classes: List<ClassNode>
		get() = ArrayList(classMap.values)

	val filesMap: MutableMap<String, ByteArray>
		get() = Nebulous.getFiles()

	val files: List<String>
		get() = ArrayList(filesMap.keys)

	val manifest: Manifest
		get() = Nebulous.getManifest()

	fun modify()

	fun getName(): String

	fun applyRemap(remap: Map<String?, String?>?) {
		val remapper = SimpleRemapper(remap)
		for(node in classes) {
			val copy = ClassNode()
			val adapter = ClassRemapper(copy, remapper)
			node.accept(adapter)
			classMap.remove(node.name)
			classMap[node.name] = copy
		}
	}

	fun getImplementations(target: ClassNode): List<ClassNode> {
		return classes.stream().filter { cn -> cn.interfaces.contains(target.name) }.collect(Collectors.toList())
	}

	fun getExtensions(target: ClassNode): List<ClassNode> {
		val extensions: MutableList<ClassNode> = mutableListOf()

		classes.stream()
				.filter { cn -> cn.superName == target.name }
				.forEach {cn ->
					extensions.add(cn)
					extensions.addAll(getExtensions(cn))
				}

		return extensions
	}
}