package com.marianhello.bgloc.react.data

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.WritableMap
import com.marianhello.bgloc.data.BackgroundLocation
import com.marianhello.utils.Convert

object LocationMapper {
    @JvmStatic
    fun toWriteableMap(location: BackgroundLocation): WritableMap {
        val out = Arguments.createMap()
        out.putString("provider", location.provider)
        
        location.locationProvider?.let { out.putInt("locationProvider", it) }
        out.putDouble("time", location.time.toDouble())
        out.putDouble("latitude", location.latitude)
        out.putDouble("longitude", location.longitude)
        
        if (location.hasAccuracy()) out.putDouble("accuracy", location.accuracy.toDouble())
        if (location.hasSpeed()) out.putDouble("speed", location.speed.toDouble())
        if (location.hasAltitude()) out.putDouble("altitude", location.altitude)
        if (location.hasBearing()) out.putDouble("bearing", location.bearing.toDouble())
        if (location.hasRadius()) out.putDouble("radius", location.radius.toDouble())
        if (location.hasIsFromMockProvider()) out.putBoolean("isFromMockProvider", location.isFromMockProvider)
        if (location.hasMockLocationsEnabled()) out.putBoolean("mockLocationsEnabled", location.areMockLocationsEnabled)

        return out
    }

    @JvmStatic
    fun toWriteableMapWithId(location: BackgroundLocation): WritableMap {
        val out = toWriteableMap(location)
        location.locationId?.let { out.putInt("id", Convert.safeLongToInt(it)) }
        return out
    }
}
