package me.tigermouthbear.nebulous;

import me.tigermouthbear.nebulous.modifiers.AllCapsModifier;
import me.tigermouthbear.nebulous.modifiers.ClassNameModifier;
import me.tigermouthbear.nebulous.modifiers.FieldNameModifier;
import me.tigermouthbear.nebulous.modifiers.MethodNameModifier;

import java.io.File;
import java.io.IOException;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		Nebulous nebulous = new Nebulous("C:\\Users\\bearw\\Desktop\\test\\build\\libs\\ares-2.0-release.jar", new File("C:\\Users\\bearw\\Desktop\\test\\build\\libs\\ARESconfig.json"));

		//Run modifiers
		//nebulous.apply(AllCapsModifier.class);
		nebulous.apply(FieldNameModifier.class);
		nebulous.apply(MethodNameModifier.class);
		nebulous.apply(ClassNameModifier.class);

		nebulous.saveJar("C:\\Users\\bearw\\Desktop\\test\\build\\libs\\out.jar");
	}
}
