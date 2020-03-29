package me.tigermouthbear.nebulous.modifiers

import me.tigermouthbear.nebulous.util.RandomString
import org.objectweb.asm.tree.ClassNode
import java.util.*

class ClassNameModifier : Modifier() {
    override fun modify() {
        val remap: MutableMap<String?, String?> = HashMap()
        classes.forEach { classNode: ClassNode -> remap[classNode.name] = RandomString.genRandomString() }
        applyRemap(remap)
    }
}