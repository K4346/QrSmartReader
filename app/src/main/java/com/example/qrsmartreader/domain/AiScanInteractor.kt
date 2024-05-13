package com.example.qrsmartreader.domain

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.PointF
import android.util.Log
import com.example.yolov8n_pose.DataProcess
import java.io.File
import java.io.FileOutputStream
import java.util.*

class AiScanInteractor(context: Context) {

    private lateinit var ortEnvironment: OrtEnvironment
    private lateinit var session: OrtSession

    init {
        load(context)
    }
    private fun load(context:Context) {
        loadPoseModel(context)

        ortEnvironment = OrtEnvironment.getEnvironment()
        session =
            ortEnvironment.createSession(
                context.filesDir.absolutePath.toString() + "/" + DataProcess.FILE_NAME,
                OrtSession.SessionOptions()
            )
    }

    private fun loadPoseModel(context:Context) {
        val outputFile = File(context.filesDir.toString() + "/" + DataProcess.FILE_NAME)

        context.assets.open(DataProcess.FILE_NAME).use { inputStream ->
            FileOutputStream(outputFile).use { outputStream ->
                val buffer = ByteArray(4 * 1024)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
            }
        }
    }

    fun findQrPoints(image:Bitmap):FloatArray? {
        val buffer = DataProcess.bitmapToFloatBuffer(image)
        val inputName = session.inputNames.iterator().next()

        val shape = longArrayOf(
            DataProcess.BATCH_SIZE.toLong(),
            DataProcess.PIXEL_SIZE.toLong(),
            DataProcess.IMAGE_INPUT_SIZE.toLong(),
            DataProcess.IMAGE_INPUT_SIZE.toLong()
        )
        val inputTensor = OnnxTensor.createTensor(ortEnvironment, buffer, shape)
        val resultTensor = session.run(Collections.singletonMap(inputName, inputTensor))
        val outputs = resultTensor[0].value as Array<*>
        val results = DataProcess.outputsToNPMSPredictions(outputs)
        Log.i("kpop","${image.height} ${image.width}")
        if (results.isNotEmpty()){
            val maxProbabilityPoints = results.maxByOrNull { it.getOrNull(4) ?: Float.MIN_VALUE }!!
            Log.i("kpop",results[0].joinToString { "$it " })
            return floatArrayOf(
                maxProbabilityPoints[5],maxProbabilityPoints[6],
                maxProbabilityPoints[8], maxProbabilityPoints[9],
                maxProbabilityPoints[14], maxProbabilityPoints[15],
                maxProbabilityPoints[11], maxProbabilityPoints[12]
            )
        }
        return null
//        image = bitmap
//        val canvas = Canvas(image)
//        drawPointsAndLines(canvas, results)
//        Log.i("kpop", "coord "+imageView.getCurrentPointOfImageCoord(-5,PointF(imageView.points[0].x,imageView.points[0].y)))
//        if (results.isNotEmpty() && perspectiveFlag){
//            perspectiveFlag=false
//            var m = results[0]
//            if (results.size>1)
//            {
//                for (r in results) {
//                    if (r[4] > m[4]) {
//                        m = r
//                    }
//                }
//            }
//            val p1= imageView.getCurrentPointOfImageCoord(-5,PointF(imageView.points[0].x,imageView.points[0].y))
//            val p2= imageView.getCurrentPointOfImageCoord(-5,PointF(imageView.points[1].x,imageView.points[1].y))
//            val p3= imageView.getCurrentPointOfImageCoord(-5,PointF(imageView.points[2].x,imageView.points[2].y))
//            val p4= imageView.getCurrentPointOfImageCoord(-5,PointF(imageView.points[3].x,imageView.points[3].y))
//            val k1= floatArrayOf(p1.x,p1.y,p2.x,p2.y,p3.x,p3.y,p4.x,p4.y)
//            //  val k1= floatArrayOf(m[5],m[6],m[8],m[9],m[14],m[15],m[11],m[12])
////        k1[0]=74f
////        k1[1]=116f
////        k1[2]=297f
////        k1[3]=49f
////        k1[4]=96f
////        k1[5]=477f
////        k1[6]=310f
////        k1[7]=566f
//            val k2 = floatArrayOf(40f, 40f, 600f, 40f, 40f, 600f, 600f, 600f)
//
//
//            image=performPerspectiveTransformation(image,k1,k2)!!
//        }
//
//        runOnUiThread {
//            imageView.setImageBitmap(image)
//        }
//
//        decodeQRCode2(image)

    }

    // Метод для выполнения перспективного преобразования
    fun performPerspectiveTransformation(
        source: Bitmap,
        srcPoints: FloatArray
    ): Bitmap? {
        // Создаем матрицу для перспективного преобразования
        val matrix = Matrix()

//        todo сделать адаптивно
        val dstPoints = floatArrayOf(0f, 0f, source.width.toFloat(), 0f, 0f, source.height.toFloat(), source.width.toFloat(), source.height.toFloat())

        // Задаем исходные и целевые точки для перспективного преобразования
        matrix.setPolyToPoly(srcPoints, 0, dstPoints, 0, 4)

        val dstBitmap = Bitmap.createBitmap(source.width, source.height, source.config)
        val canvas = Canvas(dstBitmap)
        canvas.drawBitmap(source, matrix, null)
        // Применяем матрицу к исходному изображению
        return dstBitmap
//        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}