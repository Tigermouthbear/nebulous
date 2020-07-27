package dev.tigr.nebulous.modifiers.misc

import dev.tigr.nebulous.modifiers.IModifier
import java.util.*

/**
 * @author Tigermouthbear
 * Shuffles the order of all nodes
 */
object MemberShuffler : IModifier {
    override fun modify() {
        classes.stream()
                .filter { cn -> !isExcluded(cn.name) }
                .forEach { cn ->
                    cn.apply {
                        shuffle(fields)
                        shuffle(methods)
                        shuffle(innerClasses)
                        shuffle(interfaces)
                        shuffle(attrs)
                        shuffle(invisibleAnnotations)
                        shuffle(visibleAnnotations)
                        shuffle(invisibleTypeAnnotations)
                        shuffle(visibleTypeAnnotations)
                        fields.forEach { fn ->
                            fn.apply {
                                shuffle(attrs)
                                shuffle(invisibleAnnotations)
                                shuffle(visibleAnnotations)
                                shuffle(invisibleTypeAnnotations)
                                shuffle(visibleTypeAnnotations)
                            }
                        }
                        methods.forEach { mn ->
                            mn.apply {
                                shuffle(attrs)
                                shuffle(invisibleAnnotations)
                                shuffle(visibleAnnotations)
                                shuffle(invisibleTypeAnnotations)
                                shuffle(visibleTypeAnnotations)
                                shuffle(exceptions)
                                shuffle(invisibleLocalVariableAnnotations)
                                shuffle(visibleLocalVariableAnnotations)
                                shuffle(localVariables)
                                shuffle(parameters)
                            }
                        }
                        innerClasses.clear()
                    }
                }
    }

    private fun shuffle(list: List<*>?) {
        if (list != null) Collections.shuffle(list)
    }


    override fun getName(): String {
        return "Member Shuffler"
    }
}