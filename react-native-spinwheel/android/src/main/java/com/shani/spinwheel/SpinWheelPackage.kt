package com.shani.spinwheel

import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager

/**
 * This class is the React Native package entry point
 * that registers the native Spin Wheel component with React Native.
 */
class SpinWheelPackage : ReactPackage {

    // region Override Methods

    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> {
        return emptyList()
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        return listOf(SpinWheelViewManager())
    }

    // endregion

}