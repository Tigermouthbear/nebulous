package me.tigermouthbear.nebulous

import me.tigermouthbear.nebulous.config.ArrayConfig
import me.tigermouthbear.nebulous.config.ConfigReader
import me.tigermouthbear.nebulous.config.StringConfig
import me.tigermouthbear.nebulous.modifiers.ClassNameModifier
import me.tigermouthbear.nebulous.modifiers.FieldNameModifier
import me.tigermouthbear.nebulous.modifiers.Modifier
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.tree.ClassNode
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream


object Nebulous {
    private val files: MutableMap<String, ByteArray> = HashMap()
    private val classNodes: MutableMap<String, ClassNode> = HashMap()

    fun run(file: File) {

        ConfigReader.read(file)

        setJar(JarFile(input.value))

        //Run modifiers
        //nebulous.apply(AllCapsModifier.class);
        //Run modifiers
        //nebulous.apply(AllCapsModifier.class);
        apply(FieldNameModifier::class.java)
        //nebulous.apply(MethodNameModifier.class);
        //nebulous.apply(MethodNameModifier.class);
        apply(ClassNameModifier::class.java)

        saveJar()
    }

    fun apply(modifier: Class<out Modifier?>) {
        try {
            modifier.newInstance()!!.setTarget(this).modify()
        } catch (e: InstantiationException) {
            e.printStackTrace()
        } catch (e: IllegalAccessException) {
            e.printStackTrace()
        }
    }

    private fun setJar(jar: JarFile) {
        val entries = jar.entries()
        while (entries.hasMoreElements()) {
            val entry = entries.nextElement()
            try {
                jar.getInputStream(entry).use { `in` ->
                    val bytes: ByteArray
                    val baos = ByteArrayOutputStream()
                    val buf = ByteArray(256)
                    var n: Int
                    while (`in`.read(buf).also { n = it } != -1) {
                        baos.write(buf, 0, n)
                    }
                    bytes = baos.toByteArray()

                    if (!entry.name.endsWith(".class")) {
                        files[entry.name] = bytes
                    } else {
                        val c = ClassNode()
                        ClassReader(bytes).accept(c, ClassReader.EXPAND_FRAMES)
                        classNodes.put(c.name, c)
                    }

                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun saveJar() {
        var loc = output.value
        if (!loc.endsWith(".jar")) loc += ".jar"
        val jarPath = Paths.get(loc)
        Files.deleteIfExists(jarPath)
        val outJar = JarOutputStream(Files.newOutputStream(jarPath, *arrayOf(StandardOpenOption.CREATE, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)))
        //Write classes into obf jar
        for (node in getClassNodes().values) {
            val entry = JarEntry(node.name + ".class")
            outJar.putNextEntry(entry)
            val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
            node.accept(writer)
            outJar.write(writer.toByteArray())
            outJar.closeEntry()
        }
        //Copy files from previous jar into obf jar
        for ((key, value) in getFiles()) {
            outJar.putNextEntry(JarEntry(key))
            outJar.write(value)
            outJar.closeEntry()
        }
        outJar.close()
    }

    fun getFiles(): Map<String, ByteArray> {
        return files
    }

    fun getClassNodes(): Map<String, ClassNode> {
        return classNodes
    }

    fun getDependencies(): List<String> {
        val temp: MutableList<String> = ArrayList()
        for (i in 0 until dependencies.value!!.length()) temp.add(dependencies.value!!.getString(i))
        return temp
    }

    private val input = StringConfig("input")
    private val output = StringConfig("output")
    private val dependencies = ArrayConfig("dependencies")
}