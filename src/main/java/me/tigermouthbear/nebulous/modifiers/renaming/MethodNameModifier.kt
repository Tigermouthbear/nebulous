package me.tigermouthbear.nebulous.modifiers.renaming

import me.tigermouthbear.nebulous.modifiers.Modifier
import me.tigermouthbear.nebulous.util.Dictionary
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import java.util.*

/**
 * @author Tigermouthbear
 */

class MethodNameModifier: Modifier() {
	private val blacklist = arrayListOf("main", "createUI")

	override fun modify() {
		val remap: MutableMap<String?, String?> = mutableMapOf()
		val methodMap: MutableMap<MethodNode, ClassNode> = mutableMapOf()

		classes.stream()
				.filter { cn -> !isDependency(cn.name) }
				.forEach { cn ->
					cn.methods.stream()
						.filter { mn -> !blacklist.contains(mn.name) && !mn.name.startsWith("<") && mn.access and ACC_NATIVE == 0 }
						.forEach { mn -> methodMap[mn] = cn }
				}

		for((mn, owner) in methodMap.entries) {
			val name = Dictionary.getNewName()

			val stack = Stack<ClassNode?>()
			stack.add(owner)

			while(!stack.empty()) {
				val cn = stack.pop()
				remap[cn!!.name + "." + mn.name + mn.desc] = name

				//add classes which implement or extend the class node to the stack so that their fields get remapped
				stack.addAll(getExtensions(cn))
				stack.addAll(getImplementations(cn))
			}
		}

		applyRemap(remap)
	}
}