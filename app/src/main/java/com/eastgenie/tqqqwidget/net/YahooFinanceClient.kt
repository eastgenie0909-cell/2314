package com.eastgenie.tqqqwidget.net

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit
import kotlin.math.round

data class TqqqSnapshot(
    val price: Double,
    val ma200: Double,
    val updatedEpochMs: Long
)

object YahooFinanceClient {
    private val http = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    @Throws(IOException::class)
    fun fetchTqqqSnapshot(): TqqqSnapshot {
        val symbol = "TQQQ"

        // 1) Realtime quote
        val quoteUrl = "https://query1.finance.yahoo.com/v7/finance/quote?symbols=$symbol"
        val quoteJson = getJson(quoteUrl)
        val price = quoteJson.getJSONObject("quoteResponse")
            .getJSONArray("result")
            .getJSONObject(0)
            .getDouble("regularMarketPrice")

        // 2) 1-year daily closes to compute 200-day MA
        val chartUrl = "https://query1.finance.yahoo.com/v8/finance/chart/$symbol?range=1y&interval=1d"
        val chartJson = getJson(chartUrl)

        val closes = chartJson.getJSONObject("chart")
            .getJSONArray("result")
            .getJSONObject(0)
            .getJSONObject("indicators")
            .getJSONArray("quote")
            .getJSONObject(0)
            .getJSONArray("close")

        val last200 = mutableListOf<Double>()
        for (i in 0 until closes.length()) {
            val v = closes.optDouble(i, Double.NaN)
            if (!v.isNaN()) last200.add(v)
        }
        val slice = if (last200.size >= 200) last200.takeLast(200) else last200
        val ma200 = if (slice.isNotEmpty()) slice.average() else price

        return TqqqSnapshot(
            price = round2(price),
            ma200 = round2(ma200),
            updatedEpochMs = System.currentTimeMillis()
        )
    }

    private fun getJson(url: String): JSONObject {
        val req = Request.Builder().url(url).get().build()
        http.newCall(req).execute().use { resp ->
            if (!resp.isSuccessful) throw IOException("HTTP ${'$'}{resp.code}")
            val body = resp.body?.string() ?: throw IOException("Empty body")
            return JSONObject(body)
        }
    }

    private fun round2(x: Double): Double = (round(x * 100.0) / 100.0)
}
