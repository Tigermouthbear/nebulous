package dev.tigr.nebulous.modifiers.renamers

import dev.tigr.nebulous.modifiers.AbstractModifier
import dev.tigr.nebulous.util.ClassEntry
import dev.tigr.nebulous.util.ClassNodeEntry
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
object MethodRenamer: AbstractModifier("MethodRenamer") {
    private val blacklist = arrayListOf("main", "createUI")

    override fun modify() {
        val remap: MutableMap<String?, String?> = mutableMapOf()
        val methodMap: MutableMap<MethodNode, ClassEntry> = mutableMapOf()

        //TODO: Make this work with acc_annotation acc_enum and acc_abstract

        // gather all methods into map
        classes.stream()
                .filter { cn -> !isExcluded(cn.name) && cn.access and ACC_ANNOTATION == 0 && cn.access and ACC_ENUM == 0 && cn.access and ACC_ABSTRACT == 0 }
                .forEach { cn ->
                    cn.methods.stream()
                            .filter { mn -> !blacklist.contains(mn.name) && !mn.name.startsWith("<") && mn.access and ACC_NATIVE == 0 }
                            .forEach { mn -> methodMap[mn] = ClassPath.get(cn) }
                }

        // create obfuscated names
        methods@
        for((mn, owner) in methodMap.entries) {
            val stack = Stack<ClassEntry>()
            stack.add(owner)

            while(stack.isNotEmpty()) {
                val ce = stack.pop()

                // if not top level method continue
                if(ce != owner && ce.getMethods().findLast { method -> method.name == mn.name && method.desc == mn.desc } != null)
                    continue@methods

                // push super class
                val parent = if(ce.getSuper() == null) null else ClassPath[ce.getSuper()]
                if(parent != null) stack.push(parent)

                // push interfaces that it implements
                ce.getInterfaces().forEach { inter: String ->
                    val interfNode = ClassPath[inter]
                    if(interfNode != null) stack.push(interfNode)
                }
            }

            val name = Dictionary.getNewName()
            stack.add(owner)

            while(stack.isNotEmpty()) {
                val ce = stack.pop()
                remap[ce.getName() + "." + mn.name + mn.desc] = name

                //add classes which implement or extend the class node to the stack so that their fields get remapped
                stack.addAll(getExtensions(ce))
                stack.addAll(getImplementations(ce))
            }
        }

        applyRemap(remap)

        Dictionary.reset()
    }
}