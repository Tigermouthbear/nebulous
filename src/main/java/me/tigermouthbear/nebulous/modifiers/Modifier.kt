package me.tigermouthbear.nebulous.modifiers

import me.tigermouthbear.nebulous.Nebulous
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.SimpleRemapper
import org.objectweb.asm.tree.ClassNode
import java.util.*
import java.util.stream.Collectors
import java.util.stream.Stream

abstract class Modifier {
    private var target: Nebulous? = null
    fun setTarget(target: Nebulous?): Modifier {
        this.target = target
        return this
    }

    abstract fun modify()
    protected fun applyRemap(remap: Map<String?, String?>?) {
        val remapper = SimpleRemapper(remap)
        for (node in ArrayList(classMap.values)) {
            val copy = ClassNode()
            val adapter = ClassRemapper(copy, remapper)
            node.accept(adapter)
            classMap.remove(node.name)
            classMap.put(node.name, copy)
        }
    }

    protected val classes: Stream<ClassNode>
        protected get() = classMap.values.stream().filter { classNode: ClassNode -> !isDependency(classNode.name) }

    protected fun getImplementations(classNode: ClassNode): List<ClassNode> {
        return classes.collect(Collectors.toList()).stream().filter { cn: ClassNode -> cn.interfaces.contains(classNode.name) }.collect(Collectors.toList())
    }

    protected fun isDependency(name: String): Boolean {
        val path = getPath(name)
        for (depencency in Nebulous.getDependencies()) {
            if (path.contains(depencency)) return true
        }
        return false
    }

    protected fun getPath(name: String): String {
        if (!name.contains("/")) return ""
        val reversedString = reverseString(name)
        val path = reversedString.substring(reversedString.indexOf("/"))
        return reverseString(path)
    }

    private fun reverseString(string: String): String {
        val sb = StringBuilder()
        val chars = string.toCharArray()
        for (i in chars.indices.reversed()) sb.append(chars[i])
        return sb.toString()
    }

    protected val classMap: MutableMap<String, ClassNode>
        get() = target!!.getClassNodes() as MutableMap<String, ClassNode>
}