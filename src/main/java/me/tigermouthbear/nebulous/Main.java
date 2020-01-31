package me.tigermouthbear.nebulous;

import me.tigermouthbear.nebulous.modifiers.AllCapsModifier;

import java.io.IOException;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		Nebulous nebulous = new Nebulous("C:\\Users\\bearw\\Desktop\\test\\build\\libs\\test-1.0-SNAPSHOT.jar");

		//Run modifiers
		(new AllCapsModifier()).modify(nebulous);

		nebulous.saveJar("C:\\Users\\bearw\\Desktop\\test\\build\\libs\\out.jar");
	}
}
