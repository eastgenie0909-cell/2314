package com.eastgenie.tqqqwidget.store

import android.content.Context
import android.content.SharedPreferences

data class StoredQuote(
    val price: Double,
    val ma200: Double,
    val updatedMs: Long
)

object Store {
    private const val PREFS = "tqqq_store"
    private const val KEY_PRICE = "price"
    private const val KEY_MA200 = "ma200"
    private const val KEY_UPDATED = "updated"

    private fun prefs(ctx: Context): SharedPreferences =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun save(ctx: Context, price: Double, ma200: Double, updated: Long) {
        prefs(ctx).edit()
            .putFloat(KEY_PRICE, price.toFloat())
            .putFloat(KEY_MA200, ma200.toFloat())
            .putLong(KEY_UPDATED, updated)
            .apply()
    }

    fun load(ctx: Context): StoredQuote? {
        val p = prefs(ctx)
        if (!p.contains(KEY_PRICE) || !p.contains(KEY_MA200)) return null
        return StoredQuote(
            price = p.getFloat(KEY_PRICE, 0f).toDouble(),
            ma200 = p.getFloat(KEY_MA200, 0f).toDouble(),
            updatedMs = p.getLong(KEY_UPDATED, 0L)
        )
    }
}
