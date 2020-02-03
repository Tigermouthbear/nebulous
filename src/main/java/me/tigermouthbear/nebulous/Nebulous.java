package me.tigermouthbear.nebulous;

import me.tigermouthbear.nebulous.config.ArrayConfig;
import me.tigermouthbear.nebulous.config.ConfigReader;
import me.tigermouthbear.nebulous.config.StringConfig;
import me.tigermouthbear.nebulous.modifiers.Modifier;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;

public class Nebulous
{
	private static StringConfig input = new StringConfig("input");
	private static StringConfig output = new StringConfig("output");
	private static ArrayConfig dependencies = new ArrayConfig("dependencies");

	private Map<String, byte[]> files = new HashMap<>();
	private Map<String, ClassNode> classNodes = new HashMap<>();

	public Nebulous(File config)
	{
		ConfigReader.read(config);

		try
		{
			setJar(new JarFile(input.getValue()));
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

	public void saveJar() throws IOException
	{
		String loc = output.getValue();

		if(!loc.endsWith(".jar")) loc += ".jar";

		Path jarPath = Paths.get(loc);
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

	public List<String> getDependencies()
	{
		List<String> temp = new ArrayList<>();
		for(int i = 0; i < dependencies.getValue().length(); i++)
			temp.add(dependencies.getValue().getString(i));
		return temp;
	}
}
