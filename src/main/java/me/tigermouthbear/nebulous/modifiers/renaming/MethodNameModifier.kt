package me.tigermouthbear.nebulous.modifiers.renaming

import me.tigermouthbear.nebulous.modifiers.Modifier
import org.objectweb.asm.tree.AnnotationNode
import org.objectweb.asm.tree.ClassNode
import java.util.function.Consumer

/**
 * @author Tigermouthbear
 */

class MethodNameModifier: Modifier() {
	override fun modify() {
		/*val remap: MutableMap<String?, String?> = HashMap()
		/*getClassMap().values().stream().filter(classNode ->
				!isDependency(classNode.name)).forEach(classNode ->
				classNode.methods.stream().filter(methodNode -> !methodNode.name.startsWith("<") && (methodNode.access & Opcodes.ACC_NATIVE) == 0 && !methodNode.name.equals("main")).forEach(methodNode ->
						remap.put(classNode.name + "." + methodNode.name + methodNode.desc, RandomString.genRandomString())));*/
		val superclasses: MutableMap<ClassNode, String> = HashMap()
		classes.filter { classNode: ClassNode -> !classMap.containsKey(classNode.superName) && classNode.access and Opcodes.ACC_INTERFACE == 0 }.collect(Collectors.toList()).forEach(Consumer { classNode: ClassNode -> superclasses[classNode] = classNode.name })
		val annotations: MutableMap<ClassNode, List<String>> = HashMap()
		classes.filter { classNode: ClassNode -> classNode.visibleAnnotations != null }.forEach { classNode: ClassNode -> annotations[classNode] = getAnnotations(classNode) }
		for(classNode in superclasses.keys) {
			val methods = classNode.methods.stream().filter { methodNode: MethodNode -> !methodNode.name.startsWith("<") && methodNode.access and Opcodes.ACC_NATIVE == 0 && methodNode.name != "main" }.collect(Collectors.toList())
			if(classNode.visibleAnnotations != null) classNode.visibleAnnotations.forEach(Consumer { an: AnnotationNode -> println(an.desc) })
			for(methodNode in methods) {
				val name = Dictionary.genRandomString()
				val stack = Stack<ClassNode>()
				stack.add(classNode)
				while(!stack.isEmpty()) {
					val node = stack.pop()
					//System.out.println(node.name + "-----" + methodNode.name + "-----" + name);
					remap[node.name + "." + methodNode.name + methodNode.desc] = name
					if(annotations.containsKey(node)) annotations[node]!!.forEach(Consumer { n: String -> remap[node.name + "." + n] = n })
					stack.addAll(classes.filter { sub: ClassNode -> sub.superName == node.name }.collect(Collectors.toList()))
					stack.addAll(getImplementations(node))
				}
			}
		}
		applyRemap(remap)*/
	}

	private fun getAnnotations(classNode: ClassNode): List<String> {
		val s: MutableList<String> = mutableListOf()
		classNode.visibleAnnotations.forEach(Consumer { a: AnnotationNode -> s.add(a.desc + a.values) })
		return s
	}
}