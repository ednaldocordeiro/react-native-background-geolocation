package com.iodine.start

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType
import com.facebook.react.bridge.WritableMap
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

object MapUtil {
    @Throws(JSONException::class)
    @JvmStatic
    fun toJSONObject(readableMap: ReadableMap): JSONObject {
        val jsonObject = JSONObject()
        val iterator = readableMap.keySetIterator()
        while (iterator.hasNextKey()) {
            val key = iterator.nextKey()
            val type = readableMap.getType(key)
            when (type) {
                ReadableType.Null -> jsonObject.put(key, JSONObject.NULL)
                ReadableType.Boolean -> jsonObject.put(key, readableMap.getBoolean(key))
                ReadableType.Number -> jsonObject.put(key, readableMap.getDouble(key))
                ReadableType.String -> jsonObject.put(key, readableMap.getString(key))
                ReadableType.Map -> jsonObject.put(key, toJSONObject(readableMap.getMap(key)!!))
                ReadableType.Array -> jsonObject.put(key, ArrayUtil.toJSONArray(readableMap.getArray(key)!!))
                else -> {}
            }
        }
        return jsonObject
    }

    @Throws(JSONException::class)
    @JvmStatic
    fun toMap(jsonObject: JSONObject): Map<String, Any?> {
        val map = HashMap<String, Any?>()
        val iterator = jsonObject.keys()
        while (iterator.hasNext()) {
            val key = iterator.next()
            var value: Any? = jsonObject[key]
            if (value is JSONObject) {
                value = toMap(value)
            }
            if (value is JSONArray) {
                value = ArrayUtil.toArray(value)
            }
            map[key] = value
        }
        return map
    }

    @JvmStatic
    fun toMap(readableMap: ReadableMap): Map<String, Any?> {
        val map = HashMap<String, Any?>()
        val iterator = readableMap.keySetIterator()
        while (iterator.hasNextKey()) {
            val key = iterator.nextKey()
            val type = readableMap.getType(key)
            when (type) {
                ReadableType.Null -> map[key] = null
                ReadableType.Boolean -> map[key] = readableMap.getBoolean(key)
                ReadableType.Number -> map[key] = readableMap.getDouble(key)
                ReadableType.String -> map[key] = readableMap.getString(key)
                ReadableType.Map -> map[key] = toMap(readableMap.getMap(key)!!)
                ReadableType.Array -> map[key] = ArrayUtil.toArray(readableMap.getArray(key)!!)
                else -> {}
            }
        }
        return map
    }

    @JvmStatic
    fun toWritableMap(map: Map<String, Any?>): WritableMap {
        val writableMap = Arguments.createMap()
        for ((key, value) in map) {
            when (value) {
                null -> writableMap.putNull(key)
                is Boolean -> writableMap.putBoolean(key, value)
                is Double -> writableMap.putDouble(key, value)
                is Int -> writableMap.putInt(key, value)
                is String -> writableMap.putString(key, value)
                is Map<*, *> -> writableMap.putMap(key, toWritableMap(value as Map<String, Any?>))
                is Array<*> -> writableMap.putArray(key, ArrayUtil.toWritableArray(value as Array<Any?>))
            }
        }
        return writableMap
    }
}
