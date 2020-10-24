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
open class Config<T>(private val name: String, private val type: Type, private val read: (JSONObject) -> T) {
    var value: T? = null

    companion object {
        private lateinit var READ_OBJECT: JSONObject
        private val TO_READ: ArrayList<Config<*>> = arrayListOf()
        private val IS_INITIALIZED: Boolean
            get() = ::READ_OBJECT.isInitialized

        fun read(file: File?) {
            READ_OBJECT = JSONObject(JSONTokener(FileInputStream(file)))
            TO_READ.forEach { it.read(READ_OBJECT) }
        }
    }

    init {
        if(IS_INITIALIZED) read(READ_OBJECT)
        else TO_READ.add(this)
    }

    private fun read(jsonObject: JSONObject) {
        value = if(jsonObject.has(name)) read.invoke(jsonObject) else null
    }

    enum class Type {
        ARRAY, STRING, BOOLEAN
    }
}

class ArrayConfig(name: String?): Config<JSONArray?>(name!!,Type.ARRAY, {
    it.getJSONArray(name)
})

class StringConfig(name: String?): Config<String?>(name!!, Type.STRING, {
    it.getString(name)
})

class BooleanConfig(name: String?): Config<Boolean?>(name!!, Type.BOOLEAN, {
    it.getBoolean(name)
})

