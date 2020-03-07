package me.tigermouthbear.nebulous.modifiers

import me.tigermouthbear.nebulous.util.RandomString
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.FieldNode
import java.util.*
import java.util.function.Consumer
import java.util.stream.Collectors

class FieldNameModifier : Modifier() {
    override fun modify() {
        val remap: MutableMap<String?, String?> = HashMap()
        //add a list of field nodes with their corresponding class nodes
        val fieldMap: MutableMap<FieldNode, ClassNode> = HashMap()
        classes.forEach { classNode: ClassNode -> classNode.fields.forEach(Consumer { fieldNode: FieldNode -> fieldMap[fieldNode] = classNode }) }
        for (fieldNode in fieldMap.keys) {
            val fieldOwner = fieldMap[fieldNode]
            val name = RandomString.genRandomString()
            val stack = Stack<ClassNode?>()
            stack.add(fieldOwner)
            while (!stack.empty()) { //remap field name in stack
                val classNode = stack.pop()
                remap[classNode!!.name + "." + fieldNode.name] = name
                //add classes which implement or extend the class node to the stack so that their fields get remapped
                stack.addAll(classes.filter { supers: ClassNode -> supers.superName == classNode.name }.collect(Collectors.toList()))
                stack.addAll(getImplementations(classNode))
            }
        }
        applyRemap(remap)
    }
}