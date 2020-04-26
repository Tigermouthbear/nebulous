package me.tigermouthbear.nebulous.modifiers

import me.tigermouthbear.nebulous.Nebulous
import me.tigermouthbear.nebulous.util.Utils
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.SimpleRemapper
import org.objectweb.asm.tree.ClassNode
import java.util.jar.Manifest
import java.util.stream.Collectors

/**
 * @author Tigermouthbear
 */

abstract class Modifier: Utils {
	protected val classMap: MutableMap<String, ClassNode>
		get() = Nebulous.getClassNodes()

	protected val classes: List<ClassNode>
		get() = ArrayList(classMap.values)

	protected val filesMap: MutableMap<String, ByteArray>
		get() = Nebulous.getFiles()

	protected val manifest: Manifest
		get() = Nebulous.getManifest()

	abstract fun modify()

	protected fun applyRemap(remap: Map<String?, String?>?) {
		val remapper = SimpleRemapper(remap)
		for(node in classes) {
			val copy = ClassNode()
			val adapter = ClassRemapper(copy, remapper)
			node.accept(adapter)
			classMap.remove(node.name)
			classMap[node.name] = copy
		}
	}

	protected fun getImplementations(target: ClassNode): List<ClassNode> {
		return classes.stream().filter { cn -> cn.interfaces.contains(target.name) }.collect(Collectors.toList())
	}

	protected fun getExtensions(target: ClassNode): List<ClassNode> {
		val extensions: MutableList<ClassNode> = mutableListOf()

		classes.stream()
				.filter { cn -> cn.superName == target.name }
				.forEach {cn ->
					extensions.add(cn)
					extensions.addAll(getExtensions(cn))
				}

		return extensions
	}

	protected fun isDependency(name: String): Boolean {
		val path = getPath(name)
		for(depencency in Nebulous.getDependencies()) {
			if(path.contains(depencency)) return true
		}
		return false
	}

	private fun getPath(name: String): String {
		if(!name.contains("/")) return ""
		val reversedString = reverseString(name)
		val path = reversedString.substring(reversedString.indexOf("/"))
		return reverseString(path)
	}

	private fun reverseString(string: String): String {
		val sb = StringBuilder()
		val chars = string.toCharArray()
		for(i in chars.indices.reversed()) sb.append(chars[i])
		return sb.toString()
	}
}