package me.tigermouthbear.nebulous.util

import java.security.SecureRandom
import kotlin.math.floor
import kotlin.math.ln


/**
 * @author Tigermouthbear
 */

object Dictionary {
	private val RANDOM = SecureRandom()
	private val SETS = arrayOf(arrayOf("o", "c"), arrayOf("O", "C"), arrayOf("l", "i"))
	private val ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
	private val stringsUsed: MutableList<String> = mutableListOf()

	fun getNewName(): String {
		return getNextInAlphabet()
	}

	private fun genHardToSeeString(): String {
		val set = SETS[RANDOM.nextInt(SETS.size)]
		val length = RANDOM.nextInt(15 - 5) + 5
		val sb = StringBuilder()
		for(i in 0 until length) sb.append(set[RANDOM.nextInt(set.size)])
		//Makes sure that string hasn't been used, if so gens a new one
		var out = sb.toString()
		if(stringsUsed.contains(sb.toString())) out = genHardToSeeString()
		stringsUsed.add(out)
		return out
	}

	private var letter = -1
	private fun getNextInAlphabet(): String {
		letter++
		return getInAlphabet(letter).toLowerCase()
	}

	fun getInAlphabet(i: Int): String {
		return if(i < 0) "" else getInAlphabet(i / 26 - 1) + (65 + i % 26).toChar()
	}

	fun genRandomString(): String {
		val length = RANDOM.nextInt(15 - 5) + 5
		val sb = StringBuilder()
		for(i in 0 until length) {
			val letter = RANDOM.nextInt(ALPHABET.length)
			sb.append(ALPHABET[letter])
		}
		return sb.toString()
	}
}