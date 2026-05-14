package com.marianhello.bgloc.react.headless

import android.content.Intent
import com.marianhello.bgloc.headless.AbstractTaskRunner
import com.marianhello.bgloc.headless.Task

class HeadlessTaskRunner : AbstractTaskRunner() {
    override fun runTask(task: Task) {
        val service = Intent(mContext, HeadlessService::class.java)
        service.putExtras(task.bundle)
        mContext.startService(service)
    }
}
