package com.iodine.start

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableType
import com.facebook.react.bridge.WritableArray
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object ArrayUtil {
    @Throws(JSONException::class)
    @JvmStatic
    fun toJSONArray(readableArray: ReadableArray): JSONArray {
        val jsonArray = JSONArray()
        for (i in 0 until readableArray.size()) {
            val type = readableArray.getType(i)
            when (type) {
                ReadableType.Null -> jsonArray.put(i, JSONObject.NULL)
                ReadableType.Boolean -> jsonArray.put(i, readableArray.getBoolean(i))
                ReadableType.Number -> jsonArray.put(i, readableArray.getDouble(i))
                ReadableType.String -> jsonArray.put(i, readableArray.getString(i))
                ReadableType.Map -> jsonArray.put(i, MapUtil.toJSONObject(readableArray.getMap(i)!!))
                ReadableType.Array -> jsonArray.put(i, toJSONArray(readableArray.getArray(i)!!))
                else -> {}
            }
        }
        return jsonArray
    }

    @Throws(JSONException::class)
    @JvmStatic
    fun toArray(jsonArray: JSONArray): Array<Any?> {
        val array = arrayOfNulls<Any>(jsonArray.length())
        for (i in 0 until jsonArray.length()) {
            var value = jsonArray[i]
            if (value is JSONObject) {
                value = MapUtil.toMap(value)
            }
            if (value is JSONArray) {
                value = toArray(value)
            }
            array[i] = value
        }
        return array
    }

    @JvmStatic
    fun toArray(readableArray: ReadableArray): Array<Any?> {
        val array = arrayOfNulls<Any>(readableArray.size())
        for (i in 0 until readableArray.size()) {
            val type = readableArray.getType(i)
            when (type) {
                ReadableType.Null -> array[i] = null
                ReadableType.Boolean -> array[i] = readableArray.getBoolean(i)
                ReadableType.Number -> array[i] = readableArray.getDouble(i)
                ReadableType.String -> array[i] = readableArray.getString(i)
                ReadableType.Map -> array[i] = MapUtil.toMap(readableArray.getMap(i)!!)
                ReadableType.Array -> array[i] = toArray(readableArray.getArray(i)!!)
                else -> {}
            }
        }
        return array
    }

    @JvmStatic
    fun toWritableArray(array: Array<Any?>): WritableArray {
        val writableArray = Arguments.createArray()
        for (value in array) {
            when (value) {
                null -> writableArray.pushNull()
                is Boolean -> writableArray.pushBoolean(value)
                is Double -> writableArray.pushDouble(value)
                is Int -> writableArray.pushInt(value)
                is String -> writableArray.pushString(value)
                is Map<*, *> -> writableArray.pushMap(MapUtil.toWritableMap(value as Map<String, Any?>))
                is Array<*> -> writableArray.pushArray(toWritableArray(value as Array<Any?>))
            }
        }
        return writableArray
    }
}
