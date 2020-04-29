package me.tigermouthbear.nebulous.modifiers.misc

import me.tigermouthbear.nebulous.modifiers.IModifier
import org.objectweb.asm.Opcodes.*

/**
 * @author Tigermouthbear
 * Sets all nodes to have full access flags
 */

class FullAccessFlags: IModifier {
	override fun modify() {
		classes.stream()
		.filter { cn -> !isExcluded(cn.name) }
		.forEach {cn ->
			cn.apply {
				access = access(access)
				methods.forEach { mn -> mn.access = access(mn.access) }
				fields.forEach { fn -> fn.access = access(fn.access) }
			}
		}
	}

	private fun access(access: Int): Int {
		var a: Int = ACC_PUBLIC
		if(access and ACC_NATIVE !== 0) a = a or ACC_NATIVE
		if(access and ACC_ABSTRACT !== 0) a = a or ACC_ABSTRACT
		if(access and ACC_ANNOTATION !== 0) a = a or ACC_ANNOTATION
		if(access and ACC_BRIDGE !== 0) a = a or ACC_BRIDGE
		if(access and ACC_ENUM !== 0) a = a or ACC_ENUM
		if(access and ACC_FINAL !== 0) a = a or ACC_FINAL
		if(access and ACC_INTERFACE !== 0) a = a or ACC_INTERFACE
		if(access and ACC_MANDATED !== 0) a = a or ACC_MANDATED
		if(access and ACC_MODULE !== 0) a = a or ACC_MODULE
		if(access and ACC_OPEN !== 0) a = a or ACC_OPEN
		if(access and ACC_STATIC !== 0) a = a or ACC_STATIC
		if(access and ACC_STATIC_PHASE !== 0) a = a or ACC_STATIC_PHASE
		if(access and ACC_STRICT !== 0) a = a or ACC_STRICT
		if(access and ACC_SUPER !== 0) a = a or ACC_SUPER
		if(access and ACC_SYNCHRONIZED !== 0) a = a or ACC_SYNCHRONIZED
		if(access and ACC_SYNTHETIC !== 0) a = a or ACC_SYNTHETIC
		if(access and ACC_TRANSIENT !== 0) a = a or ACC_TRANSIENT
		if(access and ACC_TRANSITIVE !== 0) a = a or ACC_TRANSITIVE
		if(access and ACC_VARARGS !== 0) a = a or ACC_VARARGS
		if(access and ACC_VOLATILE !== 0) a = a or ACC_VOLATILE
		return a
	}

	override fun getName(): String {
		return "Full Access Flags"
	}
}