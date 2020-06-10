package dev.tigr.nebulous.util

import dev.tigr.nebulous.Nebulous
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode

/**
 * @author Tigermouthbear
 * General Utils
 */

interface Utils {
	fun isExcluded(name: String): Boolean {
		if(name == "me/tigermouthbear/ares/Ares") return true

		val path = getPath(name)
		for(exclusion in Nebulous.getExclusions()) {
			if(path.contains(exclusion)) return true
		}
		return false
	}

	fun getPath(name: String): String {
		if(!name.contains("/")) return ""
		val reversedString = name.reversed()
		val path = reversedString.substring(reversedString.indexOf("/"))
		return path.reversed()
	}

	fun getMethod(cn: ClassNode, method: String): MethodNode? {
		cn.methods.forEach { mn ->
			if(mn.name == method) return mn
		}

		return null
	}
}