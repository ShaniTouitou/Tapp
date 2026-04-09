package com.shani.spinwheel.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.RemoteViews
import com.shani.spinwheel.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.random.Random

/**
 * This class is responsible for managing the spin wheel home screen widget,
 * including UI updates, user interactions, and spin animation lifecycle.
 */
class SpinWheelWidgetReceiver : AppWidgetProvider() {

    // region AppWidgetProvider Lifecycle

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        spinning.set(false)

        for (id in appWidgetIds) {
            updateWidget(context, appWidgetManager, id)
        }

        if (WidgetState.getWheelPath(context).isNullOrBlank()) {
            CoroutineScope(Dispatchers.IO).launch {
                SpinWheelWidgetUpdater.refreshFromRemote(context)
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)

        when (intent.action) {
            ACTION_SPIN -> {
                val pendingResult = goAsync()
                startSpinAnimation(context) { pendingResult.finish() }
            }

            ACTION_REFRESH -> {
                CoroutineScope(Dispatchers.IO).launch {
                    SpinWheelWidgetUpdater.refreshFromRemote(context)
                }
            }
        }
    }

    // endregion

    companion object {

        // region Constants Members

        const val ACTION_SPIN    = "com.shani.spinwheel.ACTION_SPIN"
        const val ACTION_REFRESH = "com.shani.spinwheel.ACTION_REFRESH"

        private const val TAG = "SpinWheelWidget"

        private const val FRAME_DELAY_MS = 16L
        private const val STATUS_SPUN_FORMAT = "Spun %dx · %dms"

        private val SECTORS = listOf(
            "5 pts", "10 pts", "20 pts", "Try again",
            "30 pts", "40 pts", "50 pts", "100 pts"
        )

        // endregion

        // region State

        private val spinning = AtomicBoolean(false)

        // endregion

        // region Public Methods

        fun updateAllWidgets(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, SpinWheelWidgetReceiver::class.java)
            )

            for (id in ids) {
                updateWidget(context, manager, id)
            }
        }

        fun updateWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            appWidgetManager.updateAppWidget(appWidgetId, buildFullViews(context))
        }

        // endregion

        // region Spin Animation

        private fun startSpinAnimation(context: Context, onComplete: () -> Unit) {
            if (!spinning.compareAndSet(false, true)) {
                onComplete()
                return
            }

            if (WidgetState.getWheelPath(context).isNullOrBlank()) {
                spinning.set(false)
                onComplete()

                CoroutineScope(Dispatchers.IO).launch {
                    SpinWheelWidgetUpdater.refreshFromRemote(context)
                }
                return
            }

            val manager = AppWidgetManager.getInstance(context)
            val ids = manager.getAppWidgetIds(
                ComponentName(context, SpinWheelWidgetReceiver::class.java)
            )

            val minSpins   = WidgetState.getMinSpins(context)
            val maxSpins   = WidgetState.getMaxSpins(context).coerceAtLeast(minSpins)
            val durationMs = WidgetState.getDurationMs(context).toLong()

            val turns    = Random.nextInt(minSpins, maxSpins + 1)
            val extraDeg = Random.nextInt(0, 360)

            val startRot   = WidgetState.getRotation(context)
            val totalDelta = turns * 360f + extraDeg

            val interpolator = AccelerateDecelerateInterpolator()
            val handler = Handler(Looper.getMainLooper())
            val startTimeMs = System.currentTimeMillis()

            val frame = object : Runnable {
                override fun run() {
                    val elapsed = System.currentTimeMillis() - startTimeMs
                    val raw = (elapsed.toFloat() / durationMs).coerceIn(0f, 1f)

                    val current = normalizeDegrees(
                        startRot + totalDelta * interpolator.getInterpolation(raw)
                    )

                    val partial = RemoteViews(
                        context.packageName,
                        R.layout.spin_wheel_widget_main
                    )
                    partial.setFloat(R.id.widget_wheel, "setRotation", current)

                    try {
                        for (id in ids) {
                            manager.partiallyUpdateAppWidget(id, partial)
                        }
                    } catch (e: Exception) {
                        Log.w(TAG, "partial update failed: ${e.message}")
                    }

                    if (raw < 1f) {
                        handler.postDelayed(this, FRAME_DELAY_MS)
                    } else {
                        finalizeSpin(context, manager, ids, startRot, totalDelta, turns, durationMs.toInt())
                        onComplete()
                    }
                }
            }

            handler.post(frame)
        }

        private fun finalizeSpin(
            context: Context,
            manager: AppWidgetManager,
            ids: IntArray,
            startRotation: Float,
            totalDelta: Float,
            turns: Int,
            durationMs: Int
        ) {
            val finalRotation = normalizeDegrees(startRotation + totalDelta)

            WidgetState.setRotation(context, finalRotation)
            WidgetState.setResult(context, SECTORS[sectorIndex(finalRotation)])
            WidgetState.setStatus(
                context,
                STATUS_SPUN_FORMAT.format(turns, durationMs)
            )

            spinning.set(false)

            for (id in ids) {
                updateWidget(context, manager, id)
            }
        }

        // endregion

        // region RemoteViews Builder

        private fun buildFullViews(context: Context): RemoteViews {
            val views = RemoteViews(
                context.packageName,
                R.layout.spin_wheel_widget_main
            )

            if (!WidgetState.getWheelPath(context).isNullOrBlank()) {
                views.setViewVisibility(R.id.widget_loading_overlay, View.GONE)

                WidgetState.getBgPath(context)?.let { path ->
                    BitmapFactory.decodeFile(path)?.let {
                        views.setImageViewBitmap(R.id.widget_background, it)
                    }
                }

                WidgetState.getWheelPath(context)?.let { path ->
                    BitmapFactory.decodeFile(path)?.let {
                        views.setImageViewBitmap(R.id.widget_wheel, it)
                    }
                }

                WidgetState.getFramePath(context)?.let { path ->
                    BitmapFactory.decodeFile(path)?.let {
                        views.setImageViewBitmap(R.id.widget_frame, it)
                    }
                }

                WidgetState.getSpinPath(context)?.let { path ->
                    BitmapFactory.decodeFile(path)?.let {
                        views.setImageViewBitmap(R.id.widget_spin_button, it)
                    }
                }

                views.setFloat(
                    R.id.widget_wheel,
                    "setRotation",
                    WidgetState.getRotation(context)
                )

            } else {
                views.setViewVisibility(R.id.widget_loading_overlay, View.VISIBLE)
            }

            val flags = PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

            val spinIntent = Intent(context, SpinWheelWidgetReceiver::class.java).apply {
                action = ACTION_SPIN
            }

            views.setOnClickPendingIntent(
                R.id.widget_spin_button,
                PendingIntent.getBroadcast(context, 0, spinIntent, flags)
            )

            views.setOnClickPendingIntent(
                R.id.widget_wheel_container,
                PendingIntent.getBroadcast(context, 1, spinIntent, flags)
            )

            return views
        }

        // endregion

        // region Helpers Methods

        private fun normalizeDegrees(degrees: Float): Float =
            ((degrees % 360f) + 360f) % 360f

        private fun sectorIndex(finalRotationDegrees: Float): Int =
            (normalizeDegrees(360f - finalRotationDegrees) / 45f)
                .toInt()
                .coerceIn(0, 7)

        // endregion

    }
}