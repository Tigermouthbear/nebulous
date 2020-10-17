package dev.tigr.nebulous.util

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONTokener
import java.io.File
import java.io.FileInputStream
import java.util.*

/**
 * @author Tigermouthbear
 */
open class Config<T>(private val name: String, private val type: Type, private val read: (JSONObject) -> T, private val mandatory: Boolean) {
    var value: T? = null

    companion object {
        private val configs: MutableList<Config<*>> = ArrayList()

        fun read(file: File?) {
            val jsonObject = JSONObject(JSONTokener(FileInputStream(file)))
            configs.forEach {
                try {
                    it.read(jsonObject)
                } catch(e: JSONException) {
                    if(it.mandatory) e.printStackTrace()
                }
            }
        }
    }

    init {
        configs.add(this)
    }

    private fun read(jsonObject: JSONObject) {
        value = read.invoke(jsonObject)
    }

    enum class Type {
        ARRAY, STRING
    }
}

class ArrayConfig(name: String?, mandatory: Boolean = false): Config<JSONArray?>(name!!,Type.ARRAY, {
    it.getJSONArray(name)
}, mandatory)

class StringConfig(name: String?, mandatory: Boolean = false): Config<String?>(name!!, Type.STRING, {
    it.getString(name)
}, mandatory)

