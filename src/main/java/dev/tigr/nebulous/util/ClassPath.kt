package dev.tigr.nebulous.util

import jdk.internal.org.objectweb.asm.Type
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import java.util.jar.JarFile

/**
 * @author Tigermouthbear
 * Holds class nodes of external nodes for use with obfuscation
 */
object ClassPath: HashMap<String, ClassEntry>() {
    override fun get(key: String): ClassEntry? = super.get(key) ?: add(get(Class.forName(key.replace("/", "."))))
    fun get(classNode: ClassNode): ClassEntry = super.get(classNode.name) ?: add(ClassNodeEntry(classNode))
    fun get(clazz: Class<*>): ClassEntry = super.get(Type.getInternalName(clazz)) ?: add(ReflectionClassEntry(clazz))
    private fun add(classEntry: ClassEntry): ClassEntry = classEntry.apply { put(classEntry.getName(), classEntry) }

    fun load(paths: List<String>) = paths.forEach { load(it)}
    fun load(path: String) {
        val jar = JarFile(path)
        val entries = jar.entries()

        while(entries.hasMoreElements()) {
            val entry = entries.nextElement()

            if(entry.name.endsWith(".class")) {
                val bytes: ByteArray = Utils.readBytes(jar.getInputStream(entry))
                val c = ClassNode()
                ClassReader(bytes).accept(c, ClassReader.SKIP_CODE or ClassReader.SKIP_DEBUG)
                put(c.name, ClassNodeEntry(c))
            }
        }
    }
}

abstract class ClassEntry {
    abstract fun getName(): String
    abstract fun getSuper(): String?
    abstract fun getAccess(): Int
    abstract fun getInterfaces(): MutableList<String>
    abstract fun getFields(): MutableList<FieldEntry>
    abstract fun getMethods(): MutableList<MethodEntry>
}

data class MethodEntry(val owner: ClassEntry, val name: String, val desc: String)
data class FieldEntry(val owner: ClassEntry, val name: String, val desc: String)

class ClassNodeEntry(private val classNode: ClassNode): ClassEntry() {
    override fun getName(): String = classNode.name
    override fun getSuper(): String? = classNode.superName
    override fun getAccess(): Int = classNode.access
    override fun getInterfaces(): MutableList<String> = classNode.interfaces

    override fun getFields(): MutableList<FieldEntry> = fieldsImpl
    private val fieldsImpl: MutableList<FieldEntry> by lazy {
        val list = mutableListOf<FieldEntry>()
        for(fn in classNode.fields) {
            list.add(FieldEntry(this, fn.name, fn.desc))
        }
        list
    }

    override fun getMethods(): MutableList<MethodEntry> = methodsImpl
    private val methodsImpl: MutableList<MethodEntry> by lazy {
        val list = mutableListOf<MethodEntry>()
        for(mn in classNode.methods) {
            list.add(MethodEntry(this, mn.name, mn.desc))
        }
        list
    }
}

class ReflectionClassEntry(private val clazz: Class<*>): ClassEntry() {
    override fun getName(): String = Type.getInternalName(clazz)
    override fun getSuper(): String? = if(clazz.superclass == null) null else Type.getInternalName(clazz.superclass) ?: if(getName() != "java/lang/Object") "java/lang/Object" else null
    override fun getAccess(): Int = clazz.modifiers

    override fun getInterfaces(): MutableList<String> = interfacesImpl
    private val interfacesImpl: MutableList<String> by lazy {
        val list = mutableListOf<String>()
        for(interf in clazz.interfaces) {
            list.add(Type.getInternalName(interf))
        }
        list
    }

    override fun getFields(): MutableList<FieldEntry> = fieldsImpl
    private val fieldsImpl: MutableList<FieldEntry> by lazy {
        val list = mutableListOf<FieldEntry>()
        for(field in clazz.declaredFields) {
            list.add(FieldEntry(this, field.name, Type.getDescriptor(field.type)))
        }
        list
    }

    override fun getMethods(): MutableList<MethodEntry> = methodsImpl
    private val methodsImpl: MutableList<MethodEntry> by lazy {
        val list = mutableListOf<MethodEntry>()
        for(method in clazz.declaredMethods) {
            list.add(MethodEntry(this, method.name, Type.getMethodDescriptor(method)))
        }
        list
    }
}