package com.chirput.bardwidgets

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

private const val APP_WIDGET_HOST_ID = 101

class MainActivity : AppCompatActivity() {

    private lateinit var appWidgetManager: AppWidgetManager
    private var interestedYoutubeWidgetProvider: String = "com.google.android.apps.youtube.app.widget.YtQuickActionsWidgetProvider"
    private lateinit var appWidgetHost: AppWidgetHost
    private var appWidgetId = 0

    private val bindWidgetLauncher: ActivityResultLauncher<Input> = registerForActivityResult(BindWidgetContract()) {
        val info = appWidgetManager.getAppWidgetInfo(appWidgetId)
        addWidgetToScreen(info)
    }

    private fun addWidgetToScreen(info: AppWidgetProviderInfo) {
        val constraintLayoutViewGroup = findViewById<FrameLayout>(R.id.single_app_widget)

        constraintLayoutViewGroup.layoutParams = RecyclerView.LayoutParams(500, 500)


        val widgetView: AppWidgetHostView = appWidgetHost.createView(this, appWidgetId, info)

        constraintLayoutViewGroup.addView(widgetView, 0, FrameLayout.LayoutParams(500, 500))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        appWidgetManager = AppWidgetManager.getInstance(this)
        appWidgetHost = AppWidgetHost(this, APP_WIDGET_HOST_ID)

        // get package name of youtube
        val youtubePackageName: String = getYoutubePackageName()

        val installedProvidersForYoutube = appWidgetManager.getInstalledProvidersForPackage(youtubePackageName, null)

        installedProvidersForYoutube.forEach { appWidgetProviderInfo ->
            if (appWidgetProviderInfo.provider.className == interestedYoutubeWidgetProvider) {
                appWidgetId = appWidgetHost.allocateAppWidgetId()
                appWidgetManager.bindAppWidgetIdIfAllowed(appWidgetId, appWidgetProviderInfo.provider)
                bindWidgetLauncher.launch(Input(appWidgetId, appWidgetProviderInfo))
            }
        }
    }

    private fun getYoutubePackageName(): String = "com.google.android.youtube"

    private class BindWidgetContract :
        ActivityResultContract<Input, Any>() {
        override fun createIntent(
            context: Context,
            input: Input
        ): Intent {
            return Intent(AppWidgetManager.ACTION_APPWIDGET_BIND)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, input.appWidgetId)
                .putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, input.info.provider)
                .putExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_PROVIDER_PROFILE,
                    input.info.profile
                )
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Any {
            return Any()
        }
    }

    internal class Input(val appWidgetId: Int, val info: AppWidgetProviderInfo)


}