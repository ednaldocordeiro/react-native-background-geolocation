package com.marianhello.bgloc.react

import android.content.Context
import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.soloader.SoLoader
import com.marianhello.bgloc.Config
import com.marianhello.bgloc.data.ArrayListLocationTemplate
import com.marianhello.bgloc.data.HashMapLocationTemplate
import com.marianhello.bgloc.data.LocationTemplateFactory
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
@SmallTest
class ConfigMapperTest {

    @Before
    @Throws(IOException::class)
    fun setUp() {
        val context = InstrumentationRegistry.getContext()
        SoLoader.init(context, 0)
    }

    @Test
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
        Assert.assertEquals(config.httpHeaders, map.getMap("httpHeaders")!!.toHashMap())
        Assert.assertEquals(config.maxLocations!!, map.getInt("maxLocations"))
        Assert.assertEquals(LocationTemplateFactory.getDefault(), LocationTemplateFactory.fromHashMap(map.getMap("postTemplate")!!.toHashMap()))
    }

    @Test
    @Throws(Exception::class)
    fun testNullableProps() {
        val map = Arguments.createMap()
        map.putNull("url")
        map.putNull("syncUrl")
        map.putNull("notificationIconColor")
        map.putNull("notificationTitle")
        map.putNull("notificationText")
        map.putNull("notificationIconLarge")
        map.putNull("notificationIconSmall")

        val config = ConfigMapper.fromMap(map)

        Assert.assertEquals(Config.NullString, config.url)
        Assert.assertTrue(config.hasUrl())
        Assert.assertFalse(config.hasValidUrl())

        Assert.assertEquals(Config.NullString, config.syncUrl)
        Assert.assertTrue(config.hasSyncUrl())
        Assert.assertFalse(config.hasValidSyncUrl())

        Assert.assertEquals(Config.NullString, config.notificationIconColor)
        Assert.assertFalse(config.hasNotificationIconColor())

        Assert.assertEquals(Config.NullString, config.notificationTitle)
        Assert.assertTrue(config.hasNotificationTitle())

        Assert.assertEquals(Config.NullString, config.notificationText)
        Assert.assertTrue(config.hasNotificationText())

        Assert.assertEquals(Config.NullString, config.largeNotificationIcon)
        Assert.assertFalse(config.hasLargeNotificationIcon())

        Assert.assertEquals(Config.NullString, config.smallNotificationIcon)
        Assert.assertFalse(config.hasSmallNotificationIcon())
    }

    @Test
    fun testNullablePropsToJSONObject() {
        val config = Config()
        config.url = Config.NullString
        config.syncUrl = Config.NullString
        config.notificationIconColor = Config.NullString
        config.notificationTitle = Config.NullString
        config.notificationText = Config.NullString
        config.largeNotificationIcon = Config.NullString
        config.smallNotificationIcon = Config.NullString

        val map = ConfigMapper.toMap(config)

        Assert.assertEquals(null, map.getString("url"))
        Assert.assertEquals(null, map.getString("syncUrl"))
        Assert.assertEquals(null, map.getString("notificationIconColor"))
        Assert.assertEquals(null, map.getString("notificationTitle"))
        Assert.assertEquals(null, map.getString("notificationText"))
        Assert.assertEquals(null, map.getString("notificationIconLarge"))
        Assert.assertEquals(null, map.getString("notificationIconSmall"))
    }

    @Test
    fun testNullHashMapTemplateToJSONObject() {
        val config = Config()
        val tpl = HashMapLocationTemplate(null as HashMap<*, *>?)
        config.template = tpl

        val jConfig = ConfigMapper.toMap(config)
        Assert.assertEquals(null, jConfig.getMap("postTemplate"))
    }

    @Test
    fun testEmptyHashMapTemplateToJSONObject() {
        val config = Config()
        val map = HashMap<Any, Any>()
        val tpl = HashMapLocationTemplate(map)
        config.template = tpl

        val jConfig = ConfigMapper.toMap(config)
        Assert.assertFalse(jConfig.getMap("postTemplate")!!.keySetIterator().hasNextKey())
    }

    @Test
    fun testHashMapTemplateToJSONObject() {
        val config = Config()
        val map = HashMap<Any, Any>()
        map["foo"] = "bar"
        map["pretzels"] = 123
        val tpl = HashMapLocationTemplate(map)
        config.template = tpl

        val jConfig = ConfigMapper.toMap(config)
        Assert.assertEquals("{ NativeMap: {\"foo\":\"bar\",\"pretzels\":123.0} }", jConfig.getMap("postTemplate").toString())
    }

    @Test
    fun testNullArrayListLocationTemplateToJSONObject() {
        val config = Config()
        val tpl = ArrayListLocationTemplate(null as ArrayListLocationTemplate?)
        config.template = tpl

        val jConfig = ConfigMapper.toMap(config)
        Assert.assertEquals(null, jConfig.getMap("postTemplate"))
    }

    @Test
    fun testEmptyArrayListLocationTemplateToJSONObject() {
        val config = Config()
        val list = ArrayList<Any>()
        val tpl = ArrayListLocationTemplate(list)
        config.template = tpl

        val jConfig = ConfigMapper.toMap(config)
        Assert.assertTrue(jConfig.getArray("postTemplate")!!.size() == 0)
    }

    @Test
    fun testArrayListLocationTemplateToJSONObject() {
        val config = Config()
        val list = ArrayList<Any>()
        list.add("foo")
        list.add(123)
        list.add("foo")

        val tpl = ArrayListLocationTemplate(list)
        config.template = tpl

        val jConfig = ConfigMapper.toMap(config)
        Assert.assertEquals("[\"foo\",123.0,\"foo\"]", jConfig.getArray("postTemplate").toString())
    }
}
