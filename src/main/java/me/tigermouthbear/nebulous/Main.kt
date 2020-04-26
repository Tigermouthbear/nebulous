package me.tigermouthbear.nebulous

import java.io.File
import kotlin.system.exitProcess

/**
 * @author Tigermouthbear
 */

fun main(args: Array<String>) {
	if(args.size != 1) {
		println("Parameters: <config file>")
		exitProcess(-1)
	}

	Nebulous.run(File(args[0]))
}