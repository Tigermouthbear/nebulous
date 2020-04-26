package me.tigermouthbear.nebulous.modifiers.renaming

import me.tigermouthbear.nebulous.modifiers.Modifier
import me.tigermouthbear.nebulous.util.Dictionary
import java.io.ByteArrayInputStream
import java.io.FileInputStream
import java.io.InputStream
import java.util.*
import java.util.jar.Manifest

/**
 * @author Tigermouthbear
 */

class ClassNameModifier: Modifier() {
	override fun modify() {
		val remap: MutableMap<String?, String?> = HashMap()

		classes.stream()
		.filter { cn -> !isDependency(cn.name) }
		.forEach { cn ->
			val name = Dictionary.getNewName()
			remap[cn.name] = name
			if(cn.name.replace("/", ".") == manifest.mainAttributes.getValue("Main-Class"))
				manifest.mainAttributes.putValue("Main-Class", name.replace("/", "."))
		}

		applyRemap(remap)
	}
}