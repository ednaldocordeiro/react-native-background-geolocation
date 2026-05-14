package com.marianhello.bgloc.react

import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.JavaOnlyArray
import com.facebook.react.bridge.JavaOnlyMap
import com.marianhello.bgloc.Config
import org.junit.Assert
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PowerMockIgnore
import org.powermock.core.classloader.annotations.PrepareForTest
import org.robolectric.RobolectricTestRunner

@PrepareForTest(Arguments::class)
@RunWith(RobolectricTestRunner::class)
@PowerMockIgnore("org.mockito.*", "org.robolectric.*", "android.*")
class ConfigMapperTest {
    @Before
    fun setUp() {
        PowerMockito.mockStatic(Arguments::class.java)
        PowerMockito.`when`(Arguments.createArray()).thenAnswer { JavaOnlyArray() }
        PowerMockito.`when`(Arguments.createMap()).thenAnswer { JavaOnlyMap() }
    }

    @Test
    @Ignore
    fun testDefaultToJSONObject() {
        val config = Config.getDefault()
        val map = ConfigMapper.toMap(config)

        Assert.assertEquals(config.stationaryRadius!!, map.getDouble("stationaryRadius").toFloat(), 0f)
        Assert.assertEquals(config.distanceFilter!!, map.getInt("distanceFilter"))
        Assert.assertEquals(config.desiredAccuracy!!, map.getInt("desiredAccuracy"))
        Assert.assertEquals(config.isDebugging!!, map.getBoolean("debug"))
        Assert.assertEquals(config.notificationTitle, map.getString("notificationTitle"))
        Assert.assertEquals(config.notificationText, map.getString("notificationText"))
        Assert.assertEquals(config.stopOnTerminate!!, map.getBoolean("stopOnTerminate"))
        Assert.assertEquals(config.startOnBoot!!, map.getBoolean("startOnBoot"))
        Assert.assertEquals(config.locationProvider!!, map.getInt("locationProvider"))
        Assert.assertEquals(config.interval!!, map.getInt("interval"))
        Assert.assertEquals(config.fastestInterval!!, map.getInt("fastestInterval"))
        Assert.assertEquals(config.activitiesInterval!!, map.getInt("activitiesInterval"))
        Assert.assertEquals(config.notificationIconColor, map.getString("notificationIconColor"))
        Assert.assertEquals(config.largeNotificationIcon, map.getString("notificationIconLarge"))
        Assert.assertEquals(config.smallNotificationIcon, map.getString("notificationIconSmall"))
        Assert.assertEquals(config.startForeground!!, map.getBoolean("startForeground"))
        Assert.assertEquals(config.stopOnStillActivity!!, map.getBoolean("stopOnStillActivity"))
        Assert.assertEquals(config.url, map.getString("url"))
        Assert.assertEquals(config.syncUrl, map.getString("syncUrl"))
        Assert.assertEquals(config.syncThreshold!!, map.getInt("syncThreshold"))
        Assert.assertEquals(config.maxLocations!!, map.getInt("maxLocations"))
    }
}
