package dev.tigr.nebulous

import dev.tigr.nebulous.modifiers.IModifier
import dev.tigr.nebulous.modifiers.constants.number.NumberPooler
import dev.tigr.nebulous.modifiers.constants.string.StringPooler
import dev.tigr.nebulous.modifiers.constants.string.StringSplitter
import dev.tigr.nebulous.modifiers.misc.DebugInfoRemover
import dev.tigr.nebulous.modifiers.misc.FullAccessFlags
import dev.tigr.nebulous.modifiers.misc.MemberShuffler
import dev.tigr.nebulous.modifiers.optimizers.GotoInliner
import dev.tigr.nebulous.modifiers.optimizers.GotoReturnInliner
import dev.tigr.nebulous.modifiers.optimizers.LineNumberRemover
import dev.tigr.nebulous.modifiers.optimizers.NOPRemover
import dev.tigr.nebulous.modifiers.renamers.ClassRenamer
import dev.tigr.nebulous.modifiers.renamers.FieldRenamer
import dev.tigr.nebulous.modifiers.renamers.MethodRenamer
import dev.tigr.nebulous.util.*
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.jar.Manifest

/**
 * @author Tigermouthbear
 */
object Nebulous {
    private val input = StringConfig("input", true)
    private val output = StringConfig("output", true)
    private val exclusions = ArrayConfig("exclusions")
    private val libraries = ArrayConfig("libraries")

    private lateinit var ioMode: IOMode
    private val files: MutableMap<String, ByteArray> = HashMap()
    private val classNodes: MutableMap<String, ClassNode> = HashMap()
    private lateinit var manifest: Manifest

    fun run(config: File) {
        val startTime = System.currentTimeMillis()

        Config.read(config)

        // load jar
        val fileIn = File(input.value)
        if(!fileIn.exists()) {
            println("Input file not found!")
            return
        }
        openFile(fileIn)

        ClassPath.load(getLibraries())

        val modifiers: List<IModifier> =
                arrayListOf(
                        // strings
                        StringPooler,
                        //StringEncryptor,
                        StringSplitter,

                        // numbers
                        NumberPooler,

                        // names
                        //FieldRenamer,
                        //MethodRenamer,
                        //ClassRenamer,

                        // misc
                        //MemberShuffler,
                        //FullAccessFlags,
                        DebugInfoRemover,

                        // optimizers
                        NOPRemover,
                        LineNumberRemover,
                        GotoInliner,
                        GotoReturnInliner
                )

        modifiers.forEach { modifier ->
            val current = System.currentTimeMillis()
            modifier.modify()
            println(modifier.getName() + " completed in " + (System.currentTimeMillis() - current) + " milliseconds")
        }

        saveFile()

        println("\nNebulous finished in " + (System.currentTimeMillis() - startTime) + " milliseconds")
    }

    private fun openFile(file: File) {
        if(file.name.endsWith(".jar")) {
            ioMode = IOMode.JAR
            openJar(JarFile(file))
        }
        else if(file.name.endsWith(".class")) {
            ioMode = IOMode.CLASS
            openClass(file)
        }
    }

    private fun openJar(jar: JarFile) {
        val entries = jar.entries()

        while(entries.hasMoreElements()) {
            val entry = entries.nextElement()

            val bytes: ByteArray = Utils.readBytes(jar.getInputStream(entry))

            if(!entry.name.endsWith(".class")) {
                files[entry.name] = bytes
            } else {
                val c = ClassNode()
                ClassReader(bytes).accept(c, ClassReader.EXPAND_FRAMES)
                classNodes[c.name] = c
            }
        }

        // open manifest
        manifest = jar.manifest
    }

    private fun openClass(file: File) {
        val bytes: ByteArray = Utils.readBytes(file.inputStream())
        val c = ClassNode()
        ClassReader(bytes).accept(c, ClassReader.EXPAND_FRAMES)
        classNodes[c.name] = c
    }

    private fun saveFile() {
        when(ioMode) {
            IOMode.JAR -> saveJar()
            IOMode.CLASS -> saveClass()
        }
    }

    private fun saveJar() {
        // save manifest
        val mos = ByteArrayOutputStream()
        manifest.write(mos)
        files["META-INF/MANIFEST.MF"] = mos.toByteArray()

        var location: String = output.value!!
        if(!location.endsWith(".jar")) location += ".jar"

        val jarPath = Paths.get(location)
        Files.deleteIfExists(jarPath)

        val outJar = JarOutputStream(Files.newOutputStream(jarPath, StandardOpenOption.CREATE, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE))

        // write classes into jar
        classNodes.values.forEach { cn ->
            outJar.putNextEntry(JarEntry(cn.name + ".class"))
            outJar.write(Utils.writeBytes(cn))
            outJar.closeEntry()
        }

        // copy files into jar
        files.entries.forEach { (key, value) ->
            outJar.putNextEntry(JarEntry(key))
            outJar.write(value)
            outJar.closeEntry()
        }

        outJar.close()
    }

    private fun saveClass() {
        var location: String = output.value!!
        if(!location.endsWith(".class")) location += ".class"

        val classPath = Paths.get(location)
        Files.deleteIfExists(classPath)

        val outputStream = Files.newOutputStream(classPath, StandardOpenOption.CREATE, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE)
        outputStream.write(Utils.writeBytes(classNodes.entries.iterator().next().value))
        outputStream.close()
    }

    fun getClassNodes(): MutableMap<String, ClassNode> {
        return classNodes
    }

    fun getFiles(): MutableMap<String, ByteArray> {
        return files
    }

    fun getManifest(): Manifest {
        return manifest
    }

    fun getExclusions(): List<String> {
        val temp: MutableList<String> = mutableListOf()
        if(exclusions.value == null) return temp
        for(i in 0 until exclusions.value!!.length()) temp.add(exclusions.value!!.getString(i))
        return temp
    }

    fun getLibraries(): List<String> {
        val temp: MutableList<String> = mutableListOf()
        if(libraries.value == null) return temp
        for(i in 0 until libraries.value!!.length()) temp.add(libraries.value!!.getString(i))
        return temp
    }

    enum class IOMode {
        CLASS, JAR;
    }
}