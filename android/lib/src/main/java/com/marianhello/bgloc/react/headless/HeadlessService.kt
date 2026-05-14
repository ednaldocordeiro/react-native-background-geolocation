package com.marianhello.bgloc.react.headless

import android.content.Intent
import com.facebook.react.HeadlessJsTaskService
import com.facebook.react.bridge.Arguments
import com.facebook.react.jstasks.HeadlessJsTaskConfig

class HeadlessService : HeadlessJsTaskService() {
    val TASK_KEY = "com.marianhello.bgloc.react.headless.Task"

    override fun getTaskConfig(intent: Intent): HeadlessJsTaskConfig? {
        val extras = intent.extras
        if (extras != null) {
            return HeadlessJsTaskConfig(
                TASK_KEY,
                Arguments.fromBundle(extras),
                60000, // timeout for the task
                true // optional: defines whether or not the task is allowed in foreground. Default is false
            )
        }
        return null
    }
}
