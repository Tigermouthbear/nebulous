package dev.tigr.nebulous.modifiers.renamers

import dev.tigr.nebulous.modifiers.IModifier
import dev.tigr.nebulous.util.Dictionary
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import java.util.*

/**
 * @author Tigermouthbear
 * Renames all fields to use the current dictionary
 */
object FieldRenamer : IModifier {
    override fun modify() {
        val remap: MutableMap<String?, String?> = mutableMapOf()
        val fieldMap: MutableMap<FieldNode, ClassNode> = mutableMapOf()

        classes.stream()
                .filter { cn -> !isExcluded(cn.name) }
                .forEach { cn ->
                    cn.fields
                            .forEach { fn -> fieldMap[fn] = cn }
                }

        // create obfuscated names
        for ((fn, owner) in fieldMap.entries) {
            val name = Dictionary.getNewName()

            val stack = Stack<ClassNode?>()
            stack.add(owner)

            while (!stack.empty()) {
                val cn = stack.pop()
                remap[cn!!.name + "." + fn.name] = name

                //add classes which implement or extend the class node to the stack so that their fields get remapped
                stack.addAll(getExtensions(cn))
                stack.addAll(getImplementations(cn))
            }
        }

        applyRemap(remap)
    }

    override fun getName(): String {
        return "Field Renamer"
    }
}