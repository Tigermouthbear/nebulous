package me.tigermouthbear.nebulous.util;

import java.util.ArrayList;
import java.util.Random;

public class RandomString
{
	private static final Random RANDOM = new Random();
	private static final int MAXLENGTH = 15;
	private static final int MINLENGTH = 5;

	private static final String[] OSET = new String[] {"o", "c", "O", "C"};
	private static final String[] LSET = new String[] {"l", "i"};
	private static final String[][] SETS = new String[][] {OSET, LSET};

	private static ArrayList<String> stringsUsed = new ArrayList<>();


	public static String genRandomString()
	{
		String[] set = SETS[RANDOM.nextInt(2)];

		int length = RANDOM.nextInt(MAXLENGTH - MINLENGTH) + MINLENGTH;

		StringBuilder sb = new StringBuilder();

		for(int i = 0; i < length; i++)
			sb.append(set[RANDOM.nextInt(set.length)]);

		//Makes sure that string hasn't been used, if so gens a new one
		String out = sb.toString();
		if(stringsUsed.contains(sb.toString())) out = genRandomString();
		stringsUsed.add(out);

		return out;
	}
}
