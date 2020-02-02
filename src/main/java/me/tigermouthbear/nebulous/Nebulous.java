package me.tigermouthbear.nebulous;

import me.tigermouthbear.nebulous.modifiers.Modifier;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.*;
import java.net.URI;
import java.nio.file.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class Nebulous
{
	private Map<String, byte[]> files = new HashMap<>();
	private Map<String, ClassNode> classNodes = new HashMap<>();

	public Nebulous(String file)
	{
		try
		{
			setJar(new JarFile(file));
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	public void apply(Class<? extends Modifier> modifier)
	{
		try
		{
			modifier.newInstance().setTarget(this).modify();
		}
		catch(InstantiationException | IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	private void setJar(JarFile jar) {
		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			try (InputStream in = jar.getInputStream(entry)) {
				byte[] bytes;
				try (ByteArrayOutputStream tmp = new ByteArrayOutputStream()) {
					byte[] buf = new byte[256];
					for (int n; (n = in.read(buf)) != -1; ) {
						tmp.write(buf, 0, n);
					}
					bytes = tmp.toByteArray();
				}
				if (!entry.getName().endsWith(".class")) {
					files.put(entry.getName(), bytes);
					continue;
				}
				ClassNode c = new ClassNode();
				new ClassReader(bytes).accept(c, ClassReader.EXPAND_FRAMES);
				classNodes.put(c.name, c);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void saveJar(String jarLocation) throws IOException
	{
		if(!jarLocation.endsWith(".jar")) jarLocation += ".jar";

		Path jarPath = Paths.get(jarLocation);
		Files.deleteIfExists(jarPath);
		JarOutputStream outJar = new JarOutputStream(Files.newOutputStream(jarPath, new StandardOpenOption[] {StandardOpenOption.CREATE, StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE}));

		//Write classes into obf jar
		for(ClassNode node: getClassNodes().values())
		{
			JarEntry entry = new JarEntry(node.name + ".class");
			outJar.putNextEntry(entry);
			ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			node.accept(writer);
			outJar.write(writer.toByteArray());
			outJar.closeEntry();
		}

		//Copy files from previous jar into obf jar
		for(Map.Entry<String, byte[]> entry: getFiles().entrySet())
		{
			outJar.putNextEntry(new JarEntry(entry.getKey()));
			outJar.write(entry.getValue());
			outJar.closeEntry();
		}

		outJar.close();
	}

	public Map<String, byte[]> getFiles()
	{
		return files;
	}
	public Map<String, ClassNode> getClassNodes()
	{
		return classNodes;
	}
}
