package dev.tigr.nebulous.modifiers.renamers

import dev.tigr.nebulous.modifiers.AbstractModifier
import dev.tigr.nebulous.util.ClassEntry
import dev.tigr.nebulous.util.ClassPath
import dev.tigr.nebulous.util.Dictionary
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import java.util.*

/**
 * @author Tigermouthbear
 * Renames all fields to use the current dictionary
 */
object FieldRenamer: AbstractModifier("FieldRenamer") {
    override fun modify() {
        val remap: MutableMap<String?, String?> = mutableMapOf()
        val fieldMap: MutableMap<FieldNode, ClassEntry> = mutableMapOf()

        classes.stream()
                .filter { cn -> !isExcluded(cn.name) }
                .forEach { cn ->
                    cn.fields
                            .forEach { fn -> fieldMap[fn] = ClassPath.get(cn) }
                }

        // create obfuscated names
        for((fn, owner) in fieldMap.entries) {
            val name = Dictionary.getNewName()

            val stack = Stack<ClassEntry>()
            stack.add(owner)

            while(!stack.empty()) {
                val ce = stack.pop()
                remap[ce.getName() + "." + fn.name] = name

                //add classes which implement or extend the class node to the stack so that their fields get remapped
                stack.addAll(getExtensions(ce))
                stack.addAll(getImplementations(ce))
            }
        }

        applyRemap(remap)

        Dictionary.reset()
    }
}