package com.example.qrsmartreader.domain.interactors

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.util.Log
import com.example.qrsmartreader.domain.DataProcess
import com.example.qrsmartreader.ui.interactors.AiScanOnnxInteractor
import java.io.File
import java.io.FileOutputStream
import java.util.Collections


class AiScanOnnxInteractorImpl(context: Context) : AiScanOnnxInteractor {

    private lateinit var ortEnvironment: OrtEnvironment
    private lateinit var session: OrtSession

    init {
        load(context)
    }

    private fun load(context: Context) {
        loadPoseModel(context)

        ortEnvironment = OrtEnvironment.getEnvironment()
        session =
            ortEnvironment.createSession(
                context.filesDir.absolutePath.toString() + "/" + DataProcess.FILE_NAME,
                OrtSession.SessionOptions()
            )
    }

    private fun loadPoseModel(context: Context) {
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

    override fun findQrPoints(image: Bitmap): FloatArray? {
        val buffer = DataProcess.bitmapToFloatBuffer(image)
        val inputName = session.inputNames.iterator().next()

        val shape = longArrayOf(
            DataProcess.BATCH_SIZE.toLong(),
            DataProcess.PIXEL_SIZE.toLong(),
            DataProcess.IMAGE_INPUT_SIZE.toLong(),
            DataProcess.IMAGE_INPUT_SIZE.toLong()
        )
        val start = System.currentTimeMillis()
        val inputTensor = OnnxTensor.createTensor(ortEnvironment, buffer, shape)
        val resultTensor = session.run(Collections.singletonMap(inputName, inputTensor))
        val outputs = resultTensor[0].value as Array<*>
        val results = DataProcess.outputsToNPMSPredictions(outputs)
        val end = System.currentTimeMillis()
        if (results.isNotEmpty()) {
            val maxProbabilityPoints = results.maxByOrNull { it.getOrNull(4) ?: Float.MIN_VALUE }!!
            return floatArrayOf(
                maxProbabilityPoints[5], maxProbabilityPoints[6],
                maxProbabilityPoints[8], maxProbabilityPoints[9],
                maxProbabilityPoints[14], maxProbabilityPoints[15],
                maxProbabilityPoints[11], maxProbabilityPoints[12]
            )
        }
        return null
    }

    // Метод для выполнения перспективного преобразования
    override fun performPerspectiveTransformation(
        source: Bitmap,
        srcPoints: FloatArray
    ): Bitmap? {
        // Создаем матрицу для перспективного преобразования
        val matrix = Matrix()

        val dstPoints = floatArrayOf(
            40f,
            40f,
            source.width.toFloat(),
            40f,
            40f,
            source.height.toFloat(),
            source.width.toFloat(),
            source.height.toFloat()
        )

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