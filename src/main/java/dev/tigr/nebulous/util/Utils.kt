package dev.tigr.nebulous.util

import dev.tigr.nebulous.Nebulous
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import org.objectweb.asm.tree.MethodNode
import java.io.ByteArrayOutputStream
import java.io.InputStream

/**
 * @author Tigermouthbear
 * General Utils
 */
interface Utils {
    companion object: Utils {
    }

    fun isExcluded(name: String): Boolean {
        if(name == "me/tigermouthbear/ares/Ares") return true

        val path = getPath(name)
        for(exclusion in Nebulous.getExclusions()) {
            if(path.contains(exclusion)) return true
        }
        return false
    }

    fun getPath(name: String): String {
        if(!name.contains("/")) return ""
        val reversedString = name.reversed()
        val path = reversedString.substring(reversedString.indexOf("/"))
        return path.reversed()
    }

    fun getMethod(cn: ClassNode, method: String): MethodNode? {
        cn.methods.forEach { mn ->
            if(mn.name == method) return mn
        }

        return null
    }

    fun readBytes(inputStream: InputStream): ByteArray {
        val bytes: ByteArray
        val baos = ByteArrayOutputStream()
        val buf = ByteArray(256)
        var n: Int
        while(inputStream.read(buf).also { n = it } != -1) baos.write(buf, 0, n)
        bytes = baos.toByteArray()

        return bytes
    }

    fun writeBytes(classNode: ClassNode): ByteArray {
        val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
        writer.newUTF8("Secured by Nebulous")
        classNode.accept(writer)
        return writer.toByteArray()
    }
}