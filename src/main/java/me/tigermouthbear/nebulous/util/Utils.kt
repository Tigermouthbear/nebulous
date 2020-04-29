package me.tigermouthbear.nebulous.util

import me.tigermouthbear.nebulous.Nebulous
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.*

interface Utils {
	fun isExcluded(name: String): Boolean {
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