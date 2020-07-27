package dev.tigr.nebulous.util

import java.security.SecureRandom


/**
 * @author Tigermouthbear
 * Name generator for obfuscation
 */
object Dictionary {
    private val RANDOM = SecureRandom()
    private val HARDTOSEE = "lI"
    private val ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
    private val INVIS = "\u200b\u200d\u200c\u200e\u200f"
    private val UNICODE = "\u9b89\u7e11\u1c58\u665c\u9aab\u7080\u5dbe\u989d\u8ad5\u81dd\u7b99\u4d46\ubf9b\u817b\u36de\uf9d5\u8d2d\u888b\u3fe6\u585a\u84fb\ub2c4\u7b99\u4d46\u7c5f\ua349\u9479\u0027\u71e1\u0268\u6208\u1b36\u8545\ud5ff\u004e\ub47f\u7660"
    private val stringsUsed: MutableList<String> = mutableListOf()

    fun getNewName(): String {
        return getNextInAlphabet()
        //return getNextInAlphabet()
    }

    private fun genWithDict(dict: String): String {
        val length = RANDOM.nextInt(10 - 5) + 5
        val sb = StringBuilder()
        for (i in 0 until length) sb.append(dict[RANDOM.nextInt(dict.length)])
        //Makes sure that string hasn't been used, if so gens a new one
        var out = sb.toString()
        if (stringsUsed.contains(sb.toString())) out = genWithDict(dict)
        stringsUsed.add(out)
        return out
    }

    private var letter = -1
    private fun getNextInAlphabet(): String {
        letter++
        return getInAlphabet(letter).toLowerCase()
    }

    fun getInAlphabet(i: Int): String {
        return if (i < 0) "" else getInAlphabet(i / 26 - 1) + (65 + i % 26).toChar()
    }

    fun genRandomString(): String {
        val length = RANDOM.nextInt(15 - 5) + 5
        val sb = StringBuilder()
        for (i in 0 until length) {
            val letter = RANDOM.nextInt(ALPHABET.length)
            sb.append(ALPHABET[letter])
        }
        return sb.toString()
    }
}