package com.shani.spinwheel

import android.view.View
import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp
import com.shani.spinwheel.widget.SpinWheelWidgetUpdater

/**
 * This class is the React Native view manager
 * that creates and exposes the native Spin Wheel view to the JavaScript layer.
 */
class SpinWheelViewManager : SimpleViewManager<View>() {

    // region Override Methods

    override fun getName(): String = "SpinWheelView"

    override fun createViewInstance(reactContext: ThemedReactContext): View {
        return SpinWheelView(reactContext)
    }

    // endregion

    // region Public Methods

    @ReactProp(name = "configUrl")
    fun setConfigUrl(view: View, url: String?) {
        val spinWheelView = view as? SpinWheelView ?: return
        if (!url.isNullOrBlank()) {
            SpinWheelWidgetUpdater.saveConfigUrl(spinWheelView.context, url)
            spinWheelView.setConfigUrl(url)
            spinWheelView.loadConfig()
        }
    }

    // endregion

}