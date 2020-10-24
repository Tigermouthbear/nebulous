package dev.tigr.nebulous.modifiers

import dev.tigr.nebulous.Nebulous
import dev.tigr.nebulous.util.*
import org.objectweb.asm.commons.ClassRemapper
import org.objectweb.asm.commons.SimpleRemapper
import org.objectweb.asm.tree.ClassNode
import java.util.jar.Manifest
import java.util.stream.Collectors

/**
 * @author Tigermouthbear
 */
abstract class AbstractModifier(val name: String): Utils, NodeUtils {
    private val setting = BooleanConfig(name)

    val classMap: MutableMap<String, ClassNode>
        get() = Nebulous.getClassNodes()

    val classes: List<ClassNode>
        get() = ArrayList(classMap.values)

    val filesMap: MutableMap<String, ByteArray>
        get() = Nebulous.getFiles()

    val files: List<String>
        get() = ArrayList(filesMap.keys)

    val manifest: Manifest
        get() = Nebulous.getManifest()

    fun run() {
        if(setting.value != null && setting.value!!) {
            val current = System.currentTimeMillis()
            modify()
            println(name + " completed in " + (System.currentTimeMillis() - current) + " milliseconds")
        }
    }

    protected abstract fun modify()

    fun applyRemap(remap: Map<String?, String?>?) {
        val remapper = SimpleRemapper(remap)
        for(node in classes) {
            val copy = ClassNode()
            val adapter = ClassRemapper(copy, remapper)
            node.accept(adapter)
            classMap.remove(node.name)
            classMap[node.name] = copy
        }
    }

    fun getImplementations(target: ClassNode): List<ClassNode> {
        return classes.stream().filter { cn -> cn.interfaces.contains(target.name) }.collect(Collectors.toList())
    }

    fun getExtensions(target: ClassNode): List<ClassNode> {
        val extensions: MutableList<ClassNode> = mutableListOf()

        classes.stream()
                .filter { cn -> cn.superName == target.name }
                .forEach { cn ->
                    extensions.add(cn)
                    extensions.addAll(getExtensions(cn))
                }

        return extensions
    }

    fun getImplementations(target: ClassEntry): List<ClassEntry> {
        val implementations = mutableListOf<ClassEntry>()
        for(cn in classes.stream().filter { cn -> cn.interfaces.contains(target.getName()) }) {
            implementations.add(ClassPath.get(cn))
            implementations.addAll(getImplementations(ClassPath.get(cn)))
        }
        return implementations
    }

    fun getExtensions(target: ClassEntry): List<ClassEntry> {
        val extensions = mutableListOf<ClassEntry>()
        classes.stream()
                .filter { cn -> cn.superName == target.getName() }
                .forEach { cn ->
                    extensions.add(ClassPath.get(cn))
                    extensions.addAll(getExtensions(ClassPath.get(cn)))
        }
        return extensions
    }
}