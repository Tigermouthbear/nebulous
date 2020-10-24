package dev.tigr.nebulous.modifiers.renamers

import dev.tigr.nebulous.modifiers.AbstractModifier
import dev.tigr.nebulous.util.Dictionary
import dev.tigr.nebulous.util.StringConfig
import java.util.*

/**
 * @author Tigermouthbear
 * Renames all classes to use the current dictionary
 */
object ClassRenamer: AbstractModifier("ClassRenamer") {
    private val path = StringConfig("ClassRenamerPrefix")

    override fun modify() {
        val remap: MutableMap<String?, String?> = HashMap()

        val prefix = if(path.value == null) "" else path.value

        classes.stream()
                .filter { cn -> !isExcluded(cn.name) }
                .forEach { cn ->
                    val name = prefix + Dictionary.getNewName()
                    remap[cn.name] = name
                    if(cn.name.replace("/", ".") == manifest.mainAttributes.getValue("Main-Class"))
                        manifest.mainAttributes.putValue("Main-Class", name.replace("/", "."))
                }

        applyRemap(remap)

        Dictionary.reset()
    }
}