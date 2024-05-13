package com.example.qrsmartreader

import android.content.res.AssetManager
import android.view.Surface

interface Yolov8NcnnInterface {

    fun clearQR()
    fun sendQRDataToCpp(): IntArray?
    fun qr_w(): Int
    fun qr_h(): Int


    fun loadModel(mgr: AssetManager?, modelid: Int, cpugpu: Int): Boolean
    fun openCamera(facing: Int, isFpsCounting: Boolean): Boolean
    fun closeCamera(): Boolean
    fun setOutputWindow(surface: Surface?): Boolean

}
