package com.example.qrsmartreader

import android.content.res.AssetManager
import android.util.Log
import android.view.Surface


class Yolov8NcnnPose : Yolov8NcnnInterface {

    override fun clearQR() {

    }

    external override fun sendQRDataToCpp(): IntArray?

    external override fun qr_w(): Int

    external override fun qr_h(): Int


    external override fun loadModel(mgr: AssetManager?, modelid: Int, cpugpu: Int, modelBin:String, modelParam: String): Boolean
    external override fun openCamera(facing: Int, isFpsCounting: Boolean): Boolean
    external override fun closeCamera(): Boolean
    external override fun setOutputWindow(surface: Surface?): Boolean

    companion object {
        init {
            System.loadLibrary("qrsmartreaderpose")
        }
    }

}

