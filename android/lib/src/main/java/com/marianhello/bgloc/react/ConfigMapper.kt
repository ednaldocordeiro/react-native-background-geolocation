package com.marianhello.bgloc.react

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType
import com.facebook.react.bridge.WritableMap
import com.iodine.start.ArrayUtil
import com.iodine.start.MapUtil
import com.marianhello.bgloc.Config
import com.marianhello.bgloc.data.ArrayListLocationTemplate
import com.marianhello.bgloc.data.HashMapLocationTemplate
import com.marianhello.bgloc.data.LocationTemplateFactory
import org.json.JSONException

object ConfigMapper {
    @Throws(JSONException::class)
    @JvmStatic
    fun fromMap(options: ReadableMap): Config {
        val config = Config()
        if (options.hasKey("stationaryRadius")) config.stationaryRadius = options.getDouble("stationaryRadius").toFloat()
        if (options.hasKey("distanceFilter")) config.distanceFilter = options.getInt("distanceFilter")
        if (options.hasKey("desiredAccuracy")) config.desiredAccuracy = options.getInt("desiredAccuracy")
        if (options.hasKey("debug")) config.isDebugging = options.getBoolean("debug")
        if (options.hasKey("notificationTitle")) config.notificationTitle =
            if (!options.isNull("notificationTitle")) options.getString("notificationTitle") else Config.NullString
        if (options.hasKey("notificationText")) config.notificationText =
            if (!options.isNull("notificationText")) options.getString("notificationText") else Config.NullString
        if (options.hasKey("notificationIconLarge")) config.largeNotificationIcon =
            if (!options.isNull("notificationIconLarge")) options.getString("notificationIconLarge") else Config.NullString
        if (options.hasKey("notificationIconSmall")) config.smallNotificationIcon =
            if (!options.isNull("notificationIconSmall")) options.getString("notificationIconSmall") else Config.NullString
        if (options.hasKey("notificationIconColor")) config.notificationIconColor =
            if (!options.isNull("notificationIconColor")) options.getString("notificationIconColor") else Config.NullString
        if (options.hasKey("stopOnTerminate")) config.stopOnTerminate = options.getBoolean("stopOnTerminate")
        if (options.hasKey("startOnBoot")) config.startOnBoot = options.getBoolean("startOnBoot")
        if (options.hasKey("startForeground")) config.startForeground = options.getBoolean("startForeground")
        if (options.hasKey("notificationsEnabled")) config.notificationsEnabled = options.getBoolean("notificationsEnabled")
        if (options.hasKey("locationProvider")) config.locationProvider = options.getInt("locationProvider")
        if (options.hasKey("interval")) config.interval = options.getInt("interval")
        if (options.hasKey("fastestInterval")) config.fastestInterval = options.getInt("fastestInterval")
        if (options.hasKey("activitiesInterval")) config.activitiesInterval = options.getInt("activitiesInterval")
        if (options.hasKey("stopOnStillActivity")) config.stopOnStillActivity = options.getBoolean("stopOnStillActivity")
        if (options.hasKey("url")) config.url =
            if (!options.isNull("url")) options.getString("url") else Config.NullString
        if (options.hasKey("syncUrl")) config.syncUrl =
            if (!options.isNull("syncUrl")) options.getString("syncUrl") else Config.NullString
        if (options.hasKey("syncThreshold")) config.syncThreshold = options.getInt("syncThreshold")
        
        if (options.hasKey("httpHeaders")) {
            val type = options.getType("httpHeaders")
            if (type != ReadableType.Map) {
                throw JSONException("httpHeaders must be object")
            }
            val httpHeadersJson = MapUtil.toJSONObject(options.getMap("httpHeaders")!!)
            config.httpHeaders = httpHeadersJson
        }
        if (options.hasKey("maxLocations")) config.maxLocations = options.getInt("maxLocations")

        if (options.hasKey("postTemplate")) {
            if (options.isNull("postTemplate")) {
                config.template = LocationTemplateFactory.getDefault()
            } else {
                val type = options.getType("postTemplate")
                var postTemplate: Any? = null
                if (type == ReadableType.Map) {
                    postTemplate = MapUtil.toJSONObject(options.getMap("postTemplate")!!)
                } else if (type == ReadableType.Array) {
                    postTemplate = ArrayUtil.toJSONArray(options.getArray("postTemplate")!!)
                }
                config.template = LocationTemplateFactory.fromJSON(postTemplate)
            }
        }

        return config
    }

    @JvmStatic
    fun toMap(config: Config): ReadableMap {
        val out = Arguments.createMap()
        val httpHeaders = Arguments.createMap()
        
        config.stationaryRadius?.let { out.putDouble("stationaryRadius", it.toDouble()) }
        config.distanceFilter?.let { out.putInt("distanceFilter", it) }
        config.desiredAccuracy?.let { out.putInt("desiredAccuracy", it) }
        config.isDebugging?.let { out.putBoolean("debug", it) }
        
        config.notificationTitle?.let {
            if (it !== Config.NullString) out.putString("notificationTitle", it)
            else out.putNull("notificationTitle")
        }
        config.notificationText?.let {
            if (it !== Config.NullString) out.putString("notificationText", it)
            else out.putNull("notificationText")
        }
        config.largeNotificationIcon?.let {
            if (it !== Config.NullString) out.putString("notificationIconLarge", it)
            else out.putNull("notificationIconLarge")
        }
        config.smallNotificationIcon?.let {
            if (it !== Config.NullString) out.putString("notificationIconSmall", it)
            else out.putNull("notificationIconSmall")
        }
        config.notificationIconColor?.let {
            if (it !== Config.NullString) out.putString("notificationIconColor", it)
            else out.putNull("notificationIconColor")
        }
        
        config.stopOnTerminate?.let { out.putBoolean("stopOnTerminate", it) }
        config.startOnBoot?.let { out.putBoolean("startOnBoot", it) }
        config.startForeground?.let { out.putBoolean("startForeground", it) }
        config.notificationsEnabled?.let { out.putBoolean("notificationsEnabled", it) }
        config.locationProvider?.let { out.putInt("locationProvider", it) }
        config.interval?.let { out.putInt("interval", it) }
        config.fastestInterval?.let { out.putInt("fastestInterval", it) }
        config.activitiesInterval?.let { out.putInt("activitiesInterval", it) }
        config.stopOnStillActivity?.let { out.putBoolean("stopOnStillActivity", it) }
        
        config.url?.let {
            if (it !== Config.NullString) out.putString("url", it)
            else out.putNull("url")
        }
        config.syncUrl?.let {
            if (it !== Config.NullString) out.putString("syncUrl", it)
            else out.putNull("syncUrl")
        }
        config.syncThreshold?.let { out.putInt("syncThreshold", it) }
        
        config.httpHeaders?.let { headers ->
            for ((key, value) in headers) {
                httpHeaders.putString(key as String, value as String)
            }
        }
        out.putMap("httpHeaders", httpHeaders)
        
        config.maxLocations?.let { out.putInt("maxLocations", it) }

        val tpl = config.template
        if (tpl is HashMapLocationTemplate) {
            val map = tpl.toMap()
            if (map != null) {
                out.putMap("postTemplate", MapUtil.toWritableMap(map as Map<String, Any?>))
            } else {
                out.putNull("postTemplate")
            }
        } else if (tpl is ArrayListLocationTemplate) {
            val keys = tpl.toArray()
            if (keys != null) {
                out.putArray("postTemplate", ArrayUtil.toWritableArray(keys as Array<Any?>))
            } else {
                out.putNull("postTemplate")
            }
        }
        return out
    }
}
