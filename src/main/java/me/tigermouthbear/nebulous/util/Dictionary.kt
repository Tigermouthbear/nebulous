package me.tigermouthbear.nebulous.util

import java.security.SecureRandom
import java.util.*

/**
 * @author Tigermouthbear
 */

object Dictionary {
	private val RANDOM = SecureRandom()
	private const val MAXLENGTH = 15
	private const val MINLENGTH = 5
	private val CSET = arrayOf("o", "c")
	private val OSET = arrayOf("O", "C")
	private val LSET = arrayOf("l", "i")
	private val SETS = arrayOf(OSET, LSET, CSET)
	private val stringsUsed: MutableList<String> = mutableListOf()

	fun getNewName(): String {
		return genRandomString()
	}

	fun genRandomString(): String {
		val set = SETS[RANDOM.nextInt(SETS.size)]
		val length = RANDOM.nextInt(MAXLENGTH - MINLENGTH) + MINLENGTH
		val sb = StringBuilder()
		for(i in 0 until length) sb.append(set[RANDOM.nextInt(set.size)])
		//Makes sure that string hasn't been used, if so gens a new one
		var out = sb.toString()
		if(stringsUsed.contains(sb.toString())) out = genRandomString()
		stringsUsed.add(out)
		return out
	}
}