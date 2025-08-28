package com.eastgenie.tqqqwidget.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.updateAll
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.unit.dp
import com.eastgenie.tqqqwidget.store.Store
import com.eastgenie.tqqqwidget.worker.UpdateWorker
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TQQQWidget : GlanceAppWidget() {
    @Composable
    override fun Content() {
        val ctx = LocalContext.current
        val data = Store.load(ctx)
        val titleStyle = TextStyle(color = ColorProvider(android.graphics.Color.WHITE))
        val normal = TextStyle(color = ColorProvider(android.graphics.Color.WHITE))

        Column(
            modifier = GlanceModifier
                .fillMaxWidth()
                .background(android.graphics.Color.parseColor("#111111"))
                .padding(12.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = "TQQQ 위젯", style = titleStyle)
            Spacer(modifier = GlanceModifier.height(6.dp))
            if (data == null) {
                Text(text = "데이터 수집 중...", style = normal)
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("가격: ${'$'}{format2(data.price)}", style = normal)
                    Spacer(GlanceModifier.width(12.dp))
                    Text("200MA: ${'$'}{format2(data.ma200)}", style = normal)
                }
                Spacer(modifier = GlanceModifier.height(4.dp))
                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                Text(text = "업데이트: ${'$'}{sdf.format(Date(data.updatedMs))}", style = normal)
            }
        }
    }

    override suspend fun onUpdate(context: Context, glanceId: GlanceId) {
        super.onUpdate(context, glanceId)
        UpdateWorker.enqueueNow(context)
    }

    override suspend fun onEnabled(context: Context) {
        super.onEnabled(context)
        UpdateWorker.ensurePeriodic(context)
        UpdateWorker.enqueueNow(context)
    }
}

// Helpers
@Composable
private fun LocalContext() = androidx.glance.LocalContext.current

private fun format2(x: Double) = String.format(Locale.US, "%.2f", x)

class TQQQWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = TQQQWidget()
}
