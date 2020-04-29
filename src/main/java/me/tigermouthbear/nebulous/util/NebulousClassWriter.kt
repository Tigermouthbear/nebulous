package me.tigermouthbear.nebulous.util

import org.objectweb.asm.ClassWriter

class NebulousClassWriter(access: Int): ClassWriter(access) {
	init {
		this.newUTF8("Secured by Nebulous")
	}
}