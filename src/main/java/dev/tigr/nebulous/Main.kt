package dev.tigr.nebulous

import java.io.File
import kotlin.system.exitProcess

/**
 * @author Tigermouthbear
 */
fun main(args: Array<String>) {
    if(args.size != 1) {
        println("Parameters: <config file>\nSee https://github.com/Tigermouthbear/nebulous/blob/master/README.md for help")
        exitProcess(-1)
    }

    println("\n" +
            " _   _      _           _                 \n" +
            "| \\ | |    | |         | |                \n" +
            "|  \\| | ___| |__  _   _| | ___  _   _ ___ \n" +
            "| . ` |/ _ \\ '_ \\| | | | |/ _ \\| | | / __|\n" +
            "| |\\  |  __/ |_) | |_| | | (_) | |_| \\__ \\\n" +
            "\\_| \\_/\\___|_.__/ \\__,_|_|\\___/ \\__,_|___/\n" +
            "v0.1")
    println("Created by Tigermouthbear\n")

    Nebulous.run(File(args[0]))
}