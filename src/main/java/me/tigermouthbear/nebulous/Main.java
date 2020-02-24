package me.tigermouthbear.nebulous;

import me.tigermouthbear.nebulous.modifiers.*;

import java.io.File;
import java.io.IOException;

public class Main
{
	public static void main(String[] args) throws IOException
	{
		if(args.length != 1)
		{
			System.out.println("Parameters: <config file>");
			System.exit(-1);
		}

		Nebulous nebulous = new Nebulous(new File(args[0]));

		//Run modifiers
		//nebulous.apply(AllCapsModifier.class);
		nebulous.apply(FieldNameModifier.class);
		//nebulous.apply(MethodNameModifier.class);
		nebulous.apply(ClassNameModifier.class);

		nebulous.saveJar();
	}
}
