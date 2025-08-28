package com.eastgenie.tqqqwidget.worker

import android.content.ComponentName
import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.updateAppWidget
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.eastgenie.tqqqwidget.net.YahooFinanceClient
import com.eastgenie.tqqqwidget.store.Store
import com.eastgenie.tqqqwidget.widget.TQQQWidget
import com.eastgenie.tqqqwidget.widget.TQQQWidgetReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

class UpdateWorker(appContext: Context, params: androidx.work.WorkerParameters) :
    CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        return@withContext try {
            val snapshot = YahooFinanceClient.fetchTqqqSnapshot()
            Store.save(applicationContext, snapshot.price, snapshot.ma200, snapshot.updatedEpochMs)

            // Trigger widget update for all instances
            val manager = GlanceAppWidgetManager(applicationContext)
            val glanceIds = manager.getGlanceIds(TQQQWidget::class.java)
            glanceIds.forEach { id ->
                TQQQWidget().updateAppWidget(applicationContext, id)
            }
            Result.success()
        } catch (t: Throwable) {
            t.printStackTrace()
            Result.retry()
        }
    }

    companion object {
        private const val PERIODIC_NAME = "tqqq_periodic_refresh"
        fun ensurePeriodic(context: Context) {
            val work = PeriodicWorkRequestBuilder<UpdateWorker>(30, TimeUnit.MINUTES)
                .addTag(PERIODIC_NAME)
                .build()
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                PERIODIC_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                work
            )
        }

        fun enqueueNow(context: Context) {
            val now = OneTimeWorkRequestBuilder<UpdateWorker>().build()
            WorkManager.getInstance(context).enqueue(now)
        }
    }
}
