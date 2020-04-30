package me.tigermouthbear.nebulous.modifiers.misc

import me.tigermouthbear.nebulous.modifiers.IModifier
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode

/**
 * @author Tigermouthbear
 * Removes debug info such as sourcefile
 */

object DebugInfoRemover: IModifier {
	override fun modify() {
		val map: MutableMap<String, ClassNode> = mutableMapOf()

		classes.stream()
		.filter { cn -> !isExcluded(cn.name) }
		.forEach { cn ->
			val cw = ClassWriter(ClassWriter.COMPUTE_MAXS)
			cn.accept(cw)
			val clone = ClassNode()
			ClassReader(cw.toByteArray()).accept(clone, ClassReader.SKIP_DEBUG)
			map[clone.name] = clone
		}

		classes.stream()
		.filter { cn -> isExcluded(cn.name) }
		.forEach { cn -> map[cn.name] = cn }

		classMap.clear()
		classMap.putAll(map)
	}


	override fun getName(): String {
		return "Debug Info Remover"
	}
}