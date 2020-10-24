package dev.tigr.nebulous.util

import java.security.SecureRandom


/**
 * @author Tigermouthbear
 * Name generator for obfuscation
 */
object Dictionary {
    private val RANDOM = SecureRandom()
    private val ALPHABET = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

    private var num = 0

    fun getNewName(): String {
        return getInAlphabet(num++)
    }

    fun reset() {
        num = 0;
    }

    private fun getInAlphabet(i: Int): String {
        return if(i < 0) "" else getInAlphabet(i/52 - 1) + (toAscii(i % 52)).toChar()
    }

    private fun toAscii(i: Int): Int {
        return if(i < 26) i + 97 else i + 39
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