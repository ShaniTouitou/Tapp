package com.shani.spinwheel

import com.facebook.react.uimanager.SimpleViewManager
import com.facebook.react.uimanager.ThemedReactContext
import com.facebook.react.uimanager.annotations.ReactProp

class SpinWheelViewManager : SimpleViewManager<SpinWheelView>() {

    // region Override Methods

    override fun getName(): String = "SpinWheelView"

    override fun createViewInstance(reactContext: ThemedReactContext): SpinWheelView {
        return SpinWheelView(reactContext)
    }

    // endregion

    // region Public Methods

    @ReactProp(name = "configUrl")
    fun setConfigUrl(view: SpinWheelView, configUrl: String?) {
        if (!configUrl.isNullOrBlank()) {
            view.setConfigUrl(configUrl)
            view.loadConfig()
        }
    }

    // endregion

}