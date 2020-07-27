package dev.tigr.nebulous.modifiers.renamers

import dev.tigr.nebulous.modifiers.IModifier
import dev.tigr.nebulous.util.ClassPath
import dev.tigr.nebulous.util.Dictionary
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import java.util.*

/**
 * @author Tigermouthbear
 * Renames all methods to use the current dictionary
 */
object MethodRenamer : IModifier {
    private val blacklist = arrayListOf("main", "createUI")

    override fun modify() {
        val remap: MutableMap<String?, String?> = mutableMapOf()
        val methodMap: MutableMap<MethodNode, ClassNode> = mutableMapOf()

        // gather all methods into map
        classes.stream()
                .filter { cn -> !isExcluded(cn.name) && cn.access and ACC_ANNOTATION == 0 && cn.access and ACC_ENUM == 0 }
                .forEach { cn ->
                    cn.methods.stream()
                            .filter { mn -> !blacklist.contains(mn.name) && !mn.name.startsWith("<") && mn.access and ACC_NATIVE == 0 }
                            .forEach { mn -> methodMap[mn] = cn }
                }

        // create obfuscated names
        methods@
        for ((mn, owner) in methodMap.entries) {
            val stack = Stack<ClassNode?>()
            stack.add(owner)

            while (stack.isNotEmpty()) {
                val cn = stack.pop()

                // if not top level method continue
                if (cn != owner && cn!!.methods.findLast { method -> method.name == mn.name && method.desc == mn.desc } != null)
                    continue@methods

                // push super class
                val parent = getClassNode(cn.superName)
                if (parent != null) stack.push(parent)

                // push interfaces that it implements
                cn.interfaces.forEach { inter ->
                    val interfNode = getClassNode(inter)
					if (interfNode != null) stack.push(interfNode)
                }
            }

            val name = Dictionary.getNewName()
            stack.add(owner)

            while (stack.isNotEmpty()) {
                val cn = stack.pop()
                remap[cn!!.name + "." + mn.name + mn.desc] = name

                //add classes which implement or extend the class node to the stack so that their fields get remapped
                stack.addAll(getExtensions(cn))
                stack.addAll(getImplementations(cn))
            }
        }

        applyRemap(remap)
    }

    private fun getClassNode(name: String?): ClassNode? {
        if (name == null) return null
        val n = classMap[name]
        return n ?: ClassPath[name]
    }

    override fun getName(): String {
        return "Method Renamer"
    }
}