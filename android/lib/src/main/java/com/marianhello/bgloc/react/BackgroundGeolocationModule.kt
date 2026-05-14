package com.marianhello.bgloc.react

import android.content.Context
import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.marianhello.bgloc.BackgroundGeolocationFacade
import com.marianhello.bgloc.Config
import com.marianhello.bgloc.PluginDelegate
import com.marianhello.bgloc.PluginException
import com.marianhello.bgloc.data.BackgroundActivity
import com.marianhello.bgloc.data.BackgroundLocation
import com.marianhello.bgloc.react.data.LocationMapper
import com.marianhello.bgloc.react.headless.HeadlessTaskRunner
import com.marianhello.logging.LogEntry
import com.marianhello.logging.LoggerManager
import org.json.JSONException

class BackgroundGeolocationModule(reactContext: ReactApplicationContext) :
    ReactContextBaseJavaModule(reactContext), LifecycleEventListener, PluginDelegate {

    private var facade: BackgroundGeolocationFacade = BackgroundGeolocationFacade(reactContext, this)
    private var logger: org.slf4j.Logger? = LoggerManager.getLogger(BackgroundGeolocationModule::class.java)
    private var currentContext: ReactContext = reactContext

    init {
        currentContext.addLifecycleEventListener(this)
    }

    override fun getName(): String {
        return "BackgroundGeolocation"
    }

    override fun onHostResume() {
        logger?.info("App will be resumed")
        facade.resume()
        sendEvent(FOREGROUND_EVENT, null)
    }

    override fun onHostPause() {
        logger?.info("App will be paused")
        facade.pause()
        sendEvent(BACKGROUND_EVENT, null)
    }

    override fun onHostDestroy() {
        logger?.info("Destroying plugin")
        facade.destroy()
    }

    override fun onCatalystInstanceDestroy() {
        super.onCatalystInstanceDestroy()
        logger?.info("Destroying plugin facade")
        facade.destroy()
    }

    private fun runOnBackgroundThread(runnable: Runnable) {
        Thread(runnable).start()
    }

    @ReactMethod
    fun start() {
        facade.start()
    }

    @ReactMethod
    fun stop() {
        facade.stop()
    }

    @ReactMethod
    fun switchMode(mode: Int, success: Callback, error: Callback) {
        facade.switchMode(mode)
    }

    @ReactMethod
    fun configure(options: ReadableMap, success: Callback, error: Callback) {
        runOnBackgroundThread(Runnable {
            try {
                val config = ConfigMapper.fromMap(options)
                facade.configure(config)
                success.invoke(true)
            } catch (e: JSONException) {
                logger?.error("Configuration error: {}", e.message)
                error.invoke(ErrorMap.from("Configuration error", e, PluginException.CONFIGURE_ERROR))
            } catch (e: PluginException) {
                logger?.error("Configuration error: {}", e.message)
                error.invoke(ErrorMap.from(e))
            }
        })
    }

    @ReactMethod
    fun showLocationSettings() {
        BackgroundGeolocationFacade.showLocationSettings(context)
    }

    @ReactMethod
    fun showAppSettings() {
        BackgroundGeolocationFacade.showAppSettings(context)
    }

    @ReactMethod
    fun getStationaryLocation(success: Callback, error: Callback) {
        val stationaryLocation = facade.stationaryLocation
        if (stationaryLocation != null) {
            success.invoke(LocationMapper.toWriteableMap(stationaryLocation))
        } else {
            success.invoke()
        }
    }

    @ReactMethod
    fun getLocations(success: Callback, error: Callback) {
        runOnBackgroundThread(Runnable {
            val locationsArray = Arguments.createArray()
            val locations = facade.locations
            for (location in locations) {
                locationsArray.pushMap(LocationMapper.toWriteableMapWithId(location))
            }
            success.invoke(locationsArray)
        })
    }

    @ReactMethod
    fun getValidLocations(success: Callback, error: Callback) {
        runOnBackgroundThread(Runnable {
            val locationsArray = Arguments.createArray()
            val locations = facade.validLocations
            for (location in locations) {
                locationsArray.pushMap(LocationMapper.toWriteableMapWithId(location))
            }
            success.invoke(locationsArray)
        })
    }

    @ReactMethod
    fun deleteLocation(locationId: Int, success: Callback, error: Callback) {
        runOnBackgroundThread(Runnable {
            facade.deleteLocation(locationId.toLong())
            success.invoke(true)
        })
    }

    @ReactMethod
    fun deleteAllLocations(success: Callback, error: Callback) {
        runOnBackgroundThread(Runnable {
            facade.deleteAllLocations()
            success.invoke(true)
        })
    }

    @ReactMethod
    fun getCurrentLocation(options: ReadableMap, success: Callback, error: Callback) {
        runOnBackgroundThread(Runnable {
            try {
                val timeout = if (options.hasKey("timeout")) options.getInt("timeout") else Int.MAX_VALUE
                val maximumAge = if (options.hasKey("maximumAge")) options.getInt("maximumAge").toLong() else Long.MAX_VALUE
                val enableHighAccuracy = options.hasKey("enableHighAccuracy") && options.getBoolean("enableHighAccuracy")

                val location = facade.getCurrentLocation(timeout, maximumAge, enableHighAccuracy)
                success.invoke(LocationMapper.toWriteableMap(location))
            } catch (e: PluginException) {
                error.invoke(ErrorMap.from(e))
            }
        })
    }

    @ReactMethod
    fun getConfig(success: Callback, error: Callback) {
        runOnBackgroundThread(Runnable {
            val config = facade.config
            val out = ConfigMapper.toMap(config)
            success.invoke(out)
        })
    }

    @ReactMethod
    fun getLogEntries(limit: Int, offset: Int, minLevel: String, success: Callback, error: Callback) {
        runOnBackgroundThread(Runnable {
            val logEntriesArray = Arguments.createArray()
            val logEntries = facade.getLogEntries(limit, offset, minLevel)
            for (logEntry in logEntries) {
                val out = Arguments.createMap()
                out.putInt("id", logEntry.id)
                out.putInt("context", logEntry.context)
                out.putString("level", logEntry.level)
                out.putString("message", logEntry.message)
                out.putString("timestamp", logEntry.timestamp.toString())
                out.putString("logger", logEntry.loggerName)
                if (logEntry.hasStackTrace()) {
                    out.putString("stackTrace", logEntry.stackTrace)
                }

                logEntriesArray.pushMap(out)
            }
            success.invoke(logEntriesArray)
        })
    }

    @ReactMethod
    fun checkStatus(success: Callback, error: Callback) {
        runOnBackgroundThread(Runnable {
            try {
                val out = Arguments.createMap()
                out.putBoolean("isRunning", facade.isRunning)
                out.putBoolean("hasPermissions", facade.hasPermissions())
                out.putBoolean("locationServicesEnabled", facade.locationServicesEnabled())
                out.putInt("authorization", authorizationStatus)
                success.invoke(out)
            } catch (e: PluginException) {
                logger?.error("Location service checked failed: {}", e.message)
                error.invoke(ErrorMap.from(e))
            }
        })
    }

    @ReactMethod
    fun registerHeadlessTask(success: Callback, error: Callback) {
        logger?.debug("Registering headless task")
        facade.registerHeadlessTask(HeadlessTaskRunner::class.java.name)
        success.invoke()
    }

    @ReactMethod
    fun forceSync(success: Callback, error: Callback) {
        facade.forceSync()
        success.invoke()
    }

    private fun sendEvent(eventName: String, params: Any?) {
        try {
            currentContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
                .emit(eventName, params)
        } catch (e: Exception) {
            logger?.debug("error sending event: {}", e.toString())
        }
    }

    private fun sendError(error: PluginException) {
        val out = Arguments.createMap()
        out.putInt("code", error.code)
        out.putString("message", error.message)

        sendEvent(ERROR_EVENT, out)
    }

    private fun sendError(code: Int, message: String) {
        val out = Arguments.createMap()
        out.putInt("code", code)
        out.putString("message", message)

        sendEvent(ERROR_EVENT, out)
    }

    val authorizationStatus: Int
        get() = facade.authorizationStatus

    val context: ReactContext
        get() = currentContext

    override fun onAuthorizationChanged(authStatus: Int) {
        sendEvent(AUTHORIZATION_EVENT, authStatus)
    }

    override fun onLocationChanged(location: BackgroundLocation) {
        sendEvent(LOCATION_EVENT, LocationMapper.toWriteableMapWithId(location))
    }

    override fun onStationaryChanged(location: BackgroundLocation) {
        sendEvent(STATIONARY_EVENT, LocationMapper.toWriteableMapWithId(location))
    }

    override fun onActivityChanged(activity: BackgroundActivity) {
        val out = Arguments.createMap()
        out.putInt("confidence", activity.confidence)
        out.putString("type", BackgroundActivity.getActivityString(activity.type))
        sendEvent(ACTIVITY_EVENT, out)
    }

    override fun onServiceStatusChanged(status: Int) {
        when (status) {
            BackgroundGeolocationFacade.SERVICE_STARTED -> {
                sendEvent(START_EVENT, null)
                return
            }
            BackgroundGeolocationFacade.SERVICE_STOPPED -> {
                sendEvent(STOP_EVENT, null)
                return
            }
        }
    }

    override fun onError(error: PluginException) {
        sendError(error)
    }

    override fun onAbortRequested() {
        sendEvent(ABORT_REQUESTED_EVENT, null)
    }

    override fun onHttpAuthorization() {
        sendEvent(HTTP_AUTHORIZATION_EVENT, null)
    }

    object ErrorMap {
        fun from(message: String, code: Int): ReadableMap {
            val out = Arguments.createMap()
            out.putInt("code", code)
            out.putString("message", message)
            return out
        }

        fun from(message: String, cause: Throwable, code: Int): ReadableMap {
            val out = Arguments.createMap()
            out.putInt("code", code)
            out.putString("message", message)
            out.putMap("cause", from(cause))
            return out
        }

        fun from(e: PluginException): ReadableMap {
            val out = Arguments.createMap()
            out.putInt("code", e.code)
            out.putString("message", e.message)
            if (e.cause != null) {
                out.putMap("cause", from(e.cause!!))
            }
            return out
        }

        private fun from(e: Throwable): WritableMap {
            val out = Arguments.createMap()
            out.putString("message", e.message)
            return out
        }
    }

    companion object {
        const val LOCATION_EVENT = "location"
        const val STATIONARY_EVENT = "stationary"
        const val ACTIVITY_EVENT = "activity"

        const val FOREGROUND_EVENT = "foreground"
        const val BACKGROUND_EVENT = "background"
        const val AUTHORIZATION_EVENT = "authorization"

        const val START_EVENT = "start"
        const val STOP_EVENT = "stop"
        const val ABORT_REQUESTED_EVENT = "abort_requested"
        const val HTTP_AUTHORIZATION_EVENT = "http_authorization"
        const val ERROR_EVENT = "error"

        private const val PERMISSIONS_REQUEST_CODE = 1
    }
}
